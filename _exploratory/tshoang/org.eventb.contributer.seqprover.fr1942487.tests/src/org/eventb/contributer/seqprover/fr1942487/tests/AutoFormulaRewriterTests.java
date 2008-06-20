package org.eventb.contributer.seqprover.fr1942487.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1942487.AutoRewriterImpl;
import org.eventb.core.seqprover.rewriterTests.AbstractFormulaRewriterTests;
import org.junit.Test;

public class AutoFormulaRewriterTests extends AbstractFormulaRewriterTests 
{
	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();
	public AutoFormulaRewriterTests()
	{
		super(rewriter);
	}
	
	@Test
	public void test_upto_remove()
	{
		predicateTest("1≤2∧2≤3", "2∈1‥3");
		predicateTest("b≤a∧a≤c", "a∈b‥c");
		predicateTest("(a∗b)≤(x+y+z)∧(x+y+z)≤(4+t∗3)", "(x+y+z)∈(a∗b)‥(4+t∗3)");		
	}

}
