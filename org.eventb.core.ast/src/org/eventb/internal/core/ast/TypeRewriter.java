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
package org.eventb.internal.core.ast;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Common implementation of classes that transform Event-B types. This
 * implementation by itself computes the exact same type. It is up to
 * sub-classes to tailor the rewriting to their own needs. In addition, this
 * class ensures that in case no rewriting occurred, the same object reference
 * is returned rather than a deep copy of it.
 * <p>
 * The programming principle of this class is that each <code>visit()</code>
 * method stores its result in the <code>result</code> variable and performs
 * recursive calls by calling the <code>rewrite()</code> method.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class TypeRewriter implements ITypeVisitor {

	// Formula factory to use for building the rewrited types
	protected final FormulaFactory ff;

	// Result of the last call to visit()
	protected Type result;

	public TypeRewriter(FormulaFactory ff) {
		this.ff = ff;
	}

	public Type rewrite(Type type) {
		type.accept(this);
		return result;
	}

	@Override
	public void visit(BooleanType type) {
		if (ff == type.getFactory()) {
			result = type;
		} else {
			result = ff.makeBooleanType();
		}
	}

	@Override
	public void visit(GivenType type) {
		if (ff == type.getFactory()) {
			result = type;
		} else {
			result = ff.makeGivenType(type.getName());
		}
	}

	@Override
	public void visit(IntegerType type) {
		if (ff == type.getFactory()) {
			result = type;
		} else {
			result = ff.makeIntegerType();
		}
	}

	@Override
	public void visit(ParametricType type) {
		boolean changed = false;
		final Type[] typeParameters = type.getTypeParameters();
		final Type[] newTypeParameters = new Type[typeParameters.length];
		for (int i = 0; i < typeParameters.length; i++) {
			newTypeParameters[i] = rewrite(typeParameters[i]);
			changed |= newTypeParameters[i] != typeParameters[i];
		}
		final IExpressionExtension constructor = type.getExprExtension();
		if (changed)
			result = ff.makeParametricType(newTypeParameters, constructor);
		else
			result = type;
	}

	@Override
	public void visit(PowerSetType type) {
		final Type base = type.getBaseType();
		final Type newBase = rewrite(base);
		if (newBase == base)
			result = type;
		else
			result = ff.makePowerSetType(newBase);
	}

	@Override
	public void visit(ProductType type) {
		final Type left = type.getLeft();
		final Type right = type.getRight();
		final Type newLeft = rewrite(left);
		final Type newRight = rewrite(right);
		if (newLeft == left && newRight == right)
			result = type;
		else
			result = ff.makeProductType(newLeft, newRight);
	}

}
