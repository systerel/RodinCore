package org.eventb.core.seqprover.eventbExtentionTests;

import junit.framework.TestCase;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;

/**
 * Unit tests for the Eq reasoner
 * 
 * @author Farhad Mehta
 */
public class EqTests extends TestCase {

	private static final IReasoner eqReasoner = new Eq();
	
	/**
	 * Tests for correct reasoner failure
	 */
	public void testFailure(){
		IProverSequent seq;
		IReasonerOutput output;
		
		final Predicate oneEqTwo = TestLib.genPred("1=2");
		
		// eqHyp not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = eqReasoner.apply(seq, new SinglePredInput(oneEqTwo), null);
		assertTrue(output instanceof IReasonerFailure);
		
		// eqHyp not an equality
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = eqReasoner.apply(seq, new SinglePredInput(TestLib.genPred("⊤")), null);
		assertTrue(output instanceof IReasonerFailure);
		
		// nothing to do
		seq = TestLib.genSeq(" 1=2 ;; ⊤ |- ⊤ ");
		output = eqReasoner.apply(seq, new SinglePredInput(TestLib.getHypRef(seq,oneEqTwo)), null);
		assertTrue(output instanceof IReasonerFailure);
	}
	
	/**
	 * Tests for reasoner success
	 */
	public void testSuccess(){
		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;
		
		final Predicate oneEqTwo = TestLib.genPred("1=2");
				
		// nothing to do
		seq = TestLib.genSeq(" 1=2 ;; 1+1 = 2 |- 1+1+1 = 3 ");
		output = eqReasoner.apply(seq, new SinglePredInput(TestLib.getHypRef(seq,oneEqTwo)), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule)output).apply(seq);
		assertTrue(newSeqs.length == 1);
		// Note: this test is pretty printer dependent. Change string (after inspection)
		// in case pretty printer is modified
		assertTrue(newSeqs[0].toString().equals("{}[][1+1=2, 1=2][2+2=2] |- 2+2+2=3"));
	}
	
}
