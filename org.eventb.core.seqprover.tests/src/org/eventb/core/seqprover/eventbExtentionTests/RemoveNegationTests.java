package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import junit.framework.TestCase;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.core.seqprover.tests.Util;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class RemoveNegationTests extends TestCase {

	private static final IReasoner rnReasoner = new RemoveNegation();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	Predicate P1 = TestLib.genPred("(0 = 1) ⇒ (¬ (1 = 2 ∧ 2 = 3 ∧ 3 = 4))");

	Predicate P2 = TestLib.genPred("∀x·x = 0 ⇒ (¬ (x = 1 ∧ x = 2 ∧ x = 3))");

	Predicate P3 = TestLib
			.genPred("(0 = 1) ⇒ (¬ (1 = 2 \u2228 2 = 3 \u2228 3 = 4))");

	Predicate P4 = TestLib
			.genPred("∀x·x = 0 ⇒ (¬ (x = 1 \u2228 x = 2 \u2228 x = 3))");

	Predicate P5 = TestLib.genPred("(0 = 1) ⇒ (¬⊤)");

	Predicate P6 = TestLib.genPred("∀x·x = 0 ⇒ (¬⊤)");

	Predicate P7 = TestLib.genPred("(0 = 1) ⇒ (¬⊥)");

	Predicate P8 = TestLib.genPred("∀x·x = 0 ⇒ (¬⊥)");

	Predicate P9 = TestLib.genPred("(0 = 1) ⇒ (¬¬(1=2))");

	Predicate P10 = TestLib.genPred("∀x·x = 0 ⇒ (¬¬(x=1))");

	Predicate P11 = TestLib.genPred("(0 = 1) ⇒ (¬(1 = 2 ⇒ 2 = 3))");

	Predicate P12 = TestLib.genPred("∀x·x = 0 ⇒ (¬(x = 1 ⇒ x = 2))");

	Predicate P13 = TestLib.genPred("(0 = 1) ⇒ (¬(∀y·y ∈ ℕ ⇒ 0 ≤ y))");

	Predicate P14 = TestLib.genPred("∀x·x = 0 ⇒ (¬(∀y·y ∈ ℕ ⇒ x ≤ y))");

	Predicate P15 = TestLib.genPred("(0 = 1) ⇒ ¬({1} = ∅)");

	Predicate P16 = TestLib.genPred("∀x·x = 0 ⇒ ¬({x} = ∅)");

	Predicate P17 = TestLib.genPred("(0 = 1) ⇒ ¬({1 ↦ 2} = ∅)");

	Predicate P18 = TestLib.genPred("∀x·x = 0 ⇒ ¬({x ↦ 0} = ∅)");

	Predicate P19 = TestLib.genPred("(0 = 1) ⇒ ¬({(1 ↦ 2) ↦ (3 ↦ 4)} = ∅)");

	Predicate P20 = TestLib.genPred("∀x·x = 0 ⇒ ¬({1 ↦ ((x ↦ 0) ↦ x)} = ∅)");

	Predicate P21 = TestLib.genPred("(0 = 1) ⇒ ¬({1 ↦ {2}} = ∅)");

	Predicate P22 = TestLib.genPred("∀x·x = 0 ⇒ ¬({{x} ↦ 0} = ∅)");

	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P13);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P14);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P15);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P16);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P17);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P18);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P19);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P20);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P21);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P22);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for correct reasoner failure
	 */
	public void testHypNotPresent() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P13 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P13, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P14 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P14, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P15 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P15, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P16 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P16, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P17 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P17, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P18 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P18, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P19 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P19, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P20 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P20, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P21 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P21, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P22 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P22, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for applicable positions
	 */
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.rn_getPositions(P1);
		assertPositions("Position found for P1 ", "1", positions);
		positions = Tactics.rn_getPositions(P2);
		assertPositions("Position found for P2 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P3);
		assertPositions("Position found for P3 ", "1", positions);
		positions = Tactics.rn_getPositions(P4);
		assertPositions("Position found for P4 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P5);
		assertPositions("Position found for P5 ", "1", positions);
		positions = Tactics.rn_getPositions(P6);
		assertPositions("Position found for P6 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P7);
		assertPositions("Position found for P7 ", "1", positions);
		positions = Tactics.rn_getPositions(P8);
		assertPositions("Position found for P8 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P9);
		assertPositions("Position found for P9 ", "1", positions);
		positions = Tactics.rn_getPositions(P10);
		assertPositions("Position found for P10 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P11);
		assertPositions("Position found for P11 ", "1", positions);
		positions = Tactics.rn_getPositions(P12);
		assertPositions("Position found for P12 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P13);
		assertPositions("Position found for P13 ", "1", positions);
		positions = Tactics.rn_getPositions(P14);
		assertPositions("Position found for P14 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P15);
		assertPositions("Position found for P15 ", "1", positions);
		positions = Tactics.rn_getPositions(P16);
		assertPositions("Position found for P16 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P17);
		assertPositions("Position found for P17 ", "1", positions);
		positions = Tactics.rn_getPositions(P18);
		assertPositions("Position found for P18 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P19);
		assertPositions("Position found for P19 ", "1", positions);
		positions = Tactics.rn_getPositions(P20);
		assertPositions("Position found for P20 ", "1.1", positions);
		positions = Tactics.rn_getPositions(P21);
		assertPositions("Position found for P21 ", "1", positions);
		positions = Tactics.rn_getPositions(P22);
		assertPositions("Position found for P22 ", "1.1", positions);
	}

	/**
	 * Tests for reasoner success
	 */
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P1 ",
				"{}[][][⊤] |- 0=1⇒¬1=2∨¬2=3∨¬3=4", newSeqs);

		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P1 ",
				"{}[0=1⇒¬(1=2∧2=3∧3=4)][][0=1⇒¬1=2∨¬2=3∨¬3=4] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P2 ",
				"{}[][][⊤] |- ∀x·x=0⇒¬x=1∨¬x=2∨¬x=3", newSeqs);

		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P2 ",
				"{}[∀x·x=0⇒¬(x=1∧x=2∧x=3)][][∀x·x=0⇒¬x=1∨¬x=2∨¬x=3] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P3 ",
				"{}[][][⊤] |- 0=1⇒¬1=2∧¬2=3∧¬3=4", newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P3 ",
				"{}[0=1⇒¬(1=2∨2=3∨3=4)][][0=1⇒¬1=2∧¬2=3∧¬3=4] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P4 ",
				"{}[][][⊤] |- ∀x·x=0⇒¬x=1∧¬x=2∧¬x=3", newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P4 ",
				"{}[∀x·x=0⇒¬(x=1∨x=2∨x=3)][][∀x·x=0⇒¬x=1∧¬x=2∧¬x=3] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P5 ", "{}[][][⊤] |- 0=1⇒⊥",
				newSeqs);

		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P5 ",
				"{}[0=1⇒¬⊤][][0=1⇒⊥] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P6 ",
				"{}[][][⊤] |- ∀x·x=0⇒⊥", newSeqs);

		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P6 ",
				"{}[∀x·x=0⇒¬⊤][][∀x·x=0⇒⊥] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P7 ", "{}[][][⊤] |- 0=1⇒⊤",
				newSeqs);

		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P7 ",
				"{}[0=1⇒¬⊥][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P8 ",
				"{}[][][⊤] |- ∀x·x=0⇒⊤", newSeqs);

		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P8 ",
				"{}[∀x·x=0⇒¬⊥][][∀x·x=0⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P9 ", "{}[][][⊤] |- 0=1⇒1=2",
				newSeqs);

		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P9 ",
				"{}[0=1⇒¬¬1=2][][0=1⇒1=2] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P10 ",
				"{}[][][⊤] |- ∀x·x=0⇒x=1", newSeqs);

		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P10 ",
				"{}[∀x·x=0⇒¬¬x=1][][∀x·x=0⇒x=1] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P11 ",
				"{}[][][⊤] |- 0=1⇒1=2∧¬2=3", newSeqs);

		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P11 ",
				"{}[0=1⇒¬(1=2⇒2=3)][][0=1⇒1=2∧¬2=3] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P12 ",
				"{}[][][⊤] |- ∀x·x=0⇒x=1∧¬x=2", newSeqs);

		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P12 ",
				"{}[∀x·x=0⇒¬(x=1⇒x=2)][][∀x·x=0⇒x=1∧¬x=2] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P13);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P13 ",
				"{}[][][⊤] |- 0=1⇒(∃y·¬(y∈ℕ⇒0≤y))", newSeqs);

		seq = TestLib.genSeq(P13 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P13, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P14 ",
				"{}[0=1⇒¬(∀y·y∈ℕ⇒0≤y)][][0=1⇒(∃y·¬(y∈ℕ⇒0≤y))] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P14);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P14 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃y·¬(y∈ℕ⇒x≤y))", newSeqs);

		seq = TestLib.genSeq(P14 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P14, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P14 ",
				"{}[∀x·x=0⇒¬(∀y·y∈ℕ⇒x≤y)][][∀x·x=0⇒(∃y·¬(y∈ℕ⇒x≤y))] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P15);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P15 ",
				"{}[][][⊤] |- 0=1⇒(∃x·x∈{1})", newSeqs);

		seq = TestLib.genSeq(P15 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P15, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P15 ",
				"{}[0=1⇒¬{1}=∅][][0=1⇒(∃x·x∈{1})] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P16);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P16 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃x0·x0∈{x})", newSeqs);

		seq = TestLib.genSeq(P16 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P16, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P16 ",
				"{}[∀x·x=0⇒¬{x}=∅][][∀x·x=0⇒(∃x0·x0∈{x})] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P17);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P17 ",
				"{}[][][⊤] |- 0=1⇒(∃x,x0·x0 ↦ x∈{1 ↦ 2})", newSeqs);

		seq = TestLib.genSeq(P17 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P17, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P17 ",
				"{}[0=1⇒¬{1 ↦ 2}=∅][][0=1⇒(∃x,x0·x0 ↦ x∈{1 ↦ 2})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P18);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P18 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃x0,x1·x1 ↦ x0∈{x ↦ 0})", newSeqs);

		seq = TestLib.genSeq(P18 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P18, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P18 ",
				"{}[∀x·x=0⇒¬{x ↦ 0}=∅][][∀x·x=0⇒(∃x0,x1·x1 ↦ x0∈{x ↦ 0})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P19);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P19 ",
				"{}[][][⊤] |- 0=1⇒(∃x,x0,x1,x2·x2 ↦ x1 ↦ (x0 ↦ x)∈{1 ↦ 2 ↦ (3 ↦ 4)})",
				newSeqs);

		seq = TestLib.genSeq(P19 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P19, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P19 ",
				"{}[0=1⇒¬{1 ↦ 2 ↦ (3 ↦ 4)}=∅][][0=1⇒(∃x,x0,x1,x2·x2 ↦ x1 ↦ (x0 ↦ x)∈{1 ↦ 2 ↦ (3 ↦ 4)})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P20);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P20 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃x0,x1,x2,x3·x3 ↦ (x2 ↦ x1 ↦ x0)∈{1 ↦ (x ↦ 0 ↦ x)})",
				newSeqs);

		seq = TestLib.genSeq(P20 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P20, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P20 ",
				"{}[∀x·x=0⇒¬{1 ↦ (x ↦ 0 ↦ x)}=∅][][∀x·x=0⇒(∃x0,x1,x2,x3·x3 ↦ (x2 ↦ x1 ↦ x0)∈{1 ↦ (x ↦ 0 ↦ x)})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P21);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P21 ",
				"{}[][][⊤] |- 0=1⇒(∃x,x0·x0 ↦ x∈{1 ↦ {2}})",
				newSeqs);

		seq = TestLib.genSeq(P21 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P21, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P21 ",
				"{}[0=1⇒¬{1 ↦ {2}}=∅][][0=1⇒(∃x,x0·x0 ↦ x∈{1 ↦ {2}})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P22);
		output = rnReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P22 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃x0,x1·x1 ↦ x0∈{{x} ↦ 0})",
				newSeqs);

		seq = TestLib.genSeq(P22 + " |- ⊤ ");
		output = rnReasoner.apply(seq, new RemoveNegation.Input(P22, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P22 ",
				"{}[∀x·x=0⇒¬{{x} ↦ 0}=∅][][∀x·x=0⇒(∃x0,x1·x1 ↦ x0∈{{x} ↦ 0})] |- ⊤",
				newSeqs);
	}

	private void assertSequents(String message, String expected,
			IProverSequent... sequents) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IProverSequent sequent : sequents) {
			if (sep)
				builder.append('\n');
			builder.append(sequent);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

	private void assertPositions(String message, String expected,
			List<IPosition> positions) {
		StringBuilder builder = new StringBuilder();
		boolean sep = false;
		for (IPosition position : positions) {
			if (sep)
				builder.append('\n');
			builder.append(position);
			sep = true;
		}
		String actual = builder.toString();
		if (!expected.equals(actual)) {
			System.out.println(Util.displayString(actual));
			fail(message + ":\n" + actual);
		}
	}

}
