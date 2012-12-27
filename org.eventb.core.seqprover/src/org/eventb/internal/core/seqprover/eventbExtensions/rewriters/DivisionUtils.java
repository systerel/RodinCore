/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
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
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public class DivisionUtils {

	private DLib dLib;

	public DivisionUtils(DLib lib) {
		dLib = lib;
	}

	public Expression getFaction(Expression E, Expression F) {
		return dLib.getFormulaFactory().makeBinaryExpression(Expression.DIV, E,
				F, null);
	}

	public Expression getFaction(Expression expression, BigInteger E,
			Expression F) {
		switch (E.signum()) {
		case -1:
			final Expression minusE = negate(E);
			return dLib.getFormulaFactory().makeBinaryExpression(
					Expression.DIV, minusE, F, null);
		case 0:
			return dLib.getFormulaFactory().makeIntegerLiteral(E, null);
		default:
			return expression;
		}
	}

	public Expression getFaction(Expression expression, Expression E,
			BigInteger F) {
		if (F.signum() < 0) {
			final Expression minusF = negate(F);
			return dLib.getFormulaFactory().makeBinaryExpression(
					Expression.DIV, E, minusF, null);
		}
		if (F.equals(BigInteger.ONE)) {
			return dLib.getFormulaFactory().makeUnaryExpression(
					Expression.UNMINUS, E, null);
		}
		return expression;
	}

	private IntegerLiteral negate(BigInteger value) {
		return dLib.getFormulaFactory()
				.makeIntegerLiteral(value.negate(), null);
	}

	public Expression getFaction(Expression expression, BigInteger E,
			BigInteger F) {
		if (E.signum() == 0)
			return dLib.getFormulaFactory().makeIntegerLiteral(BigInteger.ZERO,
					null);
		if (F.equals(BigInteger.ONE)) {
			return dLib.getFormulaFactory().makeIntegerLiteral(E, null);
		}
		if (E.signum() < 0 && F.signum() < 0) {
			final Expression minusE = negate(E);
			final Expression minusF = negate(F);
			return dLib.getFormulaFactory().makeBinaryExpression(
					Expression.DIV, minusE, minusF, null);
		}
		return expression;
	}
}
