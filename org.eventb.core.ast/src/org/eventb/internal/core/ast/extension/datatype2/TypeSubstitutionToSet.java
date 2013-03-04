/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype2;

import static java.util.Collections.emptyList;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.POW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeVisitor;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Substitutes the datatype type and its type parameters representation by the
 * sets provided .
 * 
 * @author Vincent Monfort
 */
public class TypeSubstitutionToSet implements ITypeVisitor {

	private static final List<Predicate> NO_PREDS = emptyList();

	// Formula factory to use for building the rewrited types
	private final FormulaFactory ff;

	// Result of the last call to visit()
	protected Expression result;

	private final Map<GivenType, Expression> substitutions;

	public TypeSubstitutionToSet(FormulaFactory ff,
			Map<GivenType, Expression> instantiated) {
		this.ff = ff;
		this.substitutions = instantiated;
	}

	public Expression toSet(Type type) {
		if (type == null) {
			return null;
		}
		result = null;
		type.accept(this);
		return result;
	}

	@Override
	public void visit(BooleanType type) {
		result = type.translate(ff).toExpression();
	}

	@Override
	public void visit(GivenType type) {
		result = substitutions.get(type);
	}

	@Override
	public void visit(IntegerType type) {
		result = type.translate(ff).toExpression();
	}

	@Override
	public void visit(ParametricType type) {
		final List<Expression> argSets = new ArrayList<Expression>();
		for (final Type argType : type.getTypeParameters()) {
			final Expression argSet = toSet(argType);
			argSets.add(argSet);
		}
		result = ff.makeExtendedExpression(type.getExprExtension(), argSets,
				NO_PREDS, null);
	}

	@Override
	public void visit(PowerSetType type) {
		final Expression argSet = toSet(type.getBaseType());
		result = ff.makeUnaryExpression(POW, argSet, null);
	}

	@Override
	public void visit(ProductType type) {
		final Expression leftSet = toSet(type.getLeft());
		final Expression rightSet = toSet(type.getRight());
		result = ff.makeBinaryExpression(CPROD, leftSet, rightSet, null);
	}
}
