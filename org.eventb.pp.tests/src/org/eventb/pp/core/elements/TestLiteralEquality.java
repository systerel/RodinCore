package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cEqual;
import static org.eventb.pp.Util.cNEqual;
import static org.eventb.pp.Util.cNotPred;
import static org.eventb.pp.Util.cNotProp;
import static org.eventb.pp.Util.cPred;
import static org.eventb.pp.Util.cProp;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
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
		doUnequalTests(cProp(0), cEqual(a, b), true);
		doUnequalTests(cProp(0), cNEqual(a, b), true);
		doUnequalTests(cNotProp(0), cPred(0,a), true);
		doUnequalTests(cNotProp(0), cNotPred(0,a), true);
		doUnequalTests(cNotProp(0), cEqual(a, b), true);
		doUnequalTests(cNotProp(0), cNEqual(a, b), true);
	}
	
	public void testPredicate() {
		doEqualTests(cPred(0, a), cPred(0, a));
		doEqualTests(cNotPred(0, a), cNotPred(0, a));
		doUnequalTests(cPred(0, a), cPred(0, b), true);
		doUnequalTests(cNotPred(0, a), cNotPred(0, b), true);
		doUnequalTests(cPred(0, a), cNotPred(0, a), true);
		doUnequalTests(cPred(0, a), cPred(1, a), true);
		doUnequalTests(cNotPred(0, a), cNotPred(1, a), true);
		
		doEqualTests(cPred(0, x), cPred(0, x));
		doEqualTests(cNotPred(0, x), cNotPred(0, x));
		doUnequalTests(cPred(0, x), cPred(0, y), false);
		doUnequalTests(cNotPred(0, x), cNotPred(0, y), false);
		doUnequalTests(cPred(0, x), cNotPred(0, x), true);
		doUnequalTests(cPred(0, x), cPred(1, x), true);
		doUnequalTests(cNotPred(0, x), cNotPred(1, x), true);
		doUnequalTests(cPred(0,x), cPred(0,a), true);
		doUnequalTests(cNotPred(0,x), cNotPred(0,a), true);
		doUnequalTests(cPred(0, x, y), cPred(0, y, x), false);
		doUnequalTests(cPred(0, x, x), cPred(0, y, x), true);
		
		doEqualTests(cPred(0, x, evar0), cPred(0, x, evar0));
		doEqualTests(cNotPred(0, x, evar0), cNotPred(0, x, evar0));
		doEqualTests(cPred(0, x, fvar0), cPred(0, x, fvar0));
		doEqualTests(cNotPred(0, x, fvar0), cNotPred(0, x, fvar0));
		
		doUnequalTests(cPred(0, x, evar0), cPred(0, x, evar1), false);
		doUnequalTests(cNotPred(0, x, evar0), cNotPred(0, x, evar1), false);
		doUnequalTests(cPred(0, x, evar0), cPred(0, x, fvar0), true);
		doUnequalTests(cNotPred(0, x, evar0), cNotPred(0, x, fvar0), true);

		doUnequalTests(cPred(0, x, x), cPred(0, x, evar0), true);
		doUnequalTests(cNotPred(0, x, x), cNotPred(0, x, evar0), true);
		doUnequalTests(cPred(0, x, x), cPred(0, x, fvar0), true);
		doUnequalTests(cNotPred(0, x, x), cNotPred(0, x, fvar0), true);
		
		doUnequalTests(cPred(0, x, a), cPred(0, x, evar0), true);
		doUnequalTests(cNotPred(0, x, a), cNotPred(0, x, evar0), true);
		doUnequalTests(cPred(0, x, a), cPred(0, x, fvar0), true);
		doUnequalTests(cNotPred(0, x, a), cNotPred(0, x, fvar0), true);
		
		doUnequalTests(cPred(0,a,b), cEqual(a, b), true);
	}
	
	public void testEquality() {
		doEqualTests(cEqual(a, a),cEqual(a, a));
		doEqualTests(cNEqual(a, a),cNEqual(a, a));
		doEqualTests(cEqual(a, b),cEqual(a, b));
		doEqualTests(cNEqual(a, b),cNEqual(a, b));
		doEqualTests(cEqual(a, b),cEqual(b, a));
		doEqualTests(cNEqual(a, b),cNEqual(b, a));
		
		doEqualTests(cEqual(a, x),cEqual(x, a));
		doEqualTests(cNEqual(a, x),cNEqual(x, a));
		
		doEqualTests(cEqual(a, evar0),cEqual(evar0, a));
		doEqualTests(cNEqual(a, evar0),cNEqual(evar0, a));
		
		doEqualTests(cEqual(x, y),cEqual(y, x));
		doEqualTests(cNEqual(x, y),cNEqual(y, x));
		doEqualTests(cEqual(x, y),cEqual(x, y));
		doEqualTests(cNEqual(x, y),cNEqual(x, y));
		
		doEqualTests(cEqual(x, evar0),cEqual(evar0, x));
		doEqualTests(cNEqual(x, evar0),cNEqual(evar0, x));
		
		doUnequalTests(cEqual(a, a),cEqual(b, b), true);
		doUnequalTests(cEqual(a, b),cEqual(a, c), true);
		doUnequalTests(cNEqual(a, a),cNEqual(b, b), true);
		doUnequalTests(cNEqual(a, b),cNEqual(a, c), true);
		doUnequalTests(cEqual(x, y), cEqual(x, x), true);
		doUnequalTests(cEqual(x, evar0), cEqual(x, x), true);
	}
	
	public void doEqualTests(ILiteral<?>... literals) {
		ILiteral<?> base = literals[0];
		for (int i = 1; i < literals.length; i++) {
			assertEquals(""+base, base, literals[i]);
			// if equal, then it is also equal with different variables
			assertTrue(base.equalsWithDifferentVariables(literals[i], new HashMap<AbstractVariable,AbstractVariable>()));
			assertEquals(base.hashCode(), literals[i].hashCode());
			assertEquals(base.hashCodeWithDifferentVariables(),literals[i].hashCodeWithDifferentVariables());
		}
		
	}
	
	public void doUnequalTests(ILiteral<?> literal1, ILiteral<?> literal2, boolean diffVars) {
		assertFalse(literal1.equals(literal2));
		if (diffVars) assertFalse(literal1.equalsWithDifferentVariables(literal2, new HashMap<AbstractVariable, AbstractVariable>()));
	}
	
}
