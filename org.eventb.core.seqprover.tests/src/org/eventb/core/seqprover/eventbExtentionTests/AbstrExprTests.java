package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstrExpr;
import org.junit.Test;

/**
 * Unit tests for the Abstract expression reasoner
 * 
 * TODO : test that WD lemmas are also added to the hyps
 * 
 * @author Farhad Mehta
 */
public class AbstrExprTests {

	private static final IReasoner reasoner = new AbstrExpr();
	
	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testFailure(){
		IProverSequent seq;
		IReasonerOutput output;
				
		// Expression not parsable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = reasoner.apply(seq, new SingleExprInput("@unparsable@",seq.typeEnvironment()), null);
		assertTrue(output instanceof IReasonerFailure);
		
		// Expression not typecheckable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = reasoner.apply(seq, new SingleExprInput("x",seq.typeEnvironment()), null);
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
				
		
		seq = TestLib.genSeq(" x=1 ;; x+1 = 2 |- (x+1)+1 = 3 ");
		output = reasoner.apply(seq, new SingleExprInput("x+1",seq.typeEnvironment()), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule)output).apply(seq);
		assertTrue(newSeqs.length == 2);
		// Note: this test is pretty printer dependent. Change string (after inspection)
		// in case pretty printer is modified
		assertTrue(newSeqs[0].toString().equals("{x=ℤ}[][][x=1, x+1=2] |- ⊤"));
		assertTrue(newSeqs[1].toString().equals("{ae=ℤ, x=ℤ}[][][x=1, x+1=2, ae=x+1] |- (x+1)+1=3"));
		
	}
	
}
