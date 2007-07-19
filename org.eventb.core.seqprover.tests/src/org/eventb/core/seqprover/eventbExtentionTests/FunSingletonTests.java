package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FunSingletonImg;

/**
 * Unit tests for the Function apply to singleton set image reasoner
 * {@link FunSingletonImg}
 * 
 * @author htson
 */
public class FunSingletonTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ f[{x}] = {3}";

	String resultP1GoalA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- x=2⇒x∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP1GoalB = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- x=2⇒{f(x)}={3}";
	
	String resultP1HypA = "{f=ℙ(ℤ×ℤ), x=ℤ}[][][x=2⇒f[{x}]={3}] |- x=2⇒x∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP1HypB = "{f=ℙ(ℤ×ℤ), x=ℤ}[x=2⇒f[{x}]={3}][][x=2⇒{f(x)}={3}] |- ⊤";
	
	String P2 = "∀x· x = 2 ⇒ {3} = f[{x}]";

	String resultP2GoalA = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- ∀x·x=2⇒x∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP2GoalB = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- ∀x·x=2⇒{3}={f(x)}";
	
	String resultP2HypA = "{f=ℙ(ℤ×ℤ)}[][][∀x·x=2⇒{3}=f[{x}]] |- ∀x·x=2⇒x∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP2HypB = "{f=ℙ(ℤ×ℤ)}[∀x·x=2⇒{3}=f[{x}]][][∀x·x=2⇒{3}={f(x)}] |- ⊤";
	
	String P3 = "f[{3}] = {3}";

	String resultP3GoalA = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- 3∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP3GoalB = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- {f(3)}={3}";
	
	String resultP3HypA = "{f=ℙ(ℤ×ℤ)}[][][f[{3}]={3}] |- 3∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP3HypB = "{f=ℙ(ℤ×ℤ)}[f[{3}]={3}][][{f(3)}={3}] |- ⊤";

	String P4 = "{3} = f[{3}]";

	String resultP4GoalA = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- 3∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP4GoalB = "{f=ℙ(ℤ×ℤ)}[][][⊤] |- {3}={f(3)}";
	
	String resultP4HypA = "{f=ℙ(ℤ×ℤ)}[][][{3}=f[{3}]] |- 3∈dom(f)∧f∈ℤ ⇸ ℤ";

	String resultP4HypB = "{f=ℙ(ℤ×ℤ)}[{3}=f[{3}]][][{3}={f(3)}] |- ⊤";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "1.0",
				P2, "1.1.1",
				P3, "0",
				P4, "1"
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funSingletonImgGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funSingletonImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, null, "1.0", resultP1GoalA,
						resultP1GoalB),
				// P1 in hyp
				new SuccessfulTest(P1 + " |- ⊤ ", P1, "1.0", resultP1HypA,
						resultP1HypB),
				// P2 in goal
				new SuccessfulTest(" ⊤ |- " + P2, null, "1.1.1", resultP2GoalA,
						resultP2GoalB),
				// P2 in hyp
				new SuccessfulTest(P2 + " |- ⊤ ", P2, "1.1.1", resultP2HypA,
								resultP2HypB),
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0", resultP3GoalA,
						resultP3GoalB),
				// P3 in hyp
				new SuccessfulTest(P3 + " |- ⊤ ", P3, "0", resultP3HypA,
						resultP3HypB),
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
						resultP4HypB) };
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
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
