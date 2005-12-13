/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

/**
 * Common protocol for event-B types.
 * <p>
 * In event-B, once type-checked, every expression has an associated type. This
 * type statically determines the set of all values the expression can take.
 * Hence, the type of an expression corresponds to the largest set to which the
 * expression can belong.
 * </p>
 * <p>
 * For implementation reasons, types are not represented as expressions but have
 * their own set of classes.
 * </p>
 * <p>
 * This class is not intended to be subclassed or instanciated directly by
 * clients. To create a new type, clients should use the methods provided by the
 * {@link FormulaFactory} class.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class Type {

	// Lazy cache for the equivalent expression
	private Expression expr = null;

	// Factory used to build the equivalent expression
	private FormulaFactory exprFactory = null;
	
	// True if this type doesn't contain any type variable
	private final boolean solved; 

	// Disabled default constructor
	protected Type(boolean solved) {
		super();
		this.solved = solved;
	}

	// Build the expression corresponding to this type
	protected abstract Expression buildExpression(FormulaFactory factory);

	// Build the string image of this type
	protected abstract void buildString(StringBuilder buffer);

	/**
	 * Returns whether this type is non-generic (doesn't contain any type variables).
	 * 
	 * @return <code>true</code> iff this type is non-generic
	 */
	public boolean isSolved() {
		return solved;
	}

	/**
	 * Returns the expression that denotes the set corresponding to this type.
	 * 
	 * @param factory
	 *            factory to use for building the result expression
	 * @return the set corresponding to this type
	 */
	public Expression toExpression(FormulaFactory factory) {
		if (expr == null || exprFactory != factory) {
			expr = buildExpression(factory);
			exprFactory = factory;
		}
		return expr;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buildString(buffer);
		return buffer.toString();
	}

}
