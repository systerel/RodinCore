package org.eventb.contributer.seqprover.fr1866809.tests;

import org.eventb.contributer.seqprover.fr1866809.AutoRewrites;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;

import junit.framework.TestCase;


public class AutoFormulaRewriterTests extends  TestCase
{

	public AutoFormulaRewriterTests()
	{
		rewriter = new AutoRewrites();
		input = new EmptyInput();
	}
	
	private AutoRewrites rewriter;
	private IProverSequent seq;
	private IReasonerInput input;
	private IReasonerOutput output;
	private Predicate pred;
	private IProofRule rule;
	
	
	public void testValids()
	{
		// total function
		seq = TestLib.genSeq("f∈ℕ→ℕ |- dom(f)=ℕ");
		rule = (IProofRule) rewriter.apply(seq, input, null);
		pred=rule.getAntecedents()[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));
		
		// total injection
		seq = TestLib.genSeq("f∈ℕ↣ℕ |- dom(f)=ℕ");
		rule = (IProofRule) rewriter.apply(seq, input, null);
		pred=rule.getAntecedents()[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));
		
		// total bijection
		seq = TestLib.genSeq("f∈ℕ⤖ℕ |- dom(f)=ℕ");
		rule = (IProofRule) rewriter.apply(seq, input, null);
		pred=rule.getAntecedents()[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));
		
		// total relation
		seq = TestLib.genSeq("f∈ℕℕ |- dom(f)=ℕ");
		rule = (IProofRule) rewriter.apply(seq, input, null);
		pred=rule.getAntecedents()[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));

		// total surjection
		seq = TestLib.genSeq("f∈ℕ↠ℕ |- dom(f)=ℕ");
		rule = (IProofRule) rewriter.apply(seq, input, null);
		pred=rule.getAntecedents()[0].getGoal();
		assertTrue(pred.toString().equals("ℕ=ℕ"));
	}
	
	public void testInvalid()
	{
		// relation
		seq = TestLib.genSeq("f∈ℕ↔ℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
		
		// surjective relation
		seq = TestLib.genSeq("f∈ℕℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
		
		// total surjective relation
		seq = TestLib.genSeq("f∈ℕℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
		
		// partial function
		seq = TestLib.genSeq("f∈ℕ⇸ℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
		
		// partial injection
		seq = TestLib.genSeq("f∈ℕ⤔ℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
		
		// partial surjection
		seq = TestLib.genSeq("f∈ℕ⤀ℕ |- dom(f)=ℕ");
		output = rewriter.apply(seq, input, null);
		//pred=output.getAntecedents()[0].getGoal();
		assertTrue(output instanceof IReasonerFailure);
	}
	
}