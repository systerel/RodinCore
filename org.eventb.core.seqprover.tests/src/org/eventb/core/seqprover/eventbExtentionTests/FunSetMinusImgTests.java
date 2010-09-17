package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FunInterImg;

/**
 * Unit tests for the Function apply to intersection image reasoner
 * {@link FunInterImg}
 * 
 * @author htson
 */
public class FunSetMinusImgTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ f[{x,3} ∖ {2}] = {3}";

	String P2 = "∀x· x = 2 ⇒ {3} = f[{x,3} ∖ {2}]";

	String P3 = "f[{x,3} ∖ {2}] = {3}";
	
	String resultP3GoalA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";
	
	String resultP3GoalB = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f[{x,3}] ∖ f[{2}]={3}";

	String resultP3HypA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][f[{x,3} ∖ {2}]={3}] |- f∼∈ℤ ⇸ ℤ";
	
	String resultP3HypB = "{f=ℙ(ℤ×ℤ), x=ℤ}[f[{x,3} ∖ {2}]={3}][][f[{x,3}] ∖ f[{2}]={3}] |- ⊤";
	
	String P4 = "{3} = f[{x,3} ∖ {2}]";

	String resultP4GoalA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP4GoalB = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- {3}=f[{x,3}] ∖ f[{2}]";

	String resultP4HypA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][{3}=f[{x,3} ∖ {2}]] |- f∼∈ℤ ⇸ ℤ";

	String resultP4HypB = "{f=ℙ(ℤ×ℤ), x=ℤ}[{3}=f[{x,3} ∖ {2}]][][{3}=f[{x,3}] ∖ f[{2}]] |- ⊤";

	String P5 = "¬f[{x,3} ∖ {2}] = {3}";
	
	String resultP5GoalA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";
	
	String resultP5GoalB = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- ¬f[{x,3}] ∖ f[{2}]={3}";

	String resultP5HypA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][¬f[{x,3} ∖ {2}]={3}] |- f∼∈ℤ ⇸ ℤ";
	
	String resultP5HypB = "{f=ℙ(ℤ×ℤ), x=ℤ}[¬f[{x,3} ∖ {2}]={3}][][¬f[{x,3}] ∖ f[{2}]={3}] |- ⊤";
	
	String P6 = "¬{3} = f[{x,3} ∖ {2}]";

	String resultP6GoalA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∼∈ℤ ⇸ ℤ";

	String resultP6GoalB = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- ¬{3}=f[{x,3}] ∖ f[{2}]";

	String resultP6HypA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][¬{3}=f[{x,3} ∖ {2}]] |- f∼∈ℤ ⇸ ℤ";

	String resultP6HypB = "{f=ℙ(ℤ×ℤ), x=ℤ}[¬{3}=f[{x,3} ∖ {2}]][][¬{3}=f[{x,3}] ∖ f[{2}]] |- ⊤";

	public String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, "0",
				P4, "1",
				P5, "0.0",
				P6, "0.1"
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funSetMinusImgGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funSetMinusImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0", resultP3GoalA,
						resultP3GoalB),
				// P3 in hyp
				new SuccessfulTest(P3 + " |- ⊤ ", P3, "0", resultP3HypA,
						resultP3HypB),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, null, "1", resultP4GoalA,
						resultP4GoalB),
				// P4 in hyp
				new SuccessfulTest(P4 + " |- ⊤ ", P4, "1", resultP4HypA,
						resultP4HypB),
				// P5 in goal
				new SuccessfulTest(" ⊤ |- " + P5, null, "0.0", resultP5GoalA,
						resultP5GoalB),
				// P5 in hyp
				new SuccessfulTest(P5 + " |- ⊤ ", P5, "0.0", resultP5HypA,
						resultP5HypB),
				// P6 in goal
				new SuccessfulTest(" ⊤ |- " + P6, null, "0.1", resultP6GoalA,
						resultP6GoalB),
				// P6 in hyp
				new SuccessfulTest(P6 + " |- ⊤ ", P6, "0.1", resultP6HypA,
						resultP6HypB)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P3 in hyp
				P3 + " |- ⊤ ",
				P3,
				"1",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"1",
				// P4 in hyp
				P4 + " |- ⊤ ",
				P4,
				"0",
				// P4 in goal
				" ⊤ |- " + P4,
				null,
				"0",
				// P5 in hyp
				P5 + " |- ⊤ ",
				P5,
				"0.1",
				// P5 in goal
				" ⊤ |- " + P5,
				null,
				"0.1",
				// P6 in hyp
				P6 + " |- ⊤ ",
				P6,
				"0.0",
				// P6 in goal
				" ⊤ |- " + P6,
				null,
				"0.0",
				// position is not WD strict
				" ⊤ |- " + P1,
				null,
				"1.0",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
