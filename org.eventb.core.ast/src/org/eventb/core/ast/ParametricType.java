/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * @author Nicolas Beauger
 * @since 2.0
 *
 */
public class ParametricType extends Type {
	
	private static boolean isSolved(List<Type> typeParameters) {
		for (Type type : typeParameters) {
			if (!type.isSolved()) {
				return false;
			}
		}
		return true;
	}

	private static List<Expression> buildExprs(List<Type> typeParams,
			FormulaFactory factory) {
		final List<Expression> result = new ArrayList<Expression>(typeParams
				.size());
		for (Type type : typeParams) {
			result.add(type.buildExpression(factory));
		}
		return result;
	}

	private final List<Type> typeParameters;
	private final IExpressionExtension exprExtension;

	protected ParametricType(List<Type> typeParameters,
			IExpressionExtension exprExtension) {
		super(isSolved(typeParameters));
		assert exprExtension.isATypeConstructor();
		this.typeParameters = new ArrayList<Type>(typeParameters);
		this.exprExtension = exprExtension;
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		for(Type type: typeParameters) {
			type.addGivenTypes(set);
		}
	}

	@Override
	protected Expression buildExpression(FormulaFactory factory) {
		final List<Expression> exprs = buildExprs(typeParameters, factory);
		final List<Predicate> preds = Collections.<Predicate>emptyList();
		return factory
				.makeExtendedExpression(exprExtension, exprs, preds, null);
	}

	@Override
	protected void buildString(StringBuilder buffer) {
		buffer.append(exprExtension.getSyntaxSymbol());
		if (typeParameters.isEmpty()) {
			return;
		}
		buffer.append('(');
		buffer.append(typeParameters.get(0));
		for (int i = 1; i < typeParameters.size(); i++) {
			buffer.append(',');
			buffer.append(typeParameters.get(i));
		}
		buffer.append(')');
	}

	public Type[] getTypeParameters() {
		return typeParameters.toArray(new Type[typeParameters.size()]);
	}
	
	public IExpressionExtension getExprExtension() {
		return exprExtension;
	}
	
	// FIXME using a client implemented interface IExpressionExtension
	// for equals and hashCode => use == instead
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ParametricType))
			return false;
		final ParametricType other = (ParametricType) obj;
		return exprExtension.equals(other.exprExtension)
				&& typeParameters.equals(other.typeParameters);
	}

	@Override
	public int hashCode() {
		final int extHash = exprExtension.hashCode();
		final int typePrmHash = combineHashCodes(typeParameters);
		return combineHashCodes(extHash, typePrmHash);
	}

	// FIXME code below is an adapted copy from Formula
	/**
	 * Returns the combination of two hash codes.
	 * 
	 * @param hash1
	 *            a hash code
	 * @param hash2
	 *            another hash code
	 * @return a combination of the two hash codes
	 */
	private static int combineHashCodes(int hash1, int hash2) {
		return hash1 * 17 + hash2;
	}

	/**
	 * Returns the combination of some types' hash codes.
	 * 
	 * @param types
	 *            some types
	 * @return a combination of the types' hash codes
	 */
	private static <T extends Type> int combineHashCodes(
			Collection<? extends T> types) {
		int result = 0;
		for (T formula: types) {
			result = combineHashCodes(result, formula.hashCode());
		}
		return result;
	}
	

}
