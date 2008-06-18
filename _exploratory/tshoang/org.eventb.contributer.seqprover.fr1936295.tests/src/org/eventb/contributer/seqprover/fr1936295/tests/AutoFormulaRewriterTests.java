package org.eventb.contributer.seqprover.fr1936295.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1936295.AutoRewriterImpl;
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
		expressionTest("{1↦1}", "{1↦1}  ∅");
		expressionTest("{1↦5, 3↦1}", "{1↦5, 3↦1}  ∅");
		expressionTest("{1↦5, 3↦1}", "∅  {1↦5, 3↦1}");
		expressionTest("{-2↦-1, 3↦-1}  {1↦1}", "{-2↦-1, 3↦-1}  ∅  {1↦1}");
		expressionTest("{1↦5, 3↦1}  {1↦1}", "∅  {1↦5, 3↦1}  ∅  ∅  {1↦1}  ∅");
	}

}
