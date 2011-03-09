/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.MultiplicationSimplifier;
import org.junit.Test;

public class MultiplicationSimplifierTests {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static AssociativeExpression mul(Expression... children) {
		return ff.makeAssociativeExpression(MUL, children, null);
	}

	private static IntegerLiteral lit(long value) {
		final BigInteger bi = BigInteger.valueOf(value);
		return ff.makeIntegerLiteral(bi, null);
	}

	private static Expression var(String name) {
		return ff.makeFreeIdentifier(name, null, ff.makeIntegerType());
	}

	private static Expression neg(Expression child) {
		return ff.makeUnaryExpression(UNMINUS, child, null);
	}

	private static void assertNoChange(Expression... children) {
		final AssociativeExpression expr = mul(children);
		final Expression actual = MultiplicationSimplifier.simplify(expr, ff);
		assertSame(expr, actual);
	}

	private static void assertChange(Expression expected,
			Expression... children) {
		final AssociativeExpression expr = mul(children);
		final Expression actual = MultiplicationSimplifier.simplify(expr, ff);
		assertEquals(expected, actual);
	}

	private static void assertZero(Expression... children) {
		assertChange(lit(0), children);
	}

	private static final Expression a = var("a");
	private static final Expression b = var("b");
	private static final Expression c = var("c");

	/**
	 * Ensures that some expressions should not be simplified but returned as
	 * they are.
	 */
	@Test
	public void noChange() throws Exception {
		assertNoChange(a, b);
		assertNoChange(a, b, c);

		assertNoChange(lit(2), a);
		assertNoChange(a, lit(2));

		assertNoChange(lit(2), lit(3));

		assertNoChange(lit(2), a, b);
		assertNoChange(a, lit(2), b);
		assertNoChange(a, b, lit(2));

		assertNoChange(lit(2), a, lit(3));
		assertNoChange(a, lit(2), lit(3));
		assertNoChange(a, lit(3), lit(2));

		assertNoChange(lit(2), lit(3), lit(4));
	}

	/**
	 * Ensures that a multiplication containing a zero factor is simplified to
	 * zero.
	 */
	@Test
	public void zero() throws Exception {
		assertZero(lit(0), a);
		assertZero(a, lit(0));

		assertZero(lit(0), a, b);
		assertZero(a, lit(0), b);
		assertZero(a, b, lit(0));
	
		assertZero(neg(lit(0)), a);
		assertZero(neg(neg(lit(0))), a);
		
		assertZero(neg(a), lit(0));
		assertZero(lit(-1), lit(0));
		assertZero(lit(-2), lit(0));
		assertZero(neg(a), neg(lit(0)));
	}

	/**
	 * Ensures that ones get eliminated.
	 */
	@Test
	public void one() throws Exception {
		assertChange(a, lit(1), a);
		assertChange(a, a, lit(1));

		assertChange(lit(2), lit(1), lit(2));
		assertChange(lit(2), lit(2), lit(1));
		
		assertChange(lit(-2), lit(1), lit(-2));
		assertChange(lit(-2), lit(-2), lit(1));
		
		assertChange(lit(1), lit(1), lit(1));
	}

	/**
	 * Ensures that minus ones get eliminated and their sign propagated.
	 */
	@Test
	public void minusOne() throws Exception {
		minusOneTests(lit(-1));
		minusOneTests(neg(lit(1)));
		minusOneTests(neg(neg(lit(-1))));
	}

	private void minusOneTests(final Expression minusOne) {
		assertChange(neg(a), minusOne, a);
		assertChange(neg(a), a, minusOne);

		assertChange(a, minusOne, neg(a));
		assertChange(a, neg(a), minusOne);
		
		assertChange(lit(-2), minusOne, lit(2));
		assertChange(lit(-2), lit(2), minusOne);
		
		assertChange(lit(2), minusOne, lit(-2));
		assertChange(lit(2), lit(-2), minusOne);
		
		assertChange(lit(-1), minusOne, lit(1));
		assertChange(lit(-1), lit(1), minusOne);
		assertChange(lit(1), minusOne, minusOne);
	}

	/**
	 * Ensures that signs get propagated.
	 */
	@Test
	public void sign() throws Exception {
		assertChange(neg(mul(a, b)), neg(a), b);
		assertChange(neg(mul(a, b)), a, neg(b));
		assertChange(mul(a, b), neg(a), neg(b));
		
		assertChange(neg(mul(a, b)), neg(neg(a)), neg(b));
	}

}
