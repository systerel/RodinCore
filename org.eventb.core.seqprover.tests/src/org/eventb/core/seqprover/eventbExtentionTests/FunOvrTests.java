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
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.junit.Test;

/**
 * Unit tests for the rn reasoner
 * 
 * @author htson
 */
public class FunOvrTests extends AbstractTests {

	private static final IReasoner ovReasoner = new FunOvr();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	Predicate P1 = TestLib.genPred("(x = 2) ⇒ (f  g  {x ↦ 3})(y) = 3");

	Predicate P2 = TestLib.genPred("∀x· x = 2 ⇒ (f  g  {x ↦ 3})(y) = 3");

	Predicate P3 = TestLib.genPred("(f  g  {2 ↦ 3})(y) = 3 ");

	Predicate P4 = TestLib.genPred("3 = (f  {2 ↦ 3}  h)(y)");

	@Test
	public void testGoalNotApplicable() {
		IProverSequent seq;
		IReasonerOutput output;

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- ⊤ ");
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- " + P1);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("")), null);

		assertTrue(output instanceof IReasonerFailure);
		// Goal is not applicable
		seq = TestLib.genSeq(" ⊤ |- " + P2);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testPositionGoalIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("1.0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in goal is incorrect
		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("1.0")), null);
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
		output = ovReasoner.apply(seq, new FunOvr.Input(P3, ff
				.makePosition("1.0")), null);
		assertTrue(output instanceof IReasonerFailure);
	}

	@Test
	public void testHypPositionIncorrect() {
		IProverSequent seq;
		IReasonerOutput output;

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = ovReasoner.apply(seq, new FunOvr.Input(P3, ff
				.makePosition("1.0")), null);
		assertTrue(output instanceof IReasonerFailure);

		// Position in hyp is incorrect
		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = ovReasoner.apply(seq, new FunOvr.Input(P4, ff
				.makePosition("1.0")), null);
		assertTrue(output instanceof IReasonerFailure);

	}

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		List<IPosition> positions;
		positions = Tactics.funOvrGetPositions(P1);
		assertPositions("No position found for P1 ", "", positions);
		positions = Tactics.funOvrGetPositions(P2);
		assertPositions("No position found for P2 ", "", positions);
		positions = Tactics.funOvrGetPositions(P3);
		assertPositions("No position found for P3 ", "0", positions);
		positions = Tactics.funOvrGetPositions(P4);
		assertPositions("No position found for P4 ", "1", positions);
	}

	/**
	 * Tests for reasoner success
	 */
	@Test
	public void testSuccess() {

		IProverSequent seq;
		IProverSequent[] newSeqs;
		IReasonerOutput output;

		seq = TestLib.genSeq(" ⊤ |- " + P3);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P3 ",
				"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤, y=2] |- 3=3\n"
						+ "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤, ¬y=2] |- (fg)(y)=3",
				newSeqs);

		seq = TestLib.genSeq(P3 + " |- ⊤ ");
		output = ovReasoner.apply(seq, new FunOvr.Input(P3, ff
				.makePosition("0")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P3 ",
				"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][y=2, 3=3] |- ⊤\n"
						+ "{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][¬y=2, (fg)(y)=3] |- ⊤",
				newSeqs);

		seq = TestLib.genSeq(" ⊤ |- " + P4);
		output = ovReasoner.apply(seq, new FunOvr.Input(null, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully goal P4 ",
				"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤, y∈dom(h)] |- 3=h(y)\n"
						+ "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤, ¬y∈dom(h)] |- 3=(f{2 ↦ 3})(y)",
				newSeqs);

		seq = TestLib.genSeq(P4 + " |- ⊤ ");
		output = ovReasoner.apply(seq, new FunOvr.Input(P4, ff
				.makePosition("1")), null);
		assertTrue(output instanceof IProofRule);
		newSeqs = ((IProofRule) output).apply(seq);
		assertSequents(
				"Applied successfully hyp P4 ",
				"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][y∈dom(h), 3=h(y)] |- ⊤\n"
						+ "{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][¬y∈dom(h), 3=(f{2 ↦ 3})(y)] |- ⊤",
				newSeqs);

	}

}
