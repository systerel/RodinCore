package org.eventb.pp.core.simplifiers;

import static org.eventb.pp.Util.cClause;
import static org.eventb.pp.Util.cEqClause;
import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cPlus;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.mList;
import junit.framework.TestCase;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;
import org.eventb.pp.Util;

/**
 * This class tests the one point rule. There are several tests :
 * <ul>
 * 	<li>one-point rule on non-arithmetic</li>
 * 	<li>one-point rule on arithmetic with single expressions</li>
 * 	<li>one-point rule on arithmetic with complex expressions</li>
 * </ul>
 *
 * @author François Terrier
 *
 */
public class TestOnePoint extends TestCase {

	private class TestPair {
		IClause input, output;
		
		TestPair(IClause input, IClause output) {
			this.input = input;
			this.output = output;
		}
	}
	
	private static Constant a = Util.cCons("a");
	private static Constant b = Util.cCons("b");
	private static Constant c = Util.cCons("c");
	
	private static Variable var0 = Util.cVar();
	private static Variable var00 = Util.cVar();
	private static Variable var1 = Util.cVar();
	private static Variable var2 = Util.cVar();
	private static Variable var3 = Util.cVar();
	
	private static LocalVariable fvar0 = Util.cFLocVar(0);
	private static LocalVariable fvar1 = Util.cFLocVar(1);
	private static LocalVariable fvar2 = Util.cFLocVar(2);
	private static LocalVariable evar0 = Util.cELocVar(0);
	private static LocalVariable evar1 = Util.cELocVar(1);
	private static LocalVariable evar2 = Util.cELocVar(2);
	
	TestPair[] tests = new TestPair[] {
			// standard test
			new TestPair(
					cClause(cNEqual(var0, a), cPred(0, var0)),
					cClause(cPred(0, a))
			),
			// no rule applies
			new TestPair(
					cClause(cNEqual(a, b), cPred(0, a)),
					cClause(cNEqual(a, b), cPred(0, a))
			),
			// no rule applies either, variable is in expression
			// border case, see what to do with it
			// TODO this test will change with arithmetic
			new TestPair(
					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(0, var0)),
					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(0, var0))
			),
			new TestPair(
					cClause(cNEqual(var0, var00), cPred(0, var0)),
					cClause(cPred(0, var00))
			),
			// more variables
			new TestPair(
					cClause(cNEqual(var0, a), cNEqual(var1, b), cPred(0, var0, var1)),
					cClause(cPred(0, a, b))
			),
			// more literals
			new TestPair(
					cClause(cNEqual(var0, a), cPred(0, var0), cPred(1, var0)),
					cClause(cPred(0, a), cPred(1, a))
			),
			// different literals
			new TestPair(
					cClause(cNEqual(var0, a), cEqual(var0, b)),
					cClause(cEqual(a, b))
			),
			// 2 inequalities
			new TestPair(
					cClause(cNEqual(var0, a), cNEqual(var0, b)),
					cClause(cNEqual(a, b))
			),
			
			// EXISTENTIAL
			// TODO add a proof of this one in thesis
			new TestPair(
					cClause(cPred(0,var0),cNEqual(evar0,var0)),
					cClause(cPred(0,evar0))
			),
			new TestPair(
					cClause(cPred(0,var0),cPred(1,var0),cNEqual(evar0,var0)),
					cClause(cPred(0,evar0),cPred(1,evar0))
			),
			
			// EQUIVALENCE
			// standard test
			new TestPair(
					cEqClause(cNEqual(var0, a), cPred(0, var0)),
					cEqClause(cNEqual(var0, a), cPred(0, var0))
			),
			new TestPair(
					cEqClause(mList(cPred(0, var0),cPred(1,var0)), cNEqual(var0, a)),
					cEqClause(cPred(0, a),cPred(1, a))
			),
			new TestPair(
					cEqClause(mList(cNEqual(var0,a),cNEqual(a,var0)),cNEqual(var0,a)),
					cEqClause(cNEqual(a,a),cNEqual(a,a))
			),
			
			//
			new TestPair(
					cEqClause(mList(cPred(0,var0),cPred(1,var0)),cNEqual(var0,a),cNEqual(b,b)),
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(b,b))
			),
			
			// EQUALITY alone
			new TestPair(
					cClause(cNEqual(var0, var1)),
					cClause()
			),
			new TestPair(
					cClause(cNEqual(var0, var1),cNEqual(var2, var3)),
					cClause()
			),
			new TestPair(
					cClause(cNEqual(var0, a)),
					cClause()
			),
	};
	
	TestPair[] testNotOnePoint = new TestPair[] {
			// standard test
			new TestPair(
					cClause(cEqual(var0, a), cPred(0, var0)),
					cClause(cEqual(var0, a), cPred(0, var0))
			)
	};
	
	private OnePointRule rule = new OnePointRule();
	
	public void testOnePoint() {
		for (TestPair test : tests) {
			IClause actual = test.input.simplify(rule);
			assertTrue(rule.canSimplify(test.input));
			assertEquals(test.output,actual);
		}
	}
	
	public void testNotOnePoint() {
		for (TestPair test : testNotOnePoint) {
			IClause actual = test.input.simplify(rule);
			assertTrue(rule.canSimplify(test.input));
			assertEquals(test.output, actual);
		}
	}
	
}
