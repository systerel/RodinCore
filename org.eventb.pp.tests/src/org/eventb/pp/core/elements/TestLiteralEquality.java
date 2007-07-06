package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.pp.AbstractPPTest;

public class TestLiteralEquality extends AbstractPPTest {

	public void testProposition() {
		doEqualTests(cProp(0), cProp(0));
		doEqualTests(cNotProp(0), cNotProp(0));
		doUnequalTests(cProp(0), cProp(1), true);
		doUnequalTests(cNotProp(0), cNotProp(1), true);
		doUnequalTests(cNotProp(0), cProp(0), true);
		doUnequalTests(cProp(0), cPred(0,a), true);
		doUnequalTests(cProp(0), cNotPred(0,a), true);
//		doUnequalTests(cProp(0), cEqual(a, b), true);
//		doUnequalTests(cProp(0), cNEqual(a, b), true);
		doUnequalTests(cNotProp(0), cPred(0,a), true);
		doUnequalTests(cNotProp(0), cNotPred(0,a), true);
//		doUnequalTests(cNotProp(0), cEqual(a, b), true);
//		doUnequalTests(cNotProp(0), cNEqual(a, b), true);
	}
	
	public void testPredicate() {
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(0, a), cPred(0, a));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(0, a), cNotPred(0, a));
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, a), cPred(0, b), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, a), cNotPred(0, b), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, a), cNotPred(0, a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, a), cPred(1, a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, a), cNotPred(1, a), true);
		
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(0, x), cPred(0, x));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(0, x), cNotPred(0, x));
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x), cPred(0, y), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x), cNotPred(0, y), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x), cNotPred(0, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x), cPred(1, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x), cNotPred(1, x), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0,x), cPred(0,a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0,x), cNotPred(0,a), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, y), cPred(0, y, x), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, x), cPred(0, y, x), true);
		
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(0, x, evar0), cPred(0, x, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(0, x, evar0), cNotPred(0, x, evar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cPred(0, x, fvar0), cPred(0, x, fvar0));
		TestLiteralEquality.<PredicateLiteral>doEqualTests(cNotPred(0, x, fvar0), cNotPred(0, x, fvar0));
		
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, evar0), cPred(0, x, evar1), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, evar0), cNotPred(0, x, evar1), false);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, evar0), cPred(0, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, evar0), cNotPred(0, x, fvar0), true);

		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, x), cPred(0, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, x), cNotPred(0, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, x), cPred(0, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, x), cNotPred(0, x, fvar0), true);
		
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, a), cPred(0, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, a), cNotPred(0, x, evar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0, x, a), cPred(0, x, fvar0), true);
		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cNotPred(0, x, a), cNotPred(0, x, fvar0), true);
		
//		TestLiteralEquality.<PredicateLiteral>doUnequalTests(cPred(0,a,b), cEqual(a, b), true);
	}
	
	public void testEquality() {
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, a),cEqual(a, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, a),cNEqual(a, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, b),cEqual(a, b));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, b),cNEqual(a, b));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, b),cEqual(b, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, b),cNEqual(b, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, x),cEqual(x, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, x),cNEqual(x, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(a, evar0),cEqual(evar0, a));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(a, evar0),cNEqual(evar0, a));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, y),cEqual(y, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, y),cNEqual(y, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, y),cEqual(x, y));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, y),cNEqual(x, y));
		
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cEqual(x, evar0),cEqual(evar0, x));
		TestLiteralEquality.<EqualityLiteral>doEqualTests(cNEqual(x, evar0),cNEqual(evar0, x));
		
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(a, a),cEqual(b, b), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(a, b),cEqual(a, c), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cNEqual(a, a),cNEqual(b, b), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cNEqual(a, b),cNEqual(a, c), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(x, y), cEqual(x, x), true);
		TestLiteralEquality.<EqualityLiteral>doUnequalTests(cEqual(x, evar0), cEqual(x, x), true);
	}
	
	public static <T extends Literal<T,?>>  void doEqualTests(T... literals) {
		Literal<T,?> base = literals[0];
		for (int i = 1; i < literals.length; i++) {
			assertEquals(""+base, base, literals[i]);
			// if equal, then it is also equal with different variables
			assertTrue(base.equalsWithDifferentVariables(literals[i], new HashMap<SimpleTerm,SimpleTerm>()));
			assertEquals(base.hashCode(), literals[i].hashCode());
			assertEquals(base.hashCodeWithDifferentVariables(),literals[i].hashCodeWithDifferentVariables());
		}
		
	}
	
	public static <T extends Literal<T,?>> void doUnequalTests(T literal1, T literal2, boolean diffVars) {
		assertFalse(literal1.equals(literal2));
		if (diffVars) assertFalse(literal1.equalsWithDifferentVariables(literal2, new HashMap<SimpleTerm, SimpleTerm>()));
	}
	
}
