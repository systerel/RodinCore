/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 *
 */
public class ArgParametricType extends ArgumentType {

	private static final List<Predicate> NO_PREDS = emptyList();

	private final IExpressionExtension typeConstr;
	private final List<ArgumentType> argTypes;

	public ArgParametricType(IExpressionExtension typeConstr, List<ArgumentType> argTypes) {
		this.typeConstr = typeConstr;
		this.argTypes = argTypes;
	}

	@Override
	public Type toType(ITypeMediator mediator, TypeInstantiation instantiation) {
		final List<Type> argTypesInst = new ArrayList<Type>();
		for (ArgumentType arg : argTypes) {
			final Type argType = arg.toType(mediator, instantiation);
			argTypesInst.add(argType);
		}
		return mediator.makeParametricType(argTypesInst, typeConstr);
	}

	@Override
	public boolean verifyType(Type proposedType, TypeInstantiation instantiation) {
		if (!(proposedType instanceof ParametricType)) {
			return false;
		}
		final ParametricType genType = (ParametricType) proposedType;
		if (genType.getExprExtension() != typeConstr) {
			return false;
		}
		final Type[] typeParams = genType.getTypeParameters();
		assert typeParams.length == argTypes.size();
		for (int i = 0; i < typeParams.length; i++) {
			if (!argTypes.get(i).verifyType(typeParams[i], instantiation)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Expression toSet(FormulaFactory fac,
			Map<ITypeParameter, Expression> substitution) {
		final List<Expression> argSets = new ArrayList<Expression>();
		for (final ArgumentType argType : argTypes) {
			final Expression argSet = argType.toSet(fac, substitution);
			argSets.add(argSet);
		}
		return fac.makeExtendedExpression(typeConstr, argSets, NO_PREDS, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argTypes == null) ? 0 : argTypes.hashCode());
		result = prime * result
				+ ((typeConstr == null) ? 0 : typeConstr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ArgParametricType)) {
			return false;
		}
		ArgParametricType other = (ArgParametricType) obj;
		if (argTypes == null) {
			if (other.argTypes != null) {
				return false;
			}
		} else if (!argTypes.equals(other.argTypes)) {
			return false;
		}
		if (typeConstr == null) {
			if (other.typeConstr != null) {
				return false;
			}
		} else if (!typeConstr.equals(other.typeConstr)) {
			return false;
		}
		return true;
	}
	
	
}
