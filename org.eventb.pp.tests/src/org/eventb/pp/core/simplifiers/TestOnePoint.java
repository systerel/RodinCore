package org.eventb.pp.core.simplifiers;

import static org.eventb.internal.pp.core.elements.terms.Util.cClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqClause;
import static org.eventb.internal.pp.core.elements.terms.Util.cEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cNEqual;
import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.mList;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.simplifiers.OnePointRule;

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
public class TestOnePoint extends AbstractPPTest {

	
	public void testOnePoint() {
		// standard test
			testOnePoint(
					cClause(cNEqual(var0, a), cPred(0, var0)),
					cClause(cPred(0, a))
			);
			// no rule applies
			testOnePoint(
					cClause(cNEqual(a, b), cPred(0, a)),
					cClause(cNEqual(a, b), cPred(0, a))
			);
			// no rule applies either, variable is in expression
			// border case, see what to do with it
			// TODO this test will change with arithmetic
//			testOnePoint(
//					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(0, var0)),
//					cClause(cNEqual(var0, cPlus(var1,var0)), cPred(0, var0))
//			);
			testOnePoint(
					cClause(cNEqual(var0, var00), cPred(0, var0)),
					cClause(cPred(0, var00))
			);
			// more variables
			testOnePoint(
					cClause(cNEqual(var0, a), cNEqual(var1, b), cPred(0, var0, var1)),
					cClause(cPred(0, a, b))
			);
			// more literals
			testOnePoint(
					cClause(cNEqual(var0, a), cPred(0, var0), cPred(1, var0)),
					cClause(cPred(0, a), cPred(1, a))
			);
			// different literals
			testOnePoint(
					cClause(cNEqual(var0, a), cEqual(var0, b)),
					cClause(cEqual(a, b))
			);
			// 2 inequalities
			testOnePoint(
					cClause(cNEqual(var0, a), cNEqual(var0, b)),
					cClause(cNEqual(a, b))
			);
			
			// EQUIVALENCE
			// standard test
			testOnePoint(
					cEqClause(cNEqual(var0, a), cPred(0, var0)),
					cEqClause(cNEqual(var0, a), cPred(0, var0))
			);
			testOnePoint(
					cEqClause(mList(cPred(0, var0),cPred(1,var0)), cNEqual(var0, a)),
					cEqClause(cPred(0, a),cPred(1, a))
			);
			testOnePoint(
					cEqClause(mList(cNEqual(var0,a),cNEqual(a,var0)),cNEqual(var0,a)),
					cEqClause(cNEqual(a,a),cNEqual(a,a))
			);
			
			//
			testOnePoint(
					cEqClause(mList(cPred(0,var0),cPred(1,var0)),cNEqual(var0,a),cNEqual(b,b)),
					cEqClause(mList(cPred(0,a),cPred(1,a)),cNEqual(b,b))
			);
			
			// Disjunctive with conditions
			testOnePoint(
					cClause(mList(cPred(0,var0)),cNEqual(var0,a)),
					cClause(cPred(0,a))
			);
			
			// EQUALITY alone
			testOnePoint(
					cClause(cNEqual(var0, var1)),
					FALSE
			);
			testOnePoint(
					cClause(cNEqual(var0, var1),cNEqual(var2, var3)),
					FALSE
			);
			testOnePoint(
					cClause(cNEqual(var0, a)),
					FALSE
			);
			
			
			// simple equality with a variable and a local variable
			testOnePoint(
					cClause(cEqual(evar0, var0)),
					TRUE
			);		
			testOnePoint(
					cClause(cEqual(evar0, evar1)),
					TRUE
			);		
	}
	
	public void testNotOnePoint () {
			// standard test
			testOnePoint(
					cClause(cEqual(var0, a), cPred(0, var0)),
					cClause(cEqual(var0, a), cPred(0, var0))
			);
			
			
			testOnePoint(
					cClause(cNEqual(evar0, var1)),
					cClause(cNEqual(evar0, var1))
			);
			
			// EXISTENTIAL
			testOnePoint(
					cClause(cPred(0,var0),cNEqual(evar0,var0)),
					cClause(cPred(0,var0),cNEqual(evar0,var0))
			);
			testOnePoint(
					cClause(cPred(0,var0),cNEqual(var0,evar0)),
					cClause(cPred(0,var0),cNEqual(var0,evar0))
			);
			testOnePoint(
					cClause(cPred(0,var0),cPred(1,var0),cNEqual(evar0,var0)),
					cClause(cPred(0,var0),cPred(1,var0),cNEqual(evar0,var0))
			);
			
			testOnePoint(
					cClause(cPred(0, var0),cEqual(evar0, evar0)),
					cClause(cPred(0, var0),cEqual(evar0, evar0))
			);
	}
	
	private OnePointRule rule = new OnePointRule();
	
	public void testOnePoint(Clause input, Clause output) {
		Clause actual = input.simplify(rule);
		assertTrue(rule.canSimplify(input));
		
		if (actual.isFalse()) assertTrue(output.isFalse());
		else if (actual.isTrue()) assertTrue(output.isTrue());
		else assertEquals(output,actual);
	}
	
	public void testNotOnePoint(Clause input, Clause output) {
		Clause actual = input.simplify(rule);
		assertTrue(rule.canSimplify(input));
		assertEquals(output, actual);
	}
	
}
