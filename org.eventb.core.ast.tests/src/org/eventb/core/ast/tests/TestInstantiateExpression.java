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
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.tests.FastFactory.mAssociativeExpression;
import static org.eventb.core.ast.tests.FastFactory.mAssociativePredicate;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;

public class TestInstantiateExpression extends AbstractTests {

	private final IntegerType tINTEGER = ff.makeIntegerType();
	private final FreeIdentifier id_a = mFreeIdentifier("a", tINTEGER);
	private final FreeIdentifier id_b = mFreeIdentifier("b", tINTEGER);

	private BoundIdentDecl[] BD(String... names) {
		BoundIdentDecl[] bd = new BoundIdentDecl[names.length];
		for (int i = 0; i < names.length; i++)
			bd[i] = ff.makeBoundIdentDecl(names[i], null);
		return bd;
	}

	private void performTest(QuantifiedExpression expr, Expression[] witnesses,
			Expression expected) {
		ITypeEnvironment te = mTypeEnvironment();
		typeCheck(expr, te);
		typeCheck(expected, te);

		final Expression result = expr.instantiate(witnesses, ff);
		assertTrue(result.isTypeChecked());
		assertEquals(expr.toString(), expected, result);
	}

	private QuantifiedExpression makeQExpr(Predicate p, Expression e,
			String... bids) {
		return mQuantifiedExpression(CSET, Form.Explicit, BD(bids), p, e);
	}

	public void test1() {
		// {x · x=0 ∣ x} == 0
		final Predicate p = mRelationalPredicate(EQUAL, mBoundIdentifier(0),
				mIntegerLiteral(0));
		final Expression e = mBoundIdentifier(0);
		final QuantifiedExpression expr = makeQExpr(p, e, "x");

		final Expression[] witnesses = mList(mIntegerLiteral(0));

		final Expression expected = mSetExtension(mIntegerLiteral(0));

		performTest(expr, witnesses, expected);
	}

	public void test2() {
		// {x,y · x=0 ∧ y=0 ∣ x+y} == {0+0}
		final Predicate p = mAssociativePredicate(
				LAND,
				mRelationalPredicate(EQUAL, mBoundIdentifier(1),
						mIntegerLiteral(0)),
				mRelationalPredicate(EQUAL, mBoundIdentifier(0),
						mIntegerLiteral(0)));
		final Expression e = mAssociativeExpression(PLUS, mBoundIdentifier(1),
				mBoundIdentifier(0));
		final QuantifiedExpression expr = makeQExpr(p, e, "x", "y");

		final Expression[] witnesses = mList(mIntegerLiteral(0),
				mIntegerLiteral(0));

		final Expression expected = mSetExtension(mAssociativeExpression(PLUS,
				mIntegerLiteral(0), mIntegerLiteral(0)));

		performTest(expr, witnesses, expected);
	}

	public void test3() {
		// {x,y · x=0 ∧ 0≥x ∣ x+y} == {y · 0≥0 ∣ 0+y}
		final Predicate p = mAssociativePredicate(
				LAND,
				mRelationalPredicate(EQUAL, mBoundIdentifier(1),
						mIntegerLiteral(0)),
				mRelationalPredicate(GE, mIntegerLiteral(0),
						mBoundIdentifier(1)));
		final Expression e = mAssociativeExpression(PLUS, mBoundIdentifier(1),
				mBoundIdentifier(0));
		final QuantifiedExpression expr = makeQExpr(p, e, "x", "y");

		final Expression[] witnesses = mList(mIntegerLiteral(0), null);

		final Predicate ep = mRelationalPredicate(GE, mIntegerLiteral(0),
				mIntegerLiteral(0));
		final Expression ee = mAssociativeExpression(PLUS, mIntegerLiteral(0),
				mBoundIdentifier(0));
		Expression expected = makeQExpr(ep, ee, "y");

		performTest(expr, witnesses, expected);
	}

	public void test4() {
		// {x,y,z · x=1 ∧ z=2 ∧ y=x↦z ∣ x+z} == {y · y=1↦2 ∣ 1+2}
		final Predicate p = mAssociativePredicate(
				LAND,
				mRelationalPredicate(EQUAL, mBoundIdentifier(2),
						mIntegerLiteral(1)),
				mRelationalPredicate(EQUAL, mBoundIdentifier(0),
						mIntegerLiteral(2)),
				mRelationalPredicate(
						EQUAL,
						mBinaryExpression(MAPSTO, mBoundIdentifier(2),
								mBoundIdentifier(0)), mBoundIdentifier(1)));
		final Expression e = mAssociativeExpression(PLUS, mBoundIdentifier(2),
				mBoundIdentifier(0));
		final QuantifiedExpression expr = makeQExpr(p, e, "x", "y", "z");

		final Expression[] witnesses = mList(mIntegerLiteral(1), null,
				mIntegerLiteral(2));

		final Predicate ep = mRelationalPredicate(
				EQUAL,
				mBinaryExpression(MAPSTO, mIntegerLiteral(1),
						mIntegerLiteral(2)), mBoundIdentifier(0));
		final Expression ee = mAssociativeExpression(PLUS, mIntegerLiteral(1),
				mIntegerLiteral(2));
		Expression expected = makeQExpr(ep, ee, "y");

		performTest(expr, witnesses, expected);
	}

	public void test5() {
		// {x,y · x=a ∧ y=x ∣ y+x} == {y · y=a ∣ y+a}
		final Predicate p = mAssociativePredicate(
				LAND,
				mRelationalPredicate(EQUAL, mBoundIdentifier(1), id_a),
				mRelationalPredicate(EQUAL, mBoundIdentifier(0),
						mBoundIdentifier(1)));
		final Expression e = mAssociativeExpression(PLUS, mBoundIdentifier(0),
				mBoundIdentifier(1));
		final QuantifiedExpression expr = makeQExpr(p, e, "x", "y");

		final Expression[] witnesses = mList(id_a, null);

		final Predicate ep = mRelationalPredicate(EQUAL, mBoundIdentifier(0),
				id_a);
		final Expression ee = mAssociativeExpression(PLUS, mBoundIdentifier(0),
				id_a);
		final QuantifiedExpression expected = makeQExpr(ep, ee, "y");

		performTest(expr, witnesses, expected);
	}

	public void test6() {
		// {x,y · x=a+1 ∧ y=b∗x ∣ x∗y} == {y · y=b∗(a+1) ∣ (a+1)∗y}
		final Predicate p = mAssociativePredicate(
				LAND,
				mRelationalPredicate(EQUAL, mBoundIdentifier(1),
						mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1))),
				mRelationalPredicate(EQUAL, mBoundIdentifier(0),
						mAssociativeExpression(MUL, id_b, mBoundIdentifier(1))));
		final Expression e = mAssociativeExpression(MUL, mBoundIdentifier(1),
				mBoundIdentifier(0));
		final QuantifiedExpression expr = makeQExpr(p, e, "x", "y");

		final Expression[] witnesses = mList(
				mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1)), null);

		final Predicate ep = mRelationalPredicate(
				EQUAL,
				mBoundIdentifier(0),
				mAssociativeExpression(MUL, id_b,
						mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1))));
		final Expression ee = mAssociativeExpression(MUL,
				mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1)),
				mBoundIdentifier(0));
		final QuantifiedExpression expected = makeQExpr(ep, ee, "y");

		performTest(expr, witnesses, expected);

		// test6 follow-up: {y · y=b∗(a+1) ∣ (a+1)∗y} == {(a+1)∗(b∗(a+1))}
		final QuantifiedExpression expr2 = expected;

		final Expression[] witnesses2 = mList(mAssociativeExpression(MUL, id_b,
				mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1))));

		final Expression expected2 = mSetExtension(mAssociativeExpression(
				MUL,
				mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1)),
				mAssociativeExpression(MUL, id_b,
						mAssociativeExpression(PLUS, id_a, mIntegerLiteral(1)))));

		performTest(expr2, witnesses2, expected2);
	}

}
