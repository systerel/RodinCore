package org.eventb.pp.core.elements;

import static org.eventb.pp.Util.cPred;

import java.util.HashMap;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.loader.clause.VariableContext;
import org.eventb.pp.AbstractPPTest;

public class TestLiteralCopy extends AbstractPPTest {

	
	public void testSimplePredicate() {
		TestLiteralCopy.<PredicateLiteral>doTest(cPred(1, evar0, evar0));
		TestLiteralCopy.<PredicateLiteral>doTest(cPred(1, evar1, evar0));
	}
	
	
	public static <T extends Literal<T,?>> void doTest(T literal) {
		VariableContext context = new VariableContext();
		
		T copy = literal.getCopyWithNewVariables(context, new HashMap<SimpleTerm, SimpleTerm>());
		assertTrue(copy.equalsWithDifferentVariables(literal, new HashMap<SimpleTerm, SimpleTerm>()));
		
	}
	
}
