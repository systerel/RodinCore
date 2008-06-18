package org.eventb.contributer.seqprover.fr1935674.tests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.contributer.seqprover.fr1935674.AutoRewriterImpl;
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
	public void test_de_morgan()
	{
		// test and and or with one and with two operators
		predicateTest("∀i,j·i∈ℕ∧j∈ℕ⇒¬(i=j)∧¬(j=i)", "∀i,j·i∈ℕ∧j∈ℕ⇒¬((i=j)∨(j=i)) ");
		predicateTest("∀i,j·i∈ℕ∧j∈ℕ⇒¬(i=j)∨¬(j=i)", "∀i,j·i∈ℕ∧j∈ℕ⇒¬((i=j)∧(j=i)) ");
		predicateTest("∀i,j,k·i∈ℕ∧j∈ℕ∧k∈ℕ⇒¬(i=j)∧¬(k=i)∧¬(k=j)", "∀i,j,k·i∈ℕ∧j∈ℕ∧k∈ℕ⇒¬((i=j)∨(k=i)∨(k=j))");
		predicateTest("∀i,j,k·i∈ℕ∧j∈ℕ∧k∈ℕ⇒¬(i=j)∨¬(k=i)∨¬(k=j)", "∀i,j,k·i∈ℕ∧j∈ℕ∧k∈ℕ⇒¬((i=j)∧(k=i)∧(k=j))");
		
		// test forall and exists with a simple and a associative predicate
		predicateTest("∃i·¬(i∈ℕ⇒i=0)", "¬(∀i·i∈ℕ⇒i=0)");
		predicateTest("∀i·¬(i∈ℕ⇒i=0)", "¬(∃i·i∈ℕ⇒i=0)");
		predicateTest("∃i,j·¬(i∈ℕ∧j∈ℕ⇒i=0∨j=0)", "¬(∀i,j·i∈ℕ∧j∈ℕ⇒i=0∨j=0)");
		predicateTest("∀i,j·¬(i∈ℕ∧j∈ℕ⇒i=0∨j=0)", "¬(∃i,j·i∈ℕ∧j∈ℕ⇒i=0∨j=0)");
	}

}
//¬ ∧ ∨ 