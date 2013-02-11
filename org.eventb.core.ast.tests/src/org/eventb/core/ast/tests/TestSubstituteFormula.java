/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.tests.FastFactory.mBinaryExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mQuantifiedPredicate;
import static org.eventb.core.ast.tests.FastFactory.mRelationalPredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.junit.Test;

/**
 * @author halstefa
 *
 */
public class TestSubstituteFormula extends AbstractTests {
	
	private static Map<FreeIdentifier, Expression> ms(FreeIdentifier[] ids, Expression[] exps) {
		assert ids.length == exps.length;
		HashMap<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>(ids.length + ids.length/3);
		for (int i=0; i<ids.length; i++)
			map.put(ids[i], exps[i]);
		return map;
	}
	
	/* Abstract class for tests */
	static abstract class TestItem {
		abstract void doTest();

		protected void typeCheck(Formula<?> form) {
			TestSubstituteFormula.typeCheck(form, tenv);
		}
		
	}
	
	static class STestItem extends TestItem {
		public final Predicate formula;
		public final Map<FreeIdentifier, Expression> sbs;
		public final Predicate expected;
		
		public STestItem(Predicate formula, Map<FreeIdentifier, Expression> sbs, Predicate expected) {
			this.formula = formula;
			this.sbs = sbs;
			this.expected = expected;
		}
		
		@Override
		public void doTest() {
			typeCheck(formula);
			
			for (Expression expression: sbs.values()) {
				typeCheck(expression);
			}

			// Type-check the expected result before comparing it
			typeCheck(expected);

			Predicate result = formula.substituteFreeIdents(sbs);
			assertTrue(formula.toString(), result.isTypeChecked());
			assertEquals(formula + "\n" + sbs + "\n" , expected, result);
		}
		
		@Override
		public String toString() {
			return formula.toString() + " //" + sbs.toString() + " == " + expected.toString();
		}

	}
	
	// Test for checking standard substitutions applied within a quantified predicate
	static class BTestItem extends TestItem { 
		public final QuantifiedPredicate formula;
		public final Predicate[] sbs;
		public final Predicate expected;
		
		public BTestItem(Predicate formula, Predicate[] sbs, Predicate expected) {
			this.formula = (QuantifiedPredicate) formula;
			this.sbs = sbs;
			this.expected = expected;
		}
		
		@Override
		public void doTest() {
			typeCheck(formula);
			
			for (Predicate predicate : sbs) {
				typeCheck(predicate);
			}

			// Type-check the expected result before comparing it
			typeCheck(expected);

			Map<FreeIdentifier, Expression> sbsMap = makeSBS(sbs);
			Predicate iresult = formula.getPredicate().substituteFreeIdents(sbsMap);
			assertTrue(formula.toString(), iresult.isTypeChecked());

			Predicate result = ff.makeQuantifiedPredicate(formula.getTag(),
					formula.getBoundIdentDecls(), iresult, null);

			assertTrue(formula.toString(), result.isTypeChecked());
			assertEquals(formula + "\n" + sbs + "\n" , expected, result);
		}

		private Map<FreeIdentifier, Expression> makeSBS(Predicate[] predicates) {
			final int length = predicates.length;
			HashMap<FreeIdentifier, Expression> map = 
				new HashMap<FreeIdentifier, Expression>(length * 4 / 3);
			for (Predicate pp : predicates) {
				map.put(fst(pp), snd(pp));
			}
			return map;
		}
		
		@Override
		public String toString() {
			String pp;
			if(sbs.length == 0)
				pp = "";
			else {
				pp = fst(sbs[0]).toString() + "=" + snd(sbs[0]).toString();
				for(int i=1; i<sbs.length; i++)
					pp += ", " + fst(sbs[i]).toString() + "=" + snd(sbs[i]).toString();
			}
			return formula.toString() + " //{" + pp + "} == " + expected.toString();
		}
	}
	
	private static interface PredicateBuilder {
		Predicate build(Predicate child);
	}
	
	private static IntegerType tINTEGER = ff.makeIntegerType();

	static AtomicExpression INTEGER = ff.makeAtomicExpression(Formula.INTEGER, null);
	
	private static FreeIdentifier id_x = mFreeIdentifier("x", tINTEGER);
	private static FreeIdentifier id_y = mFreeIdentifier("y", tINTEGER);
	private static FreeIdentifier id_A = mFreeIdentifier("A", null);
	private static FreeIdentifier id_f = mFreeIdentifier("f", REL(tINTEGER,tINTEGER));
	private static FreeIdentifier id_a = mFreeIdentifier("a", tINTEGER);
	private static FreeIdentifier id_b = mFreeIdentifier("b", tINTEGER);

	public static final ITypeEnvironment tenv = mTypeEnvironment(
			"x=ℤ; y=ℤ; A=ℙ(ℤ); B=ℙ(ℤ); f=ℤ↔ℤ; Y=ℙ(BOOL)", ff);
	
	private static FreeIdentifier[] mi(FreeIdentifier...freeIdentifiers) {
		return freeIdentifiers;
	}
	
	private static Expression[] me(Expression...expressions) {
		return expressions;
	}
	
	private static Predicate[] mp(Predicate...predicates) {
		return predicates;
	}
	
	static Predicate eq(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.EQUAL, l, r, null);
	}
	
	private static Predicate lt(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.LT, l, r, null);
	}
	
	static QuantifiedPredicate forall(BoundIdentDecl[] bd, Predicate pr) {
		return ff.makeQuantifiedPredicate(Formula.FORALL, bd, pr, null);
	}
	
	static QuantifiedPredicate exists(BoundIdentDecl[] bd, Predicate pr) {
		return ff.makeQuantifiedPredicate(Formula.EXISTS, bd, pr, null);
	}
	
	static BoundIdentDecl[] BD(String... names) {
		BoundIdentDecl[] bd = new BoundIdentDecl[names.length];
		for(int i=0; i<names.length; i++)
			bd[i] = ff.makeBoundIdentDecl(names[i], null);
		return bd;
	}
	
	static BoundIdentDecl[] BDI(String... names) {
		BoundIdentDecl[] bd = new BoundIdentDecl[names.length];
		for(int i=0; i<names.length; i++)
			bd[i] = ff.makeBoundIdentDecl(names[i], null, tINTEGER);
		return bd;
	}
	
	static BoundIdentifier bd(int i) {
		return ff.makeBoundIdentifier(i, null);
	}
	
	static BoundIdentifier bdi(int i) {
		return ff.makeBoundIdentifier(i, null, tINTEGER);
	}
	
	private static Expression apply(Expression l, Expression r) {
		return ff.makeBinaryExpression(Formula.FUNIMAGE, l, r, null);
	}
	
	static Expression num(int i) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}
	
	static Expression plus(Expression...expressions) {
		return ff.makeAssociativeExpression(Formula.PLUS, expressions, null);
	}
	
	private static Expression minus(Expression l, Expression r) {
		return ff.makeBinaryExpression(Formula.MINUS, l, r, null);
	}
	
	static Predicate in(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.IN, l, r, null);
	}
	
	private static Expression fun(BoundIdentDecl[] d, Predicate p, Expression e) {
		return ff.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Lambda);
	}
	
	static Predicate limp(Predicate l, Predicate r) {
		return ff.makeBinaryPredicate(Formula.LIMP, l, r, null);
	}
	
	private static Expression maplet(Expression l, Expression r) {
		return ff.makeBinaryExpression(Formula.MAPSTO, l, r,null);
	}
	
	Predicate[] pra = new Predicate[] {
			eq(id_x, id_y),
			forall(BD("x"), eq(apply(id_f, bd(0)),num(0))),
			forall(BD("a", "x"), exists(BD("b"), eq(apply(id_f, plus(bd(2), bd(1))), plus(bd(0), id_y)))),
			lt(id_x, id_y)
	};
	
	Predicate[] prb = new Predicate[] {
			eq(minus(num(1), id_y), id_y),
			forall(BD("x"), eq(apply(fun(BD("x"), in(bd(0), id_A), maplet(bd(0),apply(id_f, bd(0)))), bd(0)),num(0))),
			forall(BD("a", "x"), exists(BD("b"), eq(apply(fun(BD("x"), in(bd(0), id_A), maplet(bd(0),plus(bd(0),bd(2)))), plus(bd(2), bd(1))), plus(bd(0), id_y)))),
			lt(id_y, id_x),
			forall(BD("a", "x"), exists(BD("b"), eq(apply(fun(BD("x"), in(bd(0), id_A), maplet(bd(0),plus(bd(0),bd(2)))), plus(bd(2), bd(1))), plus(bd(0), bd(2)))))
	};
	
	// the equality after the implication serves to construct an substitution
	// the rest is there for type checking
	Predicate[] pxx = new Predicate[] {
			forall(BD("a", "x"), limp(in(bd(1),id_A),eq(id_f,(fun(BD("x"), in(bd(0), id_A), maplet(bd(0),plus(bd(0),bd(1)))))))),
			forall(BD("a", "x"), limp(in(bd(0),id_A),eq(id_y,bd(1))))
	};
	
	static FreeIdentifier fst(Predicate p) {
		return (FreeIdentifier) ((RelationalPredicate) ((BinaryPredicate) ((QuantifiedPredicate) p).getPredicate()).getRight()).getLeft();
	}
	
	static Expression snd(Predicate p) {
		return ((RelationalPredicate) ((BinaryPredicate) ((QuantifiedPredicate) p).getPredicate()).getRight()).getRight();
	}
	
	Expression[] exa = new Expression[] {
			minus(num(1), id_y),
			fun(BD("x"), in(bd(0), id_A), maplet(bd(0),apply(id_f, bd(0))))
	};
	
	QuantifiedPredicate[] tra = new QuantifiedPredicate[] {
			forall(BD("x"), in(bd(0), INTEGER)),
			forall(BD("m"), eq(bd(0), bd(1))),
			forall(BD("h","i","j","k"), limp(eq(plus(bd(3),bd(2),bd(1),bd(0)),minus(bd(5),bd(4))), lt(bd(2),bd(5)))),
			forall(BD("h","i","j","k"), limp(eq(plus(bd(3),bd(2),bd(1),bd(0)),minus(bd(5),bd(4))), exists(BD("z"),lt(plus(bd(0),bd(3)),bd(6))))),
			forall(BD("x", "y"), eq(bd(1), plus(bd(0), num(1)))),
	};
	
	PredicateBuilder[] spa = new PredicateBuilder[] {
			new PredicateBuilder() {
				@Override
				public Predicate build(Predicate child) {
					return child;
				}
			},
			new PredicateBuilder() {
				@Override
				public Predicate build(Predicate child) {
					return forall(BDI("w"), limp(in(bdi(0), INTEGER), child));
				}
			},
			new PredicateBuilder() {
				@Override
				public Predicate build(Predicate child) {
					return exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), child));
				}
			},
			new PredicateBuilder() {
				@Override
				public Predicate build(Predicate child) {
					return exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), child));
				}
			},
	};
	
	Predicate[] spr = new Predicate[] {
			in(plus(id_y, apply(id_f, id_y)), INTEGER),
			forall(BDI("w"), limp(in(bdi(0), INTEGER), eq(plus(id_x, num(1)), bdi(0)))),
			exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), 
					forall(BDI("h","k"), limp(
							eq(plus(bdi(1),apply(id_f, num(5)),minus(num(9), id_y),bdi(0)),minus(bdi(3),bdi(2))),
							lt(apply(id_f, num(5)),bdi(3))))
			)),
			exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), 
					forall(BDI("h","k"), limp(
							eq(plus(bdi(1),apply(id_f, bdi(4)),plus(bdi(3),num(1)),bdi(0)),minus(bdi(3),bdi(2))),
							lt(apply(id_f, bdi(4)),bdi(3))))
			)),
			exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), 
					forall(BDI("h","k"), limp(eq(plus(bdi(1),apply(id_f, bdi(4)),plus(bdi(3),num(1)),bdi(0)),minus(bdi(3),bdi(2))), exists(BDI("z"),lt(plus(bdi(0),apply(id_f, bdi(5))),bdi(4)))))
			)),
			exists(BDI("e", "f"), limp(eq(plus(bdi(1),bdi(0)),num(1)), 
					forall(BDI("i","k"), limp(
							eq(plus(apply(id_f, bdi(4)), bdi(1), plus(bdi(3),num(1)), bdi(0)), minus(bdi(3),bdi(2))),
							exists(BDI("z"), lt(plus(bdi(0), bdi(2)), bdi(4)))))
			)),
			forall(BDI("x"), eq(bdi(0), plus(id_a, num(1)))),
			eq(id_a, plus(id_b, num(1))),
	};
	
	Expression[] sea = new Expression[] {
			plus(id_y, apply(id_f, id_y)),
			plus(id_x, num(1)),
			apply(id_f, num(5)),
			minus(num(9), id_y),
			apply(id_f, bdi(2)),
			plus(bdi(1),num(1)),
	};
	
	static class UTestItem extends TestItem {
		public final PredicateBuilder builder;
		public final QuantifiedPredicate subpred;
		public final Expression[] map;
		public final Predicate expected;
		public final String mapImage;
		
		public UTestItem(PredicateBuilder builder, QuantifiedPredicate subpred, Expression[] map, Predicate expected) {
			this.builder = builder;
			this.subpred = subpred;
			this.map = map;
			this.expected = expected;

			StringBuilder mapImageBuilder = new StringBuilder("[");
			boolean comma = false;
			for(Expression expression : map) {
				if (comma) mapImageBuilder.append(", ");
				mapImageBuilder.append(expression);
				comma = true;
			}
			mapImageBuilder.append("]");
			mapImage = mapImageBuilder.toString();
		}
		
		@Override
		public void doTest() {
			Predicate predicate = builder.build(subpred);
			typeCheck(predicate);
			
			assertTrue(expected.isTypeChecked());

			Predicate result = subpred.instantiate(map, ff);
			result = builder.build(result);
			assertTrue(predicate.toString(), result.isTypeChecked());
			assertEquals(predicate + "\n" + mapImage + "\n", expected, result);
		}
		
		@Override
		public String toString() {
			return "apply " + mapImage
			+ "\n  to        " + subpred
			+ "\n  in        " + builder.build(subpred)
			+ "\n  expecting " + expected;
		}
	}
	
	TestItem[] testItems = new TestItem[] {
			new STestItem(pra[0], ms(mi(id_x), me(exa[0])), prb[0]),
			new STestItem(pra[1], ms(mi(id_f), me(exa[1])), prb[1]),
			new BTestItem(pra[2], mp(pxx[0]), prb[2]),
			new STestItem(pra[3], ms(mi(id_x, id_y), me(id_y, id_x)), prb[3]),
			new UTestItem(spa[0], tra[0], mList(me(sea[0])), spr[0]),
			new UTestItem(spa[1], tra[1], mList(me(sea[1])), spr[1]),
			new UTestItem(spa[2], tra[2], mList(null, sea[2], sea[3], null), spr[2]),
			new UTestItem(spa[2], tra[2], mList(null, sea[4], sea[5], null), spr[3]),
			new UTestItem(spa[3], tra[3], mList(null, sea[4], sea[5], null), spr[4]),
			new BTestItem(pra[2], mp(pxx[0],pxx[1]), prb[4]),
			new UTestItem(spa[3], tra[3], mList(sea[4], null, sea[5], null), spr[5]),
			// Two examples from Javadoc of QuantifiedPredicate.instantiate()
			new UTestItem(spa[0], tra[4], mList(null, id_a), spr[6]),
			new UTestItem(spa[0], tra[4], mList(id_a, id_b), spr[7]),
	};
	
	@Test 
	public void testSubstitutionStandard() {
		for (TestItem testItem: testItems) {
			testItem.doTest();
		}
	}
	
	@Test 
	public void testSubst() {
		QuantifiedPredicate pred =
			mQuantifiedPredicate(EXISTS, BD("x", "y"),
					mRelationalPredicate(EQUAL, bd(0),
							mBinaryExpression(DIV, bd(1), id_a))
			);
		ITypeEnvironment te = mTypeEnvironment();
		typeCheck(pred, te);
		
        Expression[] witnesses = mList(id_a, null);
        
        Predicate expected = 
        	mQuantifiedPredicate(EXISTS, BD("y"),
					mRelationalPredicate(EQUAL, bd(0),
							mBinaryExpression(DIV, id_a, id_a))
			);
		typeCheck(expected, te);
        
        Predicate result = pred.instantiate(witnesses, ff);
		assertTrue(result.isTypeChecked());
        assertEquals(pred.toString(), expected, result);
	}

	/**
	 * Examples given in the Javadoc of
	 * {@link Formula#applyAssignment(BecomesEqualTo)}.
	 */
	@Test 
	public void testApplyAssignment() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		Expression expr = plus(id_x, id_y);
		typeCheck(expr, te);
		
		// First example
		BecomesEqualTo assignment = ff.makeBecomesEqualTo(id_x, num(0), null);
		typeCheck(assignment, te);
		Expression expExpr = plus(num(0), id_y);
		typeCheck(expExpr, te);
		Expression actual = expr.applyAssignment(assignment);
		assertTrue("Formula " + actual + " should be typechecked.",
				actual.isTypeChecked());
		assertEquals(expExpr, actual);
		
		// Second example
		assignment = ff.makeBecomesEqualTo(id_x, plus(id_x, num(1)), null);
		typeCheck(assignment, te);
		expExpr = plus(plus(id_x, num(1)), id_y);
		typeCheck(expExpr, te);
		actual = expr.applyAssignment(assignment);
		assertTrue("Formula " + actual + " should be typechecked.",
				actual.isTypeChecked());
		assertEquals(expExpr, actual);
		
		// Third example
		assignment = ff.makeBecomesEqualTo(mList(id_x, id_y), mList(id_y, id_x), null);
		typeCheck(assignment, te);
		expExpr = plus(id_y, id_x);
		typeCheck(expExpr, te);
		actual = expr.applyAssignment(assignment);
		assertTrue("Formula " + actual + " should be typechecked.",
				actual.isTypeChecked());
		assertEquals(expExpr, actual);
		
		// Example with a predicate and a bound variable capture
		Predicate pred = forall(BD("y"), eq(bd(0), id_x));
		typeCheck(pred, te);
		assignment = ff.makeBecomesEqualTo(id_x, id_y, null);
		typeCheck(assignment, te);
		Predicate expPred = forall(BD("z"), eq(bd(0), id_y));
		typeCheck(expPred, te);
		Predicate actualPred = pred.applyAssignment(assignment);
		assertTrue("Formula " + actualPred + " should be typechecked.",
				actualPred.isTypeChecked());
		assertEquals(expPred, actualPred);
	}
	
	/**
	 * Simple examples of a parallel assignment that gives a result that can not
	 * be achieved by a sequence of assignments.
	 */
	@Test 
	public void testApplyAssignments() {
		ITypeEnvironment te = ff.makeTypeEnvironment();
		Expression expr = plus(id_x, id_y);
		typeCheck(expr, te);
		
		BecomesEqualTo[] assignments = new BecomesEqualTo[] {
				ff.makeBecomesEqualTo(id_x, id_y, null),
				ff.makeBecomesEqualTo(id_y, id_x, null)
		};
		
		Expression expected = plus(id_y, id_x);
		typeCheck(expected, te); 

		Expression actual = expr.applyAssignments(Arrays.asList(assignments));
		assertEquals("Wrong result of substitution", expected, actual);
	}
	
	static class ShiftTestItem<T extends Formula<T>> extends TestItem {
		public final T formula;
		public final int offset;
		public final T expected;
		
		public ShiftTestItem(T formula, int offset, T expected) {
			this.formula = formula;
			this.offset = offset;
			this.expected = expected;
		}
		
		@Override
		public void doTest() {
			Formula<?> result = formula.shiftBoundIdentifiers(offset, ff);
			assertEquals(formula + "\n" + offset + "\n" , expected, result);
		}
		
		@Override
		public String toString() {
			return formula.toString() + " //" + offset + " == " + expected.toString();
		}
	}

	ShiftTestItem<?>[] shiftTestItems = new ShiftTestItem[] {
		// Test on closed expression
		new ShiftTestItem<Expression>(
			id_x,
			1,
			id_x
		// Test on open expression (without internally bound)
		), new ShiftTestItem<Expression>(
			bd(0),
			0,
			bd(0)
		), new ShiftTestItem<Expression>(
			bd(0),
			1,
			bd(1)
		), new ShiftTestItem<Expression>(
			bd(1),
			-1,
			bd(0)
		// Test on open expression (with internally bound)
		), new ShiftTestItem<Predicate>(
			exists(BD("x"), lt(bd(0), bd(1))),
			0,
			exists(BD("x"), lt(bd(0), bd(1)))
		), new ShiftTestItem<Predicate>(
			exists(BD("x"), lt(bd(0), bd(1))),
			1,
			exists(BD("x"), lt(bd(0), bd(2)))
		), new ShiftTestItem<Predicate>(
			exists(BD("x"), lt(bd(0), bd(2))),
			-1,
			exists(BD("x"), lt(bd(0), bd(1)))
		),	
	};
	
	/**
	 * Test for {@link Formula#shiftBoundIdentifiers(int, FormulaFactory)}.
	 */
	@Test 
	public void testShiftBoundIdentifiers() {
		for (TestItem item : shiftTestItems) {
			item.doTest();
		}
	}

}
