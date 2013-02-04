/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.Formula.combineHashCodes;

import java.util.Arrays;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Implementation of an instance of a parametric type contributed by a math
 * extension. A parametric type is composed of a type extension together with a
 * (possibly empty) list of type parameters.
 * 
 * @author Nicolas Beauger
 * @author Laurent Voisin
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ParametricType extends Type {

	private static final Predicate[] NO_PRED = new Predicate[0];

	private static boolean isSolved(Type[] typeParameters) {
		for (Type type : typeParameters) {
			if (!type.isSolved()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private static Expression[] buildExprs(Type[] typeParams,
			FormulaFactory factory) {
		final int length = typeParams.length;
		final Expression[] result = new Expression[length];
		for (int i = 0; i < length; i++) {
			result[i] = typeParams[i].toExpression(factory);
		}
		return result;
	}

	private final IExpressionExtension typeConstructor;
	private final Type[] typeParameters;

	/**
	 * Must never be called directly: use the factory method instead.
	 * 
	 * @see FormulaFactory#makeParametricType(Type[], IExpressionExtension)
	 * @see FormulaFactory#makeParametricType(java.util.List, IExpressionExtension)
	 * @since 3.0
	 */
	// The array of type parameters must have been built by a formula factory
	// without any reference leaked outside
	protected ParametricType(FormulaFactory ff,
			IExpressionExtension typeConstructor, Type[] typeParameters) {
		super(ff, isSolved(typeParameters));
		if (!typeConstructor.isATypeConstructor()) {
			throw new IllegalArgumentException("Invalid type constructor "
					+ typeConstructor.getId());
		}
		this.typeParameters = typeParameters;
		this.typeConstructor = typeConstructor;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for (Type type : typeParameters) {
			type.addGivenTypes(set);
		}
	}

	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		return factory.makeExtendedExpression(typeConstructor,
				buildExprs(typeParameters, factory), NO_PRED, null,
				factory.makePowerSetType(this));
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(typeConstructor.getSyntaxSymbol());
		if (typeParameters.length == 0) {
			return;
		}
		char sep = '(';
		for (Type param : typeParameters) {
			buffer.append(sep);
			sep = ',';
			param.buildString(buffer);
		}
		buffer.append(')');
	}

	public Type[] getTypeParameters() {
		return typeParameters.clone();
	}

	public IExpressionExtension getExprExtension() {
		return typeConstructor;
	}

	// FIXME using a client implemented interface IExpressionExtension
	// for equals and hashCode => use == instead

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !this.getClass().equals(obj.getClass()))
			return false;
		final ParametricType other = (ParametricType) obj;
		return this.typeConstructor.equals(other.typeConstructor)
				&& Arrays.equals(this.typeParameters, other.typeParameters);
	}

	@Override
	public int hashCode() {
		return combineHashCodes(typeConstructor.hashCode(),
				combineHashCodes(typeParameters));
	}

	@Override
	public void accept(ITypeVisitor visitor) {
		visitor.visit(this);
	}

}
