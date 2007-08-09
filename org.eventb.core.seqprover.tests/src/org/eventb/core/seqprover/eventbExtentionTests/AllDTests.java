package org.eventb.core.seqprover.eventbExtentionTests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;

// import org.eventb.core.seqprover.ITactic;
// import com.b4free.rodin.core.B4freeCore;

/**
 * Unit tests for the AllD reasoner
 * 
 * @author Farhad Mehta
 *
 * TODO : make these tests independant of the integer type (use an arbitrary carrier set instead)
 * TODO : After that, add expected new sequents
 */
public class AllDTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return (new AllD()).getReasonerID();
	}
	
	final static IProverSequent seq = TestLib.genSeq(" ∀x,y· x∈ℤ ⇒ x∈P ∧ y∈P  |- ⊥ ");
	final static Predicate hyp = TestLib.getFirstHyp(seq);
	
	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {

		return new SuccessfullReasonerApplication[]{
				// hyp not univ quantified implication, but still univ quantified
				new SuccessfullReasonerApplication(TestLib.genSeq(" ∀x· x=0 |- ⊥ "), 
						new AllD.Input(TestLib.genPred("∀x· x=0"), Lib.makeTypeEnvironment(),new String[]{"0"}),
						"[{}[][][∀x·x=0] |- ⊤," +
						" {}[][][∀x·x=0, 0=0] |- ⊥]"
				),
				// without WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1"})
				),
				// with WD condition
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0","1÷0"}),
						"[{P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P] |- 0≠0," +
						" {P=ℙ(ℤ)}[][][∀x,y·x∈ℤ⇒x∈P∧y∈P, 0≠0, 0∈ℤ⇒0∈P∧1 ÷ 0∈P] |- ⊥]"
				),
				// not all bound idents instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0"})
				),
				// only first bound ident instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{"0", null})
				),
				// only second bound ident instantiated
				new SuccessfullReasonerApplication(
						seq,
						new AllD.Input(hyp,seq.typeEnvironment(),new String[]{null, "0"})
				),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		return new UnsuccessfullReasonerApplication[]{
				// hyp not present
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new AllD.Input(hyp, Lib.makeTypeEnvironment(),new String[]{})),
				// hyp not univ quantified
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊥ "), new AllD.Input(Lib.True, Lib.makeTypeEnvironment(),new String[]{})),	
		};
	}
	
//	// Comitted out, but make tests succeed	
//	@Override
//	public ITactic getJustDischTactic() {
//		return  
//		B4freeCore.externalPP(false);
//	}

}
