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
 * Denotes a product type.
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ProductType extends Type {
	
	// First component of this type
	private final Type left;

	// Second component of this type
	private final Type right;

	/**
	 * Creates a new instance of this type.
	 */
	protected ProductType(Type left, Type right) {
		super(left.isSolved() && right.isSolved());
		this.left = left;
		this.right = right;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		left.addGivenTypes(set);
		right.addGivenTypes(set);
	}
	
	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		Expression leftExpr = left.toExpression(factory);
		Expression rightExpr = right.toExpression(factory);
		return factory.makeBinaryExpression(Formula.CPROD, leftExpr, rightExpr, null);
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		left.buildString(buffer);
		
		buffer.append('\u00d7');
		
		final boolean rightNeedsParen = (right instanceof ProductType);
		if (rightNeedsParen) buffer.append('(');
		right.buildString(buffer);
		if (rightNeedsParen) buffer.append(')');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (! (o instanceof ProductType)) return false;
		ProductType other = (ProductType) o;
		return left.equals(other.left) && right.equals(other.right);
	}
	
	/**
	 * Returns the first component of this type.
	 * 
	 * @return Returns the first component of this type
	 */
	public Type getLeft() {
		return left;
	}

	/**
	 * Returns the second component of this type.
	 * 
	 * @return Returns the second component of this type
	 */
	public Type getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() * 17 + right.hashCode();
	}

	@Override
	public Type specialize(ISpecialization specialization) {
		return new ProductType(left.specialize(specialization),
				right.specialize(specialization));
	}
	
}
