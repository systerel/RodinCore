package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.ast.IPosition.ROOT;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjunctionToImplicationRewrites;
import org.junit.Test;

/**
 * Unit tests for the disjunction to implication reasoner
 * 
 * @author htson
 */
public class DisjToImplTests extends AbstractTests {

	private static final IReasoner dtiReasoner = new DisjunctionToImplicationRewrites();

	Predicate P1 = TestLib.genPred("x = 1 ∨ x = 2 ∨ x = 3");

	Predicate P2 = TestLib.genPred("x = 1 ⇒ x = 2 ∨ x = 3 ∨ x = 4");

	Predicate P3 = TestLib.genPred("∀x·x = 0 ⇒ x = 2 ∨ x = 3 ∨ x = 4");

	Predicate P4 = TestLib.genPred("(x=1 ∨ x=2)∧x=3");

	@Test
	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				ROOT), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("1")), null);
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
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P1,
				ROOT), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P1,
				makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P2,
				makePosition("0.1")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P3,
				makePosition("0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P4,
				makePosition("1")), null);
		assertTrue(output instanceof IReasonerFailure);

	}

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.disjToImplGetPositions(P1);
		assertPositions("Position found for P1 ", "", positions);
		positions = Tactics.disjToImplGetPositions(P2);
		assertPositions("Position found for P2 ", "1", positions);
		positions = Tactics.disjToImplGetPositions(P3);
		assertPositions("Position found for P3 ", "1.1", positions);
		positions = Tactics.disjToImplGetPositions(P4);
		assertPositions("Position found for P4 ", "0", positions);
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
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				ROOT), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P1 ",
				"{x=ℤ}[][][⊤] |- ¬x=1⇒x=2∨x=3", newSeqs);

		seq = TestLib.genSeq(P1 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P1,
				makePosition("")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P1 ",
				"{x=ℤ}[x=1∨x=2∨x=3][][¬x=1⇒x=2∨x=3] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P2 ",
				"{x=ℤ}[][][⊤] |- x=1⇒(¬x=2⇒x=3∨x=4)", newSeqs);

		seq = TestLib.genSeq(P2 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P2,
				makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P2 ",
				"{x=ℤ}[x=1⇒x=2∨x=3∨x=4][][x=1⇒(¬x=2⇒x=3∨x=4)] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P3 ",
				"{}[][][⊤] |- ∀x·x=0⇒(¬x=2⇒x=3∨x=4)", newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P3,
				makePosition("1.1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P3 ",
				"{}[∀x·x=0⇒x=2∨x=3∨x=4][][∀x·x=0⇒(¬x=2⇒x=3∨x=4)] |- ⊤", newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(null,
				makePosition("0")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully goal P4 ",
				"{x=ℤ}[][][⊤] |- ¬x=1⇒x=2\n{x=ℤ}[][][⊤] |- x=3", newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = dtiReasoner.apply(seq, new DisjunctionToImplicationRewrites.Input(P4,
				makePosition("0")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents("Applied successfully hyp P4 ",
				"{x=ℤ}[(x=1∨x=2)∧x=3][][¬x=1⇒x=2, x=3] |- ⊤", newSeqs);

	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
