package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunRelImg;

/**
 * Unit tests for the Finite of relational image of a function reasoner
 * {@link FiniteFunRelImg}
 * 
 * @author htson
 */
public class FiniteFunRelImgTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[S])";

	String P2 = "∀x· x = 2 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[S])";

	String P3 = "finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}[S])";

	String resultP3GoalA = "{S=ℙ(ℤ), x=ℤ}[][][⊤] |- {0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℤ ⇸ ℤ × ℤ";

	String resultP3GoalB = "{S=ℙ(ℤ), x=ℤ}[][][⊤] |- finite(S)";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteFunRelImgGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteFunRelImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3GoalA, resultP3GoalB)
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P1 in goal
				" ⊤ |- " + P1,
				// P2 in goal
				" ⊤ |- " + P2
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "ROOT",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
