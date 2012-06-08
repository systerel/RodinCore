/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - implemented specialization
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.Set;

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
 * This class is not intended to be subclassed or instantiated directly by
 * clients. To create a new type, clients should use the methods provided by the
 * {@link FormulaFactory} class.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
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
		this.solved = solved;
	}

	// Build the expression corresponding to this type
	protected abstract Expression buildExpression(FormulaFactory factory);

	// Build the string image of this type
	protected abstract void buildString(StringBuilder buffer);

    /**
	 * Indicates whether the given object denotes the same type as this one.
	 * <p>
	 * Comparison is done using deep equality, not reference equality.
	 * </p>
	 * 
	 * @param obj
	 *            the object with which to compare
	 * @return <code>true</code> if this type denotes the same type as the obj
	 *         argument, <code>false</code> otherwise
	 */
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
	
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

	/**
	 * Returns the source type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha × beta)</code>, this method will
	 * return <code>alpha</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the source type of this type, or <code>null</code>
	 */
	public Type getSource() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return ((ProductType) baseType).getLeft();
			}
		}
		return null;
	}

	/**
	 * Returns the target type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha × beta)</code>, this method will
	 * return <code>beta</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the target type of this type, or <code>null</code>
	 */
	public Type getTarget() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return ((ProductType) baseType).getRight();
			}
		}
		return null;
	}

	/**
	 * Returns whether this type is a relational type.
	 * <p>
	 * This type is relational if it looks like <code>ℙ(alpha × beta)</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this type is a relational type.
	 */
	protected boolean isRelational() {
		if (this instanceof PowerSetType) {
			Type baseType = ((PowerSetType) this).getBaseType();
			if (baseType instanceof ProductType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the base type of this type, if any.
	 * <p>
	 * If this type looks like <code>ℙ(alpha)</code>, this method will
	 * return <code>alpha</code>, otherwise <code>null</code>.
	 * </p>
	 * 
	 * @return the base type of this type, or <code>null</code>.
	 */
	public Type getBaseType() {
		return null;
	}

	protected abstract void addGivenTypes(Set<GivenType> set);

	/**
	 * Uses the given specialization to get a new specialization of this type.
	 * 
	 * @since 2.6
	 */
	public abstract Type specialize(ISpecialization specialization);

}
