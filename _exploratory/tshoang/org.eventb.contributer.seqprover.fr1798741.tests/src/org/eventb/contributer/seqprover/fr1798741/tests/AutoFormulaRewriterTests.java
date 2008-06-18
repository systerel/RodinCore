package org.eventb.contributer.seqprover.fr1798741.tests;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1798741.AutoRewriterImpl;
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
		Expression emptySetInteger =
			ff.makeEmptySet(ff.makePowerSetType(ff.makeIntegerType()), null);
		Expression emptySetIntegerProduct = ff.makeEmptySet(ff.makePowerSetType(
				ff.makeProductType(ff.makeIntegerType(), ff.makeIntegerType())), null);
		
		Expression inputExp = makeInputExpression("{1↦2}[∅]");
		expressionTest(emptySetInteger, inputExp);

		inputExp = makeInputExpression("{1↦2, 1↦-2}[∅]");
		expressionTest(emptySetInteger, inputExp);
		
		inputExp = makeInputExpression("{-4↦2, -3↦-2, -1↦1}[∅]");
		expressionTest(emptySetInteger, inputExp);
		
		// ∅[{1, 2, 3}]
		inputExp = ff.makeBinaryExpression(Expression.RELIMAGE,
				emptySetIntegerProduct, makeInputExpression("{1, 2, 3}"), null);
		expressionTest(emptySetInteger, inputExp);
		
		// ∅[∅]
		inputExp = ff.makeBinaryExpression(Expression.RELIMAGE,
				emptySetIntegerProduct, emptySetInteger, null);
		expressionTest(emptySetInteger, inputExp);
		
	}

}
