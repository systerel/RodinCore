/*******************************************************************************
 * Copyright (c) 2012, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation 
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import java.util.LinkedList;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Type;

/**
 * This class represents an inferred type environment generated during type
 * checking and based on an initial type environment. It extends TypeEnvironment
 * class and thus contains only methods necessary to differentiate those
 * classes.
 * <p>
 * A inferred type environment is a map from names to their respective type that
 * is based on an initial type environment. As a consequence it does not contain
 * values already contained in its initial type environment.
 * </p>
 * 
 * <p>
 * All value access methods return only values from inferred environment and all
 * value set methods add values in inferred environment only if they do not
 * exist in initial environment.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class InferredTypeEnvironment extends TypeEnvironment implements
		IInferredTypeEnvironment {

	private ITypeEnvironment initialTypeEnvironment;

	public InferredTypeEnvironment(ITypeEnvironment intialTypenv) {
		super(intialTypenv.getFormulaFactory());
		this.initialTypeEnvironment = intialTypenv;
	}

	public InferredTypeEnvironment(InferredTypeEnvironment inferredTypEnv) {
		super(inferredTypEnv);
		this.initialTypeEnvironment = inferredTypEnv.initialTypeEnvironment
				.clone();
	}

	@Override
	public ITypeEnvironment getInitialTypeEnvironment() {
		return this.initialTypeEnvironment;
	}

	/**
	 * Get the type registered for the given name searching first in initial
	 * environment and then in inferred environment.
	 * 
	 * @param name
	 *            the name to lookup
	 * @return the type associated to the given name in initial environment or
	 *         inferred environment or <code>null</code> if it is not in this
	 *         type environment.
	 */
	private Type getTypeInBoth(String name) {
		Type type = this.initialTypeEnvironment.getType(name);
		if (type == null) {
			this.getType(name);
		}
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#addName(java.lang.String,
	 * org.eventb.core.ast.Type)
	 */
	@Override
	public void addName(String name, Type type) {
		// Same code than in TypeEnvironment but using getTypeInBoth
		if (name == null) {
			throw new NullPointerException("Null name");
		} else {
			if (!this.getFormulaFactory().isValidIdentifierName(name)) {
				throw new IllegalArgumentException(name
						+ " is an invalid identifier name in current language");
			}
		}
		if (type == null) {
			throw new NullPointerException("Null type");
		}

		Type oldType = getTypeInBoth(name);

		if (oldType != null && !oldType.equals(type)) {
			throw new IllegalArgumentException(
					"Trying to register an existing name with a different type");
		} else if (oldType == null) {
			setName(name, type);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.typecheck.TypeEnvironment#setName(java.lang.
	 * String, org.eventb.core.ast.Type)
	 */
	private void setName(String name, Type type) {
		// Same code than in TypeEnvironment but using getTypeInBoth
		if (type.isSolved()) {
			// Check if it is a new given set
			boolean is_new_given_set = false;
			if (type instanceof PowerSetType) {
				PowerSetType powerSetType = (PowerSetType) type;
				Type baseType = powerSetType.getBaseType();
				if (baseType instanceof GivenType) {
					GivenType givenType = (GivenType) baseType;
					is_new_given_set = givenType.getName().equals(name);
				}
			}
			// Allowing to declare a new GivenSet type
			if (!is_new_given_set) {
				for (GivenType givenType : type.getGivenTypes()) {
					String mustBeGivenType = givenType.getName();
					Type knownType = this.getTypeInBoth(mustBeGivenType);
					if (knownType == null) {
						// If given set does not exist add it
						addGivenSet(mustBeGivenType);
					} else {
						// The already known type must be a given set
						Type baseType = knownType.getBaseType();
						if (baseType == null || !baseType.equals(givenType)) {
							throw new IllegalArgumentException(
									"Trying to register " + name
											+ " with the type " + type
											+ " whereas " + mustBeGivenType
											+ "is already defined as "
											+ knownType);
						}
					}
				}
			}
		}

		map.put(name, type);
	}

	// solves the unknown types (names who have type variable as their
	// corresponding type).
	public void solveVariables(TypeUnifier unifier) {
		// In order to add unknown given sets we need to set solved types
		// (modify the map)
		// outside of the keySet iteration
		LinkedList<String> keys = new LinkedList<String>();
		for (String key : map.keySet()) {
			keys.add(key);
		}
		for (String key : keys) {
			setName(key, unifier.solve(map.get(key)));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			InferredTypeEnvironment inferredTypEnv = (InferredTypeEnvironment) obj;
			return this.initialTypeEnvironment
					.equals(inferredTypEnv.initialTypeEnvironment)
					&& super.equals(obj);
		}
		return false;
	}

	@Override
	public ITypeEnvironment clone() {
		return new InferredTypeEnvironment(this);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + initialTypeEnvironment.hashCode();
	}

}
