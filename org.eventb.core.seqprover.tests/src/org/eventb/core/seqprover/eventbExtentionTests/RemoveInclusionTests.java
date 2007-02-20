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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class RemoveInclusionTests extends TestCase {

	private static final IReasoner riReasoner = new RemoveInclusion();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	Predicate P1 = TestLib.genPred("(0 = 1) ⇒ {1} ⊆ {1, 2}");

	Predicate P2 = TestLib.genPred("∀x·x = 0 ⇒ {x} ⊆ {x, 2}");

	Predicate P3 = TestLib.genPred("(0 = 1) ⇒ ∅ ⊆ {1, 2}");

	Predicate P4 = TestLib.genPred("∀x·x = 0 ⇒ ∅ ⊆ {x, 2}");

	Predicate P5 = TestLib.genPred("(0 = 1) ⇒ {1, 2} ⊆ {1, 2}");

	Predicate P6 = TestLib.genPred("∀x·x = 0 ⇒ {x, 2} ⊆ {x, 2}");

	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
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
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	/**
	 * Tests for applicable positions
	 */
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.ri_getPositions(P1);
		assertPositions("Position found for P1 ", "1", positions);
		positions = Tactics.ri_getPositions(P2);
		assertPositions("Position found for P2 ", "1.1", positions);
		positions = Tactics.ri_getPositions(P3);
		assertPositions("Position found for P3 ", "1", positions);
		positions = Tactics.ri_getPositions(P4);
		assertPositions("Position found for P4 ", "1.1", positions);
		positions = Tactics.ri_getPositions(P5);
		assertPositions("Position found for P5 ", "1", positions);
		positions = Tactics.ri_getPositions(P6);
		assertPositions("Position found for P6 ", "1.1", positions);
	}

	/**
	 * Tests for reasoner success
	 */
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P1 ",
				"{}[][][⊤] |- 0=1⇒(∀x·x∈{1}⇒x∈{1,2})", newSeqs);

		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P1, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P1 ",
				"{}[0=1⇒{1}⊆{1,2}][][0=1⇒(∀x·x∈{1}⇒x∈{1,2})] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P2 ",
				"{}[][][⊤] |- ∀x·x=0⇒(∀x0·x0∈{x}⇒x0∈{x,2})", newSeqs);

		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P2, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P2 ",
				"{}[∀x·x=0⇒{x}⊆{x,2}][][∀x·x=0⇒(∀x0·x0∈{x}⇒x0∈{x,2})] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P3 ",
				"{}[][][⊤] |- 0=1⇒⊤", newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P3, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P3 ",
				"{}[0=1⇒∅⊆{1,2}][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P4 ",
				"{}[][][⊤] |- ∀x·x=0⇒⊤", newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P4, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P4 ",
				"{}[∀x·x=0⇒∅⊆{x,2}][][∀x·x=0⇒⊤] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P5);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P5 ",
				"{}[][][⊤] |- 0=1⇒⊤", newSeqs);

		seq = TestLib.genSeq(P5 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P5, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P5 ",
				"{}[0=1⇒{1,2}⊆{1,2}][][0=1⇒⊤] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P6);
		output = riReasoner.apply(seq, new RemoveNegation.Input(null, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P6 ",
				"{}[][][⊤] |- ∀x·x=0⇒⊤", newSeqs);

		seq = TestLib.genSeq(P6 + " |- ⊤ ");
		output = riReasoner.apply(seq, new RemoveNegation.Input(P6, ff
				.makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P6 ",
				"{}[∀x·x=0⇒{x,2}⊆{x,2}][][∀x·x=0⇒⊤] |- ⊤",
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
