package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AutoImpF;
import org.junit.Test;

/**
 * Unit tests for the AutoImpE reasoner
 * 
 * @author Farhad Mehta
 */
public class AutoImpETests {

	private static final IReasoner autoImpEreasoner = new AutoImpF();
	
	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testFailure(){
		IProverSequent seq;
		IReasonerOutput output;

		// impHyp not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = autoImpEreasoner.apply(seq, new EmptyInput(), null);
		assertTrue(output instanceof IReasonerFailure);
		
		// impHyp not usable
		seq = TestLib.genSeq(" 1=1 ⇒ 2=2 ;; 2=2 |- ⊤ ");
		output = autoImpEreasoner.apply(seq, new EmptyInput(), null);
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
				
		// test partial impE
		seq = TestLib.genSeq(" 1=1 ;; 1=1 ∧ 2=2 ⇒ 3=3 |- 4 = 4 ");
		output = autoImpEreasoner.apply(seq, new EmptyInput(), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule)output).apply(seq);
		assertTrue(newSeqs.length == 1);
		// Note: this test is pretty printer dependent. Change string (after inspection)
		// in case pretty printer is modified
		assertTrue(newSeqs[0].toString().equals("{}[1=1∧2=2⇒3=3][][1=1, 2=2⇒3=3] |- 4=4"));
		
		// test total impE
		seq = TestLib.genSeq(" 1=1 ;; 2=2 ;; 1=1 ∧ 2=2 ⇒ 3=3 |- 4 = 4 ");
		output = autoImpEreasoner.apply(seq, new EmptyInput(), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule)output).apply(seq);
		assertTrue(newSeqs.length == 1);
		// Note: this test is pretty printer dependent. Change string (after inspection)
		// in case pretty printer is modified
		assertTrue(newSeqs[0].toString().equals("{}[1=1∧2=2⇒3=3][][1=1, 2=2, 3=3] |- 4=4"));
		
	}
	
}
