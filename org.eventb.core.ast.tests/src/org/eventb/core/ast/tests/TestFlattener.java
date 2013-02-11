/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mBinaryPredicate;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedExpression;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mSetExtension;
import static org.eventb.core.ast.tests.FastFactory.mSimplePredicate;
import static org.eventb.core.ast.tests.FastFactory.mUnaryPredicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.junit.Test;

/**
 * Unit test for formula normalization.
 * 
 * @author franz
 */
public class TestFlattener {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	private static FreeIdentifier id_x = ff.makeFreeIdentifier("x",null);
	
	private static BoundIdentDecl bd_a = ff.makeBoundIdentDecl("a",null);
	private static BoundIdentDecl bd_b = ff.makeBoundIdentDecl("b",null);
	private static BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x",null);
	private static BoundIdentDecl bd_y = ff.makeBoundIdentDecl("y",null);
	private static BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z",null);
	private static BoundIdentDecl bd_s = ff.makeBoundIdentDecl("s",null);
	private static BoundIdentDecl bd_t = ff.makeBoundIdentDecl("t",null);

	private static LiteralPredicate btrue = ff.makeLiteralPredicate(BTRUE, null);

	private static Predicate mFin(int... n) {
		final int len = n.length;
		final Expression[] members = new Expression[len];
		for (int i = 0; i < len; ++i) {
			members[i] = ff.makeBoundIdentifier(n[i], null);
		}
		final Expression set = ff.makeSetExtension(members, null);
		return ff.makeSimplePredicate(KFINITE, set, null);
	}
	
	private Expression[] unnormalizedExpressions = new Expression[] {
			mAssociativeExpression(PLUS,
					mAssociativeExpression(PLUS, id_x, id_x),
					mAssociativeExpression(MUL, id_x, id_x)
			),
			mAssociativeExpression(PLUS,
					mAssociativeExpression(PLUS,
							mAssociativeExpression(PLUS, id_x, id_x),
							id_x,
							id_x
					),
					mAssociativeExpression(MUL,
							id_x,
							id_x)
			),
			mAssociativeExpression(PLUS,
					mAssociativeExpression(PLUS, id_x, id_x),
					mAssociativeExpression(MUL, id_x, id_x),
					mAssociativeExpression(PLUS, id_x, id_x)
			),
	
			mUnaryExpression(UNMINUS, mIntegerLiteral(5)),
			mUnaryExpression(UNMINUS,
					mUnaryExpression(UNMINUS, mIntegerLiteral(5))
			),
			
			mSetExtension(),
	};
	
	private Expression[] normalizedExpressions = new Expression[] {
			mAssociativeExpression(PLUS,
					id_x,
					id_x,
					mAssociativeExpression(MUL, id_x, id_x)
			),
			mAssociativeExpression(PLUS,
					id_x,
					id_x,
					id_x,
					id_x,
					mAssociativeExpression(MUL, id_x,id_x)
			),
			mAssociativeExpression(PLUS, 
					id_x,
					id_x,
					mAssociativeExpression(MUL, id_x, id_x),
					id_x,
					id_x),
	
			mIntegerLiteral(-5),
			mIntegerLiteral(5),
			
			mAtomicExpression(EMPTYSET),
	};
	
	private Predicate[] unnormalizedPredicates = new Predicate[] {
			mAssociativePredicate(LOR,
					mAssociativePredicate(LOR,  btrue, btrue),
					mAssociativePredicate(LAND, btrue, btrue)
			),
			mAssociativePredicate(LOR,
					mAssociativePredicate(LOR,
							mAssociativePredicate(LOR, btrue, btrue),
							btrue,
							btrue
					),
					mAssociativePredicate(LAND, btrue, btrue)
			),
			mAssociativePredicate(LOR,
					mAssociativePredicate(LOR,  btrue, btrue),
					mAssociativePredicate(LAND, btrue, btrue),
					mAssociativePredicate(LOR,  btrue, btrue)
			),
	
			mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y), mFin(0,1))
			),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y),
					mQuantifiedPredicate(FORALL, mList(bd_s, bd_t), mFin(0,1,2,3))
			),

			mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y),
							mQuantifiedPredicate(FORALL, mList(bd_z), mFin(0,1,2))
					)
			),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y),
					mQuantifiedPredicate(FORALL, mList(bd_s, bd_t),
							mQuantifiedPredicate(FORALL, mList(bd_a, bd_b), mFin(0,1,2,3,4,5))
					)
			),
			mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y), mFin(0))
			),
			mQuantifiedPredicate(FORALL, mList(bd_x),
					mQuantifiedPredicate(FORALL, mList(bd_y), mFin(1))
			),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z),
					mQuantifiedPredicate(FORALL, mList(bd_s),
							mQuantifiedPredicate(FORALL, mList(bd_a, bd_b), mFin(0,2,4,5))
					)
			),
			
	};
	
	private Predicate[] normalizedPredicates = new Predicate[] {
			mAssociativePredicate(LOR,
					btrue,
					btrue,
					mAssociativePredicate(LAND, btrue, btrue)
			),
			mAssociativePredicate(LOR,
					btrue,
					btrue,
					btrue,
					btrue,
					mAssociativePredicate(LAND, btrue,btrue)
			),
			mAssociativePredicate(LOR,
					btrue,
					btrue,
					mAssociativePredicate(LAND, btrue, btrue),
					btrue,
					btrue
			),
	
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y), mFin(0,1)),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_s, bd_t), mFin(0,1,2,3)),
			
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_z), mFin(0,1,2)),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_s, bd_t, bd_a, bd_b), mFin(0,1,2,3,4,5)),
			mQuantifiedPredicate(FORALL, mList(bd_y), mFin(0)),
			mQuantifiedPredicate(FORALL, mList(bd_x), mFin(0)),
			mQuantifiedPredicate(FORALL, mList(bd_x, bd_y, bd_s, bd_b), mFin(0,1,2,3)),

	};
	
	private Expression[] constructExpressions(Predicate[] preds, Expression exprs[]) {
		Expression[] expressions = new Expression[7 * preds.length];
		int l = 0;
		
		for (int i = 0; i < preds.length; i++) {
			expressions[l++] = mBinaryExpression(exprs[i%exprs.length], exprs[i%exprs.length]);
			expressions[l++] = mAtomicExpression(TRUE);
			expressions[l++] = mBoolExpression(preds[i]);
			expressions[l++] = mQuantifiedExpression(mList(bd_x), preds[i], exprs[i%exprs.length]);
			expressions[l++] = mSetExtension(exprs[i%exprs.length]);
			expressions[l++] = mUnaryExpression(POW, exprs[i%exprs.length]);
			expressions[l++] = id_x;
		}
		return expressions;
	}
	
	private Predicate[] constructPredicates(Predicate[] preds, Expression exprs[]) {
		Predicate[] predicates = new Predicate[preds.length * 5];
		int l = 0;
		for (int i = 0; i < preds.length; i++) {
			predicates[l++] = mBinaryPredicate(preds[i], preds[i]);
			predicates[l++] = mLiteralPredicate();
			predicates[l++] = mSimplePredicate(exprs[i%exprs.length]);
			predicates[l++] = mRelationalPredicate(exprs[i%exprs.length], exprs[i%exprs.length]);
			predicates[l++] = mUnaryPredicate(preds[i]);
		}
		return predicates;
	}

	private static AssociativeExpression mAssociativeExpression(
			int tag, Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	private AssociativePredicate mAssociativePredicate(int tag, Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	private static AtomicExpression mAtomicExpression(int tag) {
		return ff.makeAtomicExpression(tag, null);
	}

	private static IntegerLiteral mIntegerLiteral(int i) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}

	private Predicate mQuantifiedPredicate(int tag, BoundIdentDecl[] idents, Predicate pred) {
		return ff.makeQuantifiedPredicate(tag, idents, pred, null);
	}

	private static UnaryExpression mUnaryExpression(int tag, Expression expr) {
		return ff.makeUnaryExpression(tag, expr, null);
	}
	
	private void routineTest(Formula<?>[] expressions, Formula<?>[] norms) {
		for (int i = 0; i < expressions.length; i++) {
			assertEquals("\nTest failed:\nString to normalize: "
					+ expressions[i] + "\nTree: "
					+ expressions[i].getSyntaxTree() + "\nExpected result: "
					+ norms[i] + "\nTree: " + norms[i].getSyntaxTree()
					+ "\nObtained tree:\n"
					+ expressions[i].flatten().getSyntaxTree(),
					norms[i], expressions[i].flatten());
		}
	}
	
	/**
	 * Main test routine. 
	 */
	@Test 
	public void testNormalizer() {
		routineTest(unnormalizedExpressions,normalizedExpressions);
		routineTest(unnormalizedPredicates,normalizedPredicates);
		routineTest(constructExpressions(unnormalizedPredicates,unnormalizedExpressions),constructExpressions(normalizedPredicates,normalizedExpressions));
		routineTest(constructPredicates(unnormalizedPredicates, unnormalizedExpressions),constructPredicates(normalizedPredicates,normalizedExpressions));
	}
	
	/**
	 * Ensures that flattening an already flattened formula doesn't create a new
	 * formula.
	 */
	@Test 
	public void testNormalizerNop() {
		assertNop(normalizedExpressions);
		assertNop(normalizedPredicates);
		assertNop(constructExpressions(normalizedPredicates,normalizedExpressions));
		assertNop(constructPredicates(normalizedPredicates,normalizedExpressions));
	}

	private void assertNop(Formula<?>[] formulas) {
		for (Formula<?> formula : formulas) {
			final Formula<?> flattened = formula.flatten();
			assertEquals("Flattener not involutive for formula: " + formula, formula, flattened);
			assertSame("Flattener created a copy of formula: " + formula, formula, flattened);
		}
	}

}
