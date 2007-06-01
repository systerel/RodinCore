package org.eventb.core.seqprover.reasonerExtentionTests;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
//import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;

//import com.b4free.rodin.core.B4freeCore;

public class ImpAndTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.impAndRewrites";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		IReasonerInput input;
		Predicate pred;
		FormulaFactory ff = FormulaFactory.getDefault();

		// Applicable at the root in goal
		pred = Lib.parsePredicate("x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition(""));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 "), input,
				"[{x=ℤ}[][][⊤] |- (x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)]"));
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition(""));

		// Applicable at the root in hypothesis
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 |- ⊤ "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 |- ⊤ "), input,
				"[{x=ℤ}[x=0⇒x=1∧x=2∧x=3][][(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)] |- ⊤]"));
		
		// Applicable at the right hand-side of an implication in goal
		pred = Lib.parsePredicate("x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition("1"));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input,
				"[{x=ℤ}[][][⊤] |- x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)]"));

		// Applicable at the right hand-side of an implication in hypothesis
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition("1"));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input,
				"[{x=ℤ}[x=4⇒(x=0⇒x=1∧x=2∧x=3)][][x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)] |- ⊤]"));

		// Applicable at the right hand-side of an implication inside a
		// quantified predicate in goal
		pred = Lib.parsePredicate("∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition("1.1"));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input,
				"[{}[][][⊤] |- ∀x·x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)]"));

		// Applicable at the right hand-side of an implication inside a
		// quantified predicate in hypothesis
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition("1.1"));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input));
		successfullReasonerApps.add(new SuccessfullReasonerApplication(TestLib
				.genSeq(" ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input,
				"[{}[∀x·x=4⇒(x=0⇒x=1∧x=2∧x=3)][][∀x·x=4⇒(x=0⇒x=1)∧(x=0⇒x=2)∧(x=0⇒x=3)] |- ⊤]"));

		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();
		IReasonerInput input;
		Predicate pred;
		FormulaFactory ff = FormulaFactory.getDefault();
		pred = Lib.parsePredicate("x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3");
		pred.typeCheck(ff.makeTypeEnvironment());

		// Hypothesis does not exist
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition(""));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 |- ⊤ "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 |- ⊤ "), input,
				"Nonexistent hypothesis: x=0⇒x=1∧x=2∧x=3"));

		// Goal is not applicable
		pred = Lib.parsePredicate("x = 0 ⇒ x = 1");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition(""));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1 "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1"), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for goal x=0⇒x=1"));
		
		// Hypothesis is not applicable
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition(""));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 |- ⊤ "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 |- ⊤ "), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for hypothesis x=0⇒x=1"));
		
		// Incorrect position in goal
		pred = Lib.parsePredicate("x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition("0"));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3"), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for goal x=0⇒x=1∧x=2∧x=3"));

		// Incorrect position in hypothesis
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition("0"));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 |- ⊤ "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3 |- ⊤ "), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for hypothesis x=0⇒x=1∧x=2∧x=3"));
		
		// Incorrect position (inside a quantified predicate) in goal
		pred = Lib.parsePredicate("∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3)");
		pred.typeCheck(ff.makeTypeEnvironment());
		input = new AbstractManualRewrites.Input(null, ff
				.makePosition("1.0"));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ⊤ |- ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) "), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for goal ∀x·x=4⇒(x=0⇒x=1∧x=2∧x=3)"));

		// Incorrect position (inside a quantified predicate) in hypothesis
		input = new AbstractManualRewrites.Input(pred, ff
				.makePosition("1.0"));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(TestLib
				.genSeq(" ∀x·x = 4 ⇒ (x = 0 ⇒ x = 1 ∧ x = 2 ∧ x = 3) |- ⊤ "), input,
				"Rewriter "	+ getReasonerID() + " is inapplicable for hypothesis ∀x·x=4⇒(x=0⇒x=1∧x=2∧x=3)"));

		return unsuccessfullReasonerApps
				.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]); 
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
