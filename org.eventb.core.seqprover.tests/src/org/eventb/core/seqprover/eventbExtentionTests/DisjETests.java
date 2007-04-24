package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.junit.Test;

/**
 * Unit tests for the ImpE reasoner
 * 
 * @author Farhad Mehta
 */
public class DisjETests {

	private static final IReasoner reasoner = new DisjE();
	
	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testFailure(){
		IProverSequent seq;
		IReasonerOutput output;
		
		final Predicate oneOrTwo = TestLib.genPred("1=1 ∨ 2=2");
		
		// hyp not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = reasoner.apply(seq, new HypothesisReasoner.Input(oneOrTwo), null);
		assertTrue(output instanceof IReasonerFailure);
		
		// hyp not an implication
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = reasoner.apply(seq, new HypothesisReasoner.Input(TestLib.genPred("⊤")), null);
		assertTrue(output instanceof IReasonerFailure);
		
	}
	
	/**
	 * Tests for reasoner success
	 */
	@Test
	public void testSuccess(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;
		
		final Predicate oneOrTwo = TestLib.genPred("1=1 ∨ 2=2");
				
		seq = TestLib.genSeq(" 1=1 ∨ 2=2  |- ⊤");
		output = reasoner.apply(seq, new HypothesisReasoner.Input(TestLib.getHypRef(seq,oneOrTwo)), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule)output).apply(seq);
		assertTrue(newSeqs.length == 2);
		// Note: this test is pretty printer dependent. Change string (after inspection)
		// in case pretty printer is modified
		assertTrue(newSeqs[0].toString().equals("{}[][1=1∨2=2][1=1] |- ⊤"));
		assertTrue(newSeqs[1].toString().equals("{}[][1=1∨2=2][2=2] |- ⊤"));
		
		// Rule is not goal dependent
		assertTrue(((IProofRule)output).getGoal() == null);
		
	}
	
}
