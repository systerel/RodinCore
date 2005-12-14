/**
 * 
 */
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

/**
 * @author halstefa
 *
 */
public class TestSubstituteFormula extends TestCase {

	public static final FormulaFactory ff = FormulaFactory.getDefault(); 
	
	private static Map<FreeIdentifier, Expression> ms(FreeIdentifier[] ids, Expression[] exps) {
		assert ids.length == exps.length;
		HashMap<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>(ids.length + ids.length/3);
		for (int i=0; i<ids.length; i++)
			map.put(ids[i], exps[i]);
		return map;
	}
	
	private static Map<Integer, Expression> mu(Integer[] ids, Expression[] exps) {
		assert ids.length == exps.length;
		HashMap<Integer, Expression> map = new HashMap<Integer, Expression>(ids.length + ids.length/3);
		for (int i=0; i<ids.length; i++)
			map.put(ids[i], exps[i]);
		return map;
	}
	
	interface TestItem {
		void doTest();
	}
	
	static class STestItem implements TestItem {
		public final Predicate formula;
		public final Map<FreeIdentifier, Expression> sbs;
		public final Predicate expected;
		
		public STestItem(Predicate formula, Map<FreeIdentifier, Expression> sbs, Predicate expected) {
			this.formula = formula;
			this.sbs = sbs;
			this.expected = expected;
		}
		
		public void doTest() {
			ITypeCheckResult tresult = formula.typeCheck(tenv);
			assertTrue(formula.toString(), tresult.isSuccess());
			
			for(Expression expression : sbs.values()) {
				ITypeCheckResult tcr = expression.typeCheck(tenv);
				assertTrue(expression.toString(), tcr.isSuccess());
			}
			
			// Type-check the expected result before comparing it
			ITypeCheckResult exptresult = expected.typeCheck(tenv);
			assertTrue(formula.toString(), exptresult.isSuccess());

			Predicate result = formula.substituteFreeIdents(sbs, ff);
			assertEquals(formula + "\n" + sbs + "\n" , expected, result);
		}
		
		@Override
		public String toString() {
			return formula.toString() + " //" + sbs.toString() + " == " + expected.toString();
		}
	}
	
	static class BTestItem implements TestItem { 
		public final Predicate formula;
		public final Predicate[] sbs;
		public final Predicate expected;
		
		public BTestItem(Predicate formula, Predicate[] sbs, Predicate expected) {
			this.formula = formula;
			this.sbs = sbs;
			this.expected = expected;
		}
		
		public void doTest() {
			ITypeCheckResult tresult = formula.typeCheck(tenv);
			assertTrue(formula.toString(), tresult.isSuccess());
			
			for(Predicate predicate : sbs) {
				ITypeCheckResult tcr = predicate.typeCheck(tenv);
				assertTrue(predicate.toString(), tcr.isSuccess());
			}

			// Type-check the expected result before comparing it
			ITypeCheckResult exptresult = expected.typeCheck(tenv);
			assertTrue(formula.toString(), exptresult.isSuccess());

			Map<FreeIdentifier, Expression> sbsMap = makeSBS(sbs);
			QuantifiedPredicate qformula = (QuantifiedPredicate) formula;
			Predicate iresult = qformula.getPredicate().substituteFreeIdents(sbsMap, ff);
			Predicate result = ff.makeQuantifiedPredicate(qformula.getTag(), qformula.getBoundIdentifiers(), iresult, null);
			assertEquals(formula + "\n" + sbs + "\n" , expected, result);
		}
		
		private Map<FreeIdentifier, Expression> makeSBS(Predicate[] predicates) {
			HashMap<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>(predicates.length + predicates.length/4 + 1);
			for(Predicate pp : predicates) {
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
	
	private static IntegerType tINTEGER = ff.makeIntegerType();
	private static BooleanType tBOOL = ff.makeBooleanType();

	private static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	private static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}
	
	private static AtomicExpression INTEGER = ff.makeAtomicExpression(Formula.INTEGER, null);
	
	private static FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private static FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	private static FreeIdentifier id_A = ff.makeFreeIdentifier("A", null);
	private static FreeIdentifier id_f = ff.makeFreeIdentifier("f", null);

	public static final ITypeEnvironment tenv = mTypeEnvironment(
			mList(
					"x",
					"y",
					"A",
					"B",
					"f",
					"Y"
			),
			mList(
					tINTEGER,
					tINTEGER,
					POW(tINTEGER),
					POW(tINTEGER),
					POW(CPROD(tINTEGER,tINTEGER)),
					POW(tBOOL)
			)
	);
	
	private static FreeIdentifier[] mi(FreeIdentifier...freeIdentifiers) {
		return freeIdentifiers;
	}
	
	private static Integer[] mj(Integer...integers) {
		return integers;
	}
	
	private static Expression[] me(Expression...expressions) {
		return expressions;
	}
	
	private static Predicate[] mp(Predicate...predicates) {
		return predicates;
	}
	
	private static Predicate eq(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.EQUAL, l, r, null);
	}
	
	private static Predicate lt(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.LT, l, r, null);
	}
	
	private static QuantifiedPredicate forall(BoundIdentDecl[] bd, Predicate pr) {
		return ff.makeQuantifiedPredicate(Formula.FORALL, bd, pr, null);
	}
	
	private static QuantifiedPredicate exists(BoundIdentDecl[] bd, Predicate pr) {
		return ff.makeQuantifiedPredicate(Formula.EXISTS, bd, pr, null);
	}
	
	private static BoundIdentDecl[] BD(String...strings) {
		BoundIdentDecl[] bd = new BoundIdentDecl[strings.length];
		for(int i=0; i<strings.length; i++)
			bd[i] = ff.makeBoundIdentDecl(strings[i], null);
		return bd;
	}
	
	private static BoundIdentifier bd(int i) {
		return ff.makeBoundIdentifier(i, null);
	}
	
	private static Expression apply(Expression l, Expression r) {
		return ff.makeBinaryExpression(Formula.FUNIMAGE, l, r, null);
	}
	
	private static Expression num(int i) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(i), null);
	}
	
	private static Expression plus(Expression...expressions) {
		return ff.makeAssociativeExpression(Formula.PLUS, expressions, null);
	}
	
	private static Expression minus(Expression l, Expression r) {
		return ff.makeBinaryExpression(Formula.MINUS, l, r, null);
	}
	
	private static Predicate in(Expression l, Expression r) {
		return ff.makeRelationalPredicate(Formula.IN, l, r, null);
	}
	
	private static Expression fun(BoundIdentDecl[] d, Predicate p, Expression e) {
		return ff.makeQuantifiedExpression(Formula.CSET, d, p, e, null, QuantifiedExpression.Form.Lambda);
	}
	
	private static Predicate limp(Predicate l, Predicate r) {
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
	
	public static FreeIdentifier fst(Predicate p) {
		return (FreeIdentifier) ((RelationalPredicate) ((BinaryPredicate) ((QuantifiedPredicate) p).getPredicate()).getRight()).getLeft();
	}
	
	public static Expression snd(Predicate p) {
		return ((RelationalPredicate) ((BinaryPredicate) ((QuantifiedPredicate) p).getPredicate()).getRight()).getRight();
	}
	
	Expression[] exa = new Expression[] {
			minus(num(1), id_y),
			fun(BD("x"), in(bd(0), id_A), maplet(bd(0),apply(id_f, bd(0))))
	};
	
	QuantifiedPredicate[] tra = new QuantifiedPredicate[] {
			forall(BD("m"), eq(bd(0), bd(1))),
			forall(BD("h","i","j","k"), limp(eq(plus(bd(3),bd(2),bd(1),bd(0)),minus(bd(5),bd(4))), lt(bd(2),bd(5)))),
			forall(BD("h","i","j","k"), limp(eq(plus(bd(3),bd(2),bd(1),bd(0)),minus(bd(5),bd(4))), exists(BD("z"),lt(plus(bd(0),bd(3)),bd(6)))))
	};
	
	Predicate[] spa = new Predicate[] {
			forall(BD("x"), in(bd(0), INTEGER)),
			forall(BD("w"), limp(in(bd(0), INTEGER), tra[0])),
			exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0)),num(1)), tra[1])),
			exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0)),num(1)), tra[2]))
	};
	
	Predicate[] spr = new Predicate[] {
			in(plus(id_y, apply(id_f, id_y)), INTEGER),
			forall(BD("w"), limp(in(bd(0), INTEGER), eq(plus(id_x, num(1)), bd(0)))),
			exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0)),num(1)), 
					forall(BD("h","k"), limp(eq(plus(bd(1),apply(id_f, num(5)),minus(num(9), id_y),bd(0)),minus(bd(3),bd(2))), lt(apply(id_f, num(5)),bd(3))))
			)),
			exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0)),num(1)), 
					forall(BD("h","k"), limp(eq(plus(bd(1),apply(id_f, bd(5)),plus(bd(4),num(1)),bd(0)),minus(bd(3),bd(2))), lt(apply(id_f, bd(5)),bd(3))))
			)),
			exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0)),num(1)), 
					forall(BD("h","k"), limp(eq(plus(bd(1),apply(id_f, bd(5)),plus(bd(4),num(1)),bd(0)),minus(bd(3),bd(2))), exists(BD("z"),lt(plus(bd(0),apply(id_f, bd(6))),bd(4)))))
			))
	};
	
	Expression[] sea = new Expression[] {
			plus(id_y, apply(id_f, id_y)),
			plus(id_x, num(1)),
			apply(id_f, num(5)),
			minus(num(9), id_y),
			apply(id_f, bd(3)),
			plus(bd(2),num(1))
	};
	
	Predicate[] tpp = new Predicate[] {
			forall(BD("p","q"), exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0),bd(2)),num(1)), eq(num(0), sea[4])))),
			forall(BD("p","q"), exists(BD("e", "f"), limp(eq(plus(bd(1),bd(0),bd(3)),num(1)), eq(num(0), sea[5]))))
	};
	
	static class UTestItem implements TestItem {
		public final Predicate predicate;
		public final QuantifiedPredicate subpred;
		public final Map<Integer, Expression> map;
		public final Predicate expected;
		
		public UTestItem(Predicate predicate, QuantifiedPredicate subpred, Map<Integer, Expression> map, Predicate expected) {
			this.predicate = predicate;
			this.subpred = subpred;
			this.map = map;
			this.expected = expected;
		}
		
		public void doTest() {
			ITypeCheckResult tresult = predicate.typeCheck(tenv);
			assertTrue(predicate.toString(), tresult.isSuccess());
			
			for(Expression expression : map.values()) {
				ITypeCheckResult tcr = expression.typeCheck(tenv);
				assertTrue(expression.toString(), tcr.isSuccess());
			}
			
			// Type-check the expected result before comparing it
			ITypeCheckResult exptresult = expected.typeCheck(tenv);
			assertTrue(predicate.toString(), exptresult.isSuccess());

			Predicate result = predicate.substituteBoundIdents(subpred, map, ff);
			assertEquals(predicate + "\n" + map + "\n", expected, result);
		}
		
		@Override
		public String toString() {
			return predicate.toString() + " //" + map.toString() + " == " + expected.toString();
		}
	}
	
	static class XTestItem extends UTestItem {
		public final Predicate[] typed;
		
		public XTestItem(Predicate[] typed, Predicate predicate, QuantifiedPredicate subpred, Map<Integer, Expression> map, Predicate result) {
			super(predicate, subpred, map, result);
			this.typed = typed;
		}

		@Override
		public void doTest() {
			ITypeCheckResult tresult = predicate.typeCheck(tenv);
			assertTrue(predicate.toString(), tresult.isSuccess());
			
			for (Predicate pred : typed) {
				ITypeCheckResult tcr = pred.typeCheck(tenv);
				assertTrue("type error " + pred.toString(), tcr.isSuccess());
			}
			
			// Type-check the expected result before comparing it
			ITypeCheckResult exptresult = expected.typeCheck(tenv);
			assertTrue(predicate.toString(), exptresult.isSuccess());

			Predicate result = predicate.substituteBoundIdents(subpred, map, ff);
			assertEquals(predicate + "\n" + map + "\n", expected, result);
		}
		
	}
	
	TestItem[] testItems = new TestItem[] {
			new STestItem(pra[0], ms(mi(id_x), me(exa[0])), prb[0]),
			new STestItem(pra[1], ms(mi(id_f), me(exa[1])), prb[1]),
			new BTestItem(pra[2], mp(pxx[0]), prb[2]),
			new STestItem(pra[3], ms(mi(id_x, id_y), me(id_y, id_x)), prb[3]),
			new UTestItem(spa[0], (QuantifiedPredicate) spa[0], mu(mj(0), me(sea[0])), spr[0]),
			new UTestItem(spa[1], tra[0], mu(mj(0), me(sea[1])), spr[1]),
			new UTestItem(spa[2], tra[1], mu(mj(2,1), me(sea[2],sea[3])), spr[2]),
			new XTestItem(mp(tpp[0],tpp[1]), spa[2], tra[1], mu(mj(2,1), me(sea[4],sea[5])), spr[3]),
			new XTestItem(mp(tpp[0],tpp[1]), spa[3], tra[2], mu(mj(2,1), me(sea[4],sea[5])), spr[4]),
			new BTestItem(pra[2], mp(pxx[0],pxx[1]), prb[4])
	};
	
	public void testSubstitutionStandard() {
		for (TestItem testItem: testItems) {
			testItem.doTest();
		}
	}
	
}
