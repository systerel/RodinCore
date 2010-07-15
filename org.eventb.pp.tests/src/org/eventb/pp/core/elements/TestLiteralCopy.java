package org.eventb.pp.core.elements;

import static org.eventb.internal.pp.core.elements.terms.Util.cPred;
import static org.eventb.internal.pp.core.elements.terms.Util.d1AA;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.junit.Test;

public class TestLiteralCopy extends AbstractPPTest {

	
    @Test
	public void testSimplePredicate() {
		TestLiteralCopy.<PredicateLiteral>doTest(cPred(d1AA, evar0, evar0));
		TestLiteralCopy.<PredicateLiteral>doTest(cPred(d1AA, evar1, evar0));
	}
	
	
	public static <T extends Literal<T,?>> void doTest(T literal) {
		VariableContext context = new VariableContext();
		
		T copy = literal.getCopyWithNewVariables(context, new HashMap<SimpleTerm, SimpleTerm>());
		assertTrue(copy.equalsWithDifferentVariables(literal, new HashMap<SimpleTerm, SimpleTerm>()));
		
	}
	
}
