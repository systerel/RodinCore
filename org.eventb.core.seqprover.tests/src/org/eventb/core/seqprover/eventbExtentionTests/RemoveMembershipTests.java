package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertTrue;

import java.util.List;

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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;
import org.junit.Test;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class RemoveMembershipTests extends AbstractTests {

	private static final IReasoner rmReasoner = new RemoveMembership();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	Predicate P1 = TestLib.genPred("(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)");

	Predicate P2 = TestLib.genPred("∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ");

	Predicate P3 = TestLib.genPred("(0 = 1) ⇒ {1} ∈ ℙ(ℕ)");

	Predicate P4 = TestLib.genPred("∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)");

	Predicate P5 = TestLib.genPred("(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}");

	Predicate P6 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}");

	Predicate P7 = TestLib.genPred("(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}");

	Predicate P8 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}");

	Predicate P9 = TestLib.genPred("(0 = 1) ⇒ 1 ∈ {1} ∖ {2}");

	Predicate P10 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {x} ∖ {1}");

	Predicate P11 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ {1, 2, 3}");

	Predicate P12 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {1, 2, 3}");

	Predicate P13 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ {0, 1, 2}");

	Predicate P14 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {1, x, 3}");

	Predicate P15 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ {1}");

	Predicate P16 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ {1}");

	Predicate P17 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ union({{1},{2}})");

	Predicate P18 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ union({{1},{2}})");

	Predicate P19 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ inter({{1},{2}})");

	Predicate P20 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ inter({{1},{2}})");

	Predicate P21 = TestLib
			.genPred("(0 = 1) ⇒ (0 ∈ (\u22c3 x · x ∈ ℕ \u2223 {x+1}))");

	Predicate P22 = TestLib
			.genPred("∀x·x = 0 ⇒ x ∈ (\u22c3 y·y∈ℕ \u2223 {x + y})");

	Predicate P23 = TestLib
			.genPred("(0 = 1) ⇒ (0 ∈ (\u22c2 x · x ∈ ℕ \u2223 {x+1}))");

	Predicate P24 = TestLib
			.genPred("∀x·x = 0 ⇒ x ∈ (\u22c2 y·y∈ℕ \u2223 {x + y})");

	Predicate P25 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})");

	Predicate P26 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})");

	Predicate P27 = TestLib.genPred("(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})");

	Predicate P28 = TestLib.genPred("∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})");

	Predicate P29 = TestLib.genPred("(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)");

	Predicate P30 = TestLib.genPred("∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)");

	Predicate P31 = TestLib.genPred("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})");

	Predicate P32 = TestLib
			.genPred("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})");

	Predicate P33 = TestLib.genPred("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})");

	Predicate P34 = TestLib
			.genPred("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})");

	Predicate P35 = TestLib.genPred("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})");

	Predicate P36 = TestLib
			.genPred("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})");

	Predicate P37 = TestLib.genPred("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})");

	Predicate P38 = TestLib
			.genPred("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})");

	@Test
	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P13);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P14);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P15);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P16);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P17);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P18);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P19);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P20);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P21);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P22);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P23);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P24);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P25);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P26);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P27);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P28);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P29);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P30);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P31);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P32);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P33);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P34);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P35);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P36);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P37);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P38);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for correct reasoner failure
	 */
	@Test
	public void testHypNotPresent() {
		IProverSequent seq;
		IReasonerOutput output;

		// Hyp is not present
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P13 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P13, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P14 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P14, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P15 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P15, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P16 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P16, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P17 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P17, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P18 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P18, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P19 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P19, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P20 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P20, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P21 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P21, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P22 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P22, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P23 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P23, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P24 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P24, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P25 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P25, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P26 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P26, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P27 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P27, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P28 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P28, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P29 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P29, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P30 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P30, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P31 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P31, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P32 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P32, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P33 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P33, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P34 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P34, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P35 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P35, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P36 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P36, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P37 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P37, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P38 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P38, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.rmGetPositions(P1);
		assertPositions("Position found for P1 ", "1", positions);
		positions = Tactics.rmGetPositions(P2);
		assertPositions("Position found for P2 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P3);
		assertPositions("Position found for P3 ", "1", positions);
		positions = Tactics.rmGetPositions(P4);
		assertPositions("Position found for P4 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P5);
		assertPositions("Position found for P5 ", "1", positions);
		positions = Tactics.rmGetPositions(P6);
		assertPositions("Position found for P6 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P7);
		assertPositions("Position found for P7 ", "1", positions);
		positions = Tactics.rmGetPositions(P8);
		assertPositions("Position found for P8 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P9);
		assertPositions("Position found for P9 ", "1", positions);
		positions = Tactics.rmGetPositions(P10);
		assertPositions("Position found for P10 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P11);
		assertPositions("Position found for P11 ", "1", positions);
		positions = Tactics.rmGetPositions(P12);
		assertPositions("Position found for P12 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P13);
		assertPositions("Position found for P13 ", "1", positions);
		positions = Tactics.rmGetPositions(P14);
		assertPositions("Position found for P14 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P15);
		assertPositions("Position found for P15 ", "1", positions);
		positions = Tactics.rmGetPositions(P16);
		assertPositions("Position found for P16 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P17);
		assertPositions("Position found for P17 ", "1", positions);
		positions = Tactics.rmGetPositions(P18);
		assertPositions("Position found for P18 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P19);
		assertPositions("Position found for P19 ", "1", positions);
		positions = Tactics.rmGetPositions(P20);
		assertPositions("Position found for P20 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P21);
		assertPositions("Position found for P21 ", "1", positions);
		positions = Tactics.rmGetPositions(P22);
		assertPositions("Position found for P22 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P23);
		assertPositions("Position found for P23 ", "1", positions);
		positions = Tactics.rmGetPositions(P24);
		assertPositions("Position found for P24 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P25);
		assertPositions("Position found for P25 ", "1", positions);
		positions = Tactics.rmGetPositions(P26);
		assertPositions("Position found for P26 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P27);
		assertPositions("Position found for P27 ", "1", positions);
		positions = Tactics.rmGetPositions(P28);
		assertPositions("Position found for P28 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P29);
		assertPositions("Position found for P29 ", "1", positions);
		positions = Tactics.rmGetPositions(P30);
		assertPositions("Position found for P30 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P31);
		assertPositions("Position found for P31 ", "1", positions);
		positions = Tactics.rmGetPositions(P32);
		assertPositions("Position found for P32 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P33);
		assertPositions("Position found for P33 ", "1", positions);
		positions = Tactics.rmGetPositions(P34);
		assertPositions("Position found for P34 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P35);
		assertPositions("Position found for P35 ", "1", positions);
		positions = Tactics.rmGetPositions(P36);
		assertPositions("Position found for P36 ", "1.1", positions);
		positions = Tactics.rmGetPositions(P37);
		assertPositions("Position found for P37 ", "1", positions);
		positions = Tactics.rmGetPositions(P38);
		assertPositions("Position found for P38 ", "1.1", positions);
	}

	/**
	 * Tests for reasoner success
	 */
	@Test
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P1 ",
				"{}[][][⊤] |- 0=1⇒1∈ℕ∧2∈ℕ", newSeqs);

		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P1 ",
				"{}[0=1⇒1 ↦ 2∈ℕ × ℕ][][0=1⇒1∈ℕ∧2∈ℕ] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P2 ",
				"{}[][][⊤] |- ∀x·x=0⇒x∈ℕ∧x∈ℕ", newSeqs);

		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P2 ",
				"{}[∀x·x=0⇒x ↦ x∈ℕ × ℕ][][∀x·x=0⇒x∈ℕ∧x∈ℕ] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P3 ",
				"{}[][][⊤] |- 0=1⇒{1}⊆ℕ", newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P3 ",
				"{}[0=1⇒{1}∈ℙ(ℕ)][][0=1⇒{1}⊆ℕ] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P4 ",
				"{}[][][⊤] |- ∀x·x=0⇒{x}⊆ℕ", newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P4 ",
				"{}[∀x·x=0⇒{x}∈ℙ(ℕ)][][∀x·x=0⇒{x}⊆ℕ] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P5 ",
				"{}[][][⊤] |- 0=1⇒1∈{1}∨1∈{2}∨1∈{3}", newSeqs);

		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P5 ",
				"{}[0=1⇒1∈{1}∪{2}∪{3}][][0=1⇒1∈{1}∨1∈{2}∨1∈{3}] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P6 ",
				"{}[][][⊤] |- ∀x·x=0⇒x∈{1}∨x∈{2}∨x∈{3}", newSeqs);

		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P6 ",
				"{}[∀x·x=0⇒x∈{1}∪{2}∪{3}][][∀x·x=0⇒x∈{1}∨x∈{2}∨x∈{3}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P7);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P7 ",
				"{}[][][⊤] |- 0=1⇒1∈{1}∧1∈{2}∧1∈{3}", newSeqs);

		seq = TestLib.genSeq(P7 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P7, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P7 ",
				"{}[0=1⇒1∈{1}∩{2}∩{3}][][0=1⇒1∈{1}∧1∈{2}∧1∈{3}] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P8);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P8 ",
				"{}[][][⊤] |- ∀x·x=0⇒x∈{1}∧x∈{2}∧x∈{3}", newSeqs);

		seq = TestLib.genSeq(P8 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P8, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P8 ",
				"{}[∀x·x=0⇒x∈{1}∩{2}∩{3}][][∀x·x=0⇒x∈{1}∧x∈{2}∧x∈{3}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P9);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P9 ",
				"{}[][][⊤] |- 0=1⇒1∈{1}∧¬1∈{2}", newSeqs);

		seq = TestLib.genSeq(P9 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P9, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P9 ",
				"{}[0=1⇒1∈{1} ∖ {2}][][0=1⇒1∈{1}∧¬1∈{2}] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P10);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P10 ",
				"{}[][][⊤] |- ∀x·x=0⇒x∈{x}∧¬x∈{1}", newSeqs);

		seq = TestLib.genSeq(P10 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P10, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P10 ",
				"{}[∀x·x=0⇒x∈{x} ∖ {1}][][∀x·x=0⇒x∈{x}∧¬x∈{1}] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P11);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P11 ",
				"{}[][][⊤] |- 0=1⇒0=1∨0=2∨0=3", newSeqs);

		seq = TestLib.genSeq(P11 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P11, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P11 ",
				"{}[0=1⇒0∈{1,2,3}][][0=1⇒0=1∨0=2∨0=3] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P12);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P12 ",
				"{}[][][⊤] |- ∀x·x=0⇒x=1∨x=2∨x=3", newSeqs);

		seq = TestLib.genSeq(P12 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P12, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P12 ",
				"{}[∀x·x=0⇒x∈{1,2,3}][][∀x·x=0⇒x=1∨x=2∨x=3] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P13);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P13 ", "{}[][][⊤] |- 0=1⇒⊤",
				newSeqs);

		seq = TestLib.genSeq(P13 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P13, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P13 ",
				"{}[0=1⇒0∈{0,1,2}][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P14);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P14 ",
				"{}[][][⊤] |- ∀x·x=0⇒⊤", newSeqs);

		seq = TestLib.genSeq(P14 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P14, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P14 ",
				"{}[∀x·x=0⇒x∈{1,x,3}][][∀x·x=0⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P15);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P15 ",
				"{}[][][⊤] |- 0=1⇒0=1", newSeqs);

		seq = TestLib.genSeq(P15 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P15, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P15 ",
				"{}[0=1⇒0∈{1}][][0=1⇒0=1] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P16);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P16 ",
				"{}[][][⊤] |- ∀x·x=0⇒x=1", newSeqs);

		seq = TestLib.genSeq(P16 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P16, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P16 ",
				"{}[∀x·x=0⇒x∈{1}][][∀x·x=0⇒x=1] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P17);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P17 ",
				"{}[][][⊤] |- 0=1⇒(∃s·s∈{{1},{2}}∧0∈s)", newSeqs);

		seq = TestLib.genSeq(P17 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P17, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P17 ",
				"{}[0=1⇒0∈union({{1},{2}})][][0=1⇒(∃s·s∈{{1},{2}}∧0∈s)] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P18);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P18 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃s·s∈{{1},{2}}∧x∈s)", newSeqs);

		seq = TestLib.genSeq(P18 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P18, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P18 ",
				"{}[∀x·x=0⇒x∈union({{1},{2}})][][∀x·x=0⇒(∃s·s∈{{1},{2}}∧x∈s)] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P19);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P19 ",
				"{}[][][⊤] |- 0=1⇒(∀s·s∈{{1},{2}}⇒0∈s)", newSeqs);

		seq = TestLib.genSeq(P19 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P19, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P19 ",
				"{}[0=1⇒0∈inter({{1},{2}})][][0=1⇒(∀s·s∈{{1},{2}}⇒0∈s)] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P20);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P20 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∀s·s∈{{1},{2}}⇒x∈s)", newSeqs);

		seq = TestLib.genSeq(P20 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P20, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P20 ",
				"{}[∀x·x=0⇒x∈inter({{1},{2}})][][∀x·x=0⇒(∀s·s∈{{1},{2}}⇒x∈s)] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P21);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P21 ",
				"{}[][][⊤] |- 0=1⇒(∃x·x∈ℕ∧0∈{x+1})", newSeqs);

		seq = TestLib.genSeq(P21 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P21, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P21 ",
				"{}[0=1⇒0∈(⋃x·x∈ℕ ∣ {x+1})][][0=1⇒(∃x·x∈ℕ∧0∈{x+1})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P22);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P22 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃y·y∈ℕ∧x∈{x+y})", newSeqs);

		seq = TestLib.genSeq(P22 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P22, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P22 ",
				"{}[∀x·x=0⇒x∈(⋃y·y∈ℕ ∣ {x+y})][][∀x·x=0⇒(∃y·y∈ℕ∧x∈{x+y})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P23);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P23 ",
				"{}[][][⊤] |- 0=1⇒(∀x·x∈ℕ⇒0∈{x+1})", newSeqs);

		seq = TestLib.genSeq(P23 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P23, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P23 ",
				"{}[0=1⇒0∈(⋂x·x∈ℕ ∣ {x+1})][][0=1⇒(∀x·x∈ℕ⇒0∈{x+1})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P24);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P24 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∀y·y∈ℕ⇒x∈{x+y})", newSeqs);

		seq = TestLib.genSeq(P24 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P24, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P24 ",
				"{}[∀x·x=0⇒x∈(⋂y·y∈ℕ ∣ {x+y})][][∀x·x=0⇒(∀y·y∈ℕ⇒x∈{x+y})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P25);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P25 ",
				"{}[][][⊤] |- 0=1⇒(∃y·0 ↦ y∈{0 ↦ 1})", newSeqs);

		seq = TestLib.genSeq(P25 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P25, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P25 ",
				"{}[0=1⇒0∈dom({0 ↦ 1})][][0=1⇒(∃y·0 ↦ y∈{0 ↦ 1})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P26);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P26 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃y·x ↦ y∈{x ↦ 1,x ↦ 2})", newSeqs);

		seq = TestLib.genSeq(P26 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P26, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P26 ",
				"{}[∀x·x=0⇒x∈dom({x ↦ 1,x ↦ 2})][][∀x·x=0⇒(∃y·x ↦ y∈{x ↦ 1,x ↦ 2})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P27);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P27 ",
				"{}[][][⊤] |- 0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})", newSeqs);

		seq = TestLib.genSeq(P27 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P27, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P27 ",
				"{}[0=1⇒0∈ran({0 ↦ 1})][][0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P28);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P28 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})", newSeqs);

		seq = TestLib.genSeq(P28 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P28, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P28 ",
				"{}[∀x·x=0⇒x∈ran({x ↦ 1,2 ↦ x})][][∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P29);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P29 ",
				"{}[][][⊤] |- 0=1⇒1 ↦ 0∈{1 ↦ 0}", newSeqs);

		seq = TestLib.genSeq(P29 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P29, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P29 ",
				"{}[0=1⇒0 ↦ 1∈{1 ↦ 0}∼][][0=1⇒1 ↦ 0∈{1 ↦ 0}] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P30);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P30 ",
				"{}[][][⊤] |- ∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}", newSeqs);

		seq = TestLib.genSeq(P30 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P30, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P30 ",
				"{}[∀x·x=0⇒x ↦ 1∈{1 ↦ x,x ↦ 2}∼][][∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P31);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P31 ",
				"{}[][][⊤] |- 0=1⇒1∈{1}∧1 ↦ 0∈{1 ↦ 0}", newSeqs);

		seq = TestLib.genSeq(P31 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P31, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P31 ",
				"{}[0=1⇒1 ↦ 0∈{1} ◁ {1 ↦ 0}][][0=1⇒1∈{1}∧1 ↦ 0∈{1 ↦ 0}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P32);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P32 ",
				"{}[][][⊤] |- ∀x·x=0⇒1∈{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}", newSeqs);

		seq = TestLib.genSeq(P32 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P32, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P32 ",
				"{}[∀x·x=0⇒1 ↦ x∈{1} ◁ {1 ↦ x,x ↦ 2}][][∀x·x=0⇒1∈{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P33);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P33 ",
				"{}[][][⊤] |- 0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}", newSeqs);

		seq = TestLib.genSeq(P33 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P33, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P33 ",
				"{}[0=1⇒1 ↦ 0∈{1} ⩤ {1 ↦ 0}][][0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P34);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P34 ",
				"{}[][][⊤] |- ∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}", newSeqs);

		seq = TestLib.genSeq(P34 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P34, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P34 ",
				"{}[∀x·x=0⇒1 ↦ x∈{1} ⩤ {1 ↦ x,x ↦ 2}][][∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P35);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P35 ",
				"{}[][][⊤] |- 0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}", newSeqs);

		seq = TestLib.genSeq(P35 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P35, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P35 ",
				"{}[0=1⇒1 ↦ 0∈{1 ↦ 0} ▷ {0}][][0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P36);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P36 ",
				"{}[][][⊤] |- ∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}", newSeqs);

		seq = TestLib.genSeq(P36 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P36, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P36 ",
				"{}[∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2} ▷ {x}][][∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P37);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P37 ",
				"{}[][][⊤] |- 0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}", newSeqs);

		seq = TestLib.genSeq(P37 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P37, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P37 ",
				"{}[0=1⇒1 ↦ 0∈{1 ↦ 0} ⩥ {0}][][0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P38);
		output = rmReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P38 ",
				"{}[][][⊤] |- ∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}", newSeqs);

		seq = TestLib.genSeq(P38 + " |- ⊤ ");
		output = rmReasoner.apply(seq, new RemoveNegation.Input(P38, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P38 ",
				"{}[∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2} ⩥ {x}][][∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}] |- ⊤",
				newSeqs);
	}

}
