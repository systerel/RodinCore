package org.eventb.contributer.seqprover.fr1834715.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1834715.AutoRewriterImpl;
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
	public void test_overwriting()
	{
		// A ⊂ B  ==  A ∈ ℙ(B) ∧ A ≠ B
		predicateTest("{1,2} ∈ ℙ({1,2,3}) ∧ {1,2} ≠ {1,2,3}", "{1,2} ⊂ {1,2,3}");
		predicateTest("{1} ∈ ℙ({1,2}) ∧ {1} ≠ {1,2}", "{1} ⊂ {1,2}");
		predicateTest("{1} ∈ ℙ({1}) ∧ {1} ≠ {1}", "{1} ⊂ {1}");
		predicateTest("{1,2} ∈ ℙ({1}) ∧ {1,2} ≠ {1}", "{1,2} ⊂ {1}");
		predicateTest("{1,2,3} ∈ ℙ({1,2}) ∧ {1,2,3} ≠ {1,2}", "{1,2,3} ⊂ {1,2}");
	}

}
