/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added support for specialization
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import static org.eventb.internal.core.ast.FreshNameSolver.solve;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.Specialization;

/**
 * This class represents a type environment used to type check an event-B
 * formula.
 * <p>
 * A type environment is a map from names to their respective type.
 * </p>
 * 
 * @author François Terrier
 */
public class TypeEnvironment implements Cloneable, ITypeEnvironment {

	static final class InternalIterator implements IIterator {

		Iterator<Map.Entry<String, Type>> iterator;

		Map.Entry<String, Type> current;

		public InternalIterator(Iterator<Map.Entry<String, Type>> iterator) {
			this.iterator = iterator;
			this.current = null;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public void advance() throws NoSuchElementException {
			current = iterator.next();
		}

		@Override
		public String getName() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getKey();
		}

		@Override
		public Type getType() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getValue();
		}

		@Override
		public boolean isGivenSet() throws NoSuchElementException {
			final Type baseType = getType().getBaseType();
			if (baseType instanceof GivenType) {
				GivenType givenType = (GivenType) baseType;
				return givenType.getName().equals(getName());
			}
			return false;
		}
	}

	public final FormulaFactory ff;

	// implementation
	private Map<String, Type> map;

	/**
	 * Constructs an initially empty type environment.
	 */
	public TypeEnvironment(FormulaFactory ff) {
		this.ff = ff;
		this.map = new HashMap<String, Type>();
	}

	/**
	 * Constructs a new type environment with the same map as the given one.
	 * 
	 * @param typenv
	 *            type environment to copy
	 */
	public TypeEnvironment(TypeEnvironment typenv) {
		this.ff = typenv.ff;
		this.map = new HashMap<String, Type>(typenv.map);
	}

	@Override
	public void add(FreeIdentifier freeIdent) {
		addName(freeIdent.getName(), freeIdent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#addAll(org.eventb.core.ast.
	 * ITypeEnvironment)
	 */
	@Override
	public void addAll(ITypeEnvironment other) {
		Map<String, Type> otherMap = ((TypeEnvironment) other).map;
		// Use addName() to check for duplicates.
		for (Entry<String, Type> entry : otherMap.entrySet()) {
			addName(entry.getKey(), entry.getValue());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#addAll(org.eventb.core.ast.
	 * FreeIdentifier[])
	 */
	@Override
	public void addAll(FreeIdentifier[] freeIdents) {
		for (FreeIdentifier freeIdent : freeIdents) {
			add(freeIdent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#addGivenSet(java.lang.String)
	 */
	@Override
	public void addGivenSet(String name) {
		addName(name, ff.makePowerSetType(ff.makeGivenType(name)));
	}

	public void addGivenSet(GivenType type) {
		addName(type.getName(), ff.makePowerSetType(type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#addName(java.lang.String,
	 * org.eventb.core.ast.Type)
	 */
	@Override
	public void addName(String name, Type type) {
		assert name != null && type != null;
		Type oldType = map.get(name);
		assert oldType == null || oldType.equals(type);
		map.put(name, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ITypeEnvironment clone() {
		return new TypeEnvironment(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String name) {
		return map.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.ast.ITypeEnvironment#containsAll(org.eventb.internal.
	 * core.typecheck.TypeEnvironment)
	 */
	@Override
	public boolean containsAll(ITypeEnvironment typenv) {
		if (this == typenv)
			return true;
		final TypeEnvironment other = (TypeEnvironment) typenv;
		for (Entry<String, Type> entry : other.map.entrySet()) {
			String name = entry.getKey();
			if (!entry.getValue().equals(this.getType(name)))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof TypeEnvironment) {
			TypeEnvironment temp = (TypeEnvironment) obj;
			return map.equals(temp.map);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#getIterator()
	 */
	@Override
	public IIterator getIterator() {
		return new InternalIterator(map.entrySet().iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#getNames()
	 */
	@Override
	public Set<String> getNames() {
		return map.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#getType(java.lang.String)
	 */
	@Override
	public Type getType(String name) {
		return map.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ast.ITypeEnvironment#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	public GivenType makeFreshGivenType(String name) {
		final String solvedName = solve(this, name);
		final GivenType result = ff.makeGivenType(solvedName);
		addGivenSet(result);
		return result;
	}

	public FreeIdentifier makeFreshFreeIdentifier(String name,
			SourceLocation sourceLocation, Type type) {
		assert type != null;
		final String solvedName = solve(this, name);
		final FreeIdentifier result = ff.makeFreeIdentifier(solvedName,
				sourceLocation, type);
		addName(solvedName, result.getType());
		return result;
	}
	
	// solves the unknown types (names who have type variable as their
	// corresponding type).
	public void solveVariables(TypeUnifier unifier) {
		for (Entry<String, Type> element : map.entrySet()) {
			element.setValue(unifier.solve(element.getValue()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return ff;
	}

	@Override
	public ITypeEnvironment specialize(ISpecialization specialization) {
		final Specialization spec = (Specialization) specialization;
		final TypeEnvironment typeEnv = getGivenTypesOfSpecialisation(spec);
		final ITypeEnvironment.IIterator iter = this.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			if (iter.isGivenSet()) {
				final Type type = iter.getType();
				final GivenType given = (GivenType) type.getBaseType();
				if (spec.get(given) == given) {
					typeEnv.addName(iter.getName(), type);
				} // else the type is specialized thus disappears
			} else {
				final FreeIdentifier ident = ff.makeFreeIdentifier(
						iter.getName(), null, iter.getType());
				final Expression expr = spec.get(ident);
				if (expr instanceof FreeIdentifier) {
					final FreeIdentifier freeIdent = (FreeIdentifier) expr;
					if (typeEnv.contains(freeIdent.getName())) {
					throw new IllegalArgumentException(expr
							+ " can not be a type and not a type.");
					}
					typeEnv.add(freeIdent);					
				}
			}
		}
		return typeEnv;
	}

	/**
	 * Returns a new type environment carrying the given types introduced by the
	 * specialization.
	 * 
	 * @param specialization
	 *            the specialization potentially introducing new given types
	 * @return the augmented type environment with the given types
	 */
	private TypeEnvironment getGivenTypesOfSpecialisation(
			ISpecialization specialization) {
		final Specialization spec = (Specialization) specialization;
		final TypeEnvironment typeEnv = new TypeEnvironment(ff);
		final Set<GivenType> newTypes = new HashSet<GivenType>();
		final Collection<Type> sTypes = spec.getSubstitutionTypes();
		for (Type type : sTypes) {
			newTypes.addAll(type.getGivenTypes());
		}
		for (GivenType g : newTypes) {
			typeEnv.addGivenSet(g);
		}
		return typeEnv;
	}

}
