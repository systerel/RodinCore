/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import static org.eventb.internal.core.ast.GivenTypeHelper.isGivenSet;

import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.FreshNameSolver;

/**
 * Mutable sub-class of {@link TypeEnvironment} providing methods for changing
 * the underlying map.
 * 
 * All changes to the underlying map must go through
 * {@link #setName(String, Type)} to ensure consistency.
 * 
 * @author Vincent Monfort
 */
public class TypeEnvironmentBuilder extends TypeEnvironment implements ITypeEnvironmentBuilder{

	/**
	 * Constructs an initially empty type environment.
	 */
	public TypeEnvironmentBuilder(FormulaFactory ff) {
		super(ff);
	}

	/**
	 * Constructs a new type environment with a copy of the map of the given one.
	 * 
	 * @param typenv
	 *            type environment to copy
	 */
	public TypeEnvironmentBuilder(TypeEnvironment typenv) {
		super(typenv);
	}
	
	@Override
	public void add(FreeIdentifier freeIdent) {
		addName(freeIdent.getName(), freeIdent.getType());
	}

	@Override
	public void addAll(ITypeEnvironment other) {
		if (other.getFormulaFactory() != ff) {
			throw new IllegalArgumentException("Incompatible formula factory: "
					+ other.getFormulaFactory() + ", should be: " + ff);
		}
		final Map<String, Type> otherMap = ((TypeEnvironment) other).map;
		// Use addName() to check for duplicates.
		for (final Entry<String, Type> entry : otherMap.entrySet()) {
			addName(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void addAll(FreeIdentifier[] freeIdents) {
		for (final FreeIdentifier freeIdent : freeIdents) {
			add(freeIdent);
		}
	}

	@Override
	public void addGivenSet(String name) {
		addName(name, ff.makePowerSetType(ff.makeGivenType(name)));
	}

	private void addGivenSet(GivenType type) {
		addName(type.getName(), ff.makePowerSetType(type));
	}

	/**
	 * Sets a name and its specified type in the type environment without
	 * considering previous value. All given sets occurring in the given type
	 * are automatically added (which can produce errors if incompatible).
	 * 
	 * @param name the name to add
	 * @param type the type to associate to the given name
	 */
	protected void setName(String name, Type type) {
		// Avoid infinite recursion when adding a given set
		if (!isGivenSet(name, type)) {
			for (final GivenType givenType : type.getGivenTypes()) {
				addGivenSet(givenType);
			}
		}
		map.put(name, type);
	}

	@Override
	public void addName(String name, Type type) {
		if (name == null) {
			throw new NullPointerException("Null name");
		}
		if (!this.getFormulaFactory().isValidIdentifierName(name)) {
			throw new IllegalArgumentException(name
					+ " is an invalid identifier name in current language");
		}
		if (type == null) {
			throw new NullPointerException("Null type");
		}
		if (type.getFactory() != ff) {
			throw new IllegalArgumentException("Invalid formula factory for ("
					+ name + " : " + type + "): " + type.getFactory()
					+ ", should be: " + ff);
		}
		final Type oldType = internalGetType(name);
		if (oldType != null && !oldType.equals(type)) {
			throw new IllegalArgumentException(
					"Trying to register an existing name with a different type");
		}
		if (oldType == null) {
			setName(name, type);
		}
	}

	/*
	 * Internal method for enquiring about the type already associated to a
	 * name. Subclasses should override.
	 */
	protected Type internalGetType(String name) {
		return map.get(name);
	}
	
	private FreeIdentifier makeFreshIdentifier(BoundIdentDecl bIdent,
			FreshNameSolver solver) {
		final String bName = bIdent.getName();
		final String fName = solver.solve(bName);
		final Type type = bIdent.getType();
		addName(fName, type);
		final SourceLocation sloc = bIdent.getSourceLocation();
		return ff.makeFreeIdentifier(fName, sloc, type);
	}
	
	@Override
	public FreeIdentifier[] makeFreshIdentifiers(BoundIdentDecl[] bIdents) {
		final int nbBoundIdentDecl = bIdents.length;
		final FreeIdentifier[] result = new FreeIdentifier[nbBoundIdentDecl];
		final FreshNameSolver solver = new FreshNameSolver(this);
		for (int i = 0; i < nbBoundIdentDecl; i++) {
			result[i] = makeFreshIdentifier(bIdents[i], solver);
		}
		return result;
	}

	@Override
	public ISealedTypeEnvironment makeSnapshot() {
		return new SealedTypeEnvironment(this);
	}

}
