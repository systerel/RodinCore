package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllmpD;

// import org.eventb.core.seqprover.ITactic;
// import com.b4free.rodin.core.B4freeCore;

/**
 * Unit tests for the AllmpD reasoner
 * 
 * @author Farhad Mehta
 *
 */
public class AllmpDTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new AllmpD()).getReasonerID();
	}
	
	final static Predicate hyp = TestLib.genPred(" ∀x,y· x ∈ ℕ ∧ y ∈ ℕ  ⇒ x ∈ P ∧ y ∈ Q ");
	final static IProverSequent seq = TestLib.genSeq(" ∀x,y· x ∈ ℕ ∧ y ∈ ℕ  ⇒ x ∈ P ∧ y ∈ Q  |- z∈P ");
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		return new SuccessfullReasonerApplication[]{
				// without WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1"})
						),
				// with WD condition
				new SuccessfullReasonerApplication(
								seq,
								new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"z","1÷z"})
								)
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// hyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new AllD.Input(hyp, Lib.makeTypeEnvironment(),new String[]{})),
				// hyp not univ quantified
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new AllD.Input(Lib.True, Lib.makeTypeEnvironment(),new String[]{})),
				// hyp not univ quantified implication
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ∀x· x=0 |- ⊥ "), new AllD.Input(TestLib.genPred("∀x· x=0"), Lib.makeTypeEnvironment(),new String[]{})),
				// not all bound idents instantiated
				new UnsuccessfullReasonerApplication(
								seq,
								new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0"}),
								"Instantiation for bound variable y not provided"
								)

	
		};
	}
	
//  Comitted out, but make tests succeed	
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
