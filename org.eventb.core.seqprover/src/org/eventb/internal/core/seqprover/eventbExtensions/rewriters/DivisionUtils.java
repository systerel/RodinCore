/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;

public class DivisionUtils {

	public static Expression getFaction(Expression E, Expression F) {
		return E.getFactory().makeBinaryExpression(Expression.DIV, E, F, null);
	}

	public static Expression getFaction(Expression expression, BigInteger E,
			Expression F) {
		FormulaFactory ff = expression.getFactory();
		switch (E.signum()) {
		case -1:
			final Expression minusE = negate(ff, E);
			return ff.makeBinaryExpression(Expression.DIV, minusE, F, null);
		case 0:
			return ff.makeIntegerLiteral(E, null);
		default:
			return expression;
		}
	}

	public static Expression getFaction(Expression expression, Expression E,
			BigInteger F) {
		FormulaFactory ff = expression.getFactory();
		if (F.signum() < 0) {
			final Expression minusF = negate(ff, F);
			return ff.makeBinaryExpression(
					Expression.DIV, E, minusF, null);
		}
		if (F.equals(BigInteger.ONE)) {
			return ff.makeUnaryExpression(
					Expression.UNMINUS, E, null);
		}
		return expression;
	}

	private static IntegerLiteral negate(FormulaFactory ff, BigInteger value) {
		return ff.makeIntegerLiteral(value.negate(), null);
	}

	public static Expression getFaction(Expression expression, BigInteger E,
			BigInteger F) {
		FormulaFactory ff = expression.getFactory();
		if (E.signum() == 0)
			return ff.makeIntegerLiteral(BigInteger.ZERO,
					null);
		if (F.equals(BigInteger.ONE)) {
			return ff.makeIntegerLiteral(E, null);
		}
		if (E.signum() < 0 && F.signum() < 0) {
			final Expression minusE = negate(ff, E);
			final Expression minusF = negate(ff, F);
			return ff.makeBinaryExpression(
					Expression.DIV, minusE, minusF, null);
		}
		return expression;
	}
}
