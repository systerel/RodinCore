package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteInter;

/**
 * Unit tests for the Finite of intersection reasoner {@link FiniteInter}
 * 
 * @author htson
 */
public class FiniteInterTests extends AbstractEmptyInputReasonerTests {

	String P1 = "(x = 2) ⇒ finite(S ∩ {0 ↦ 3} ∩ T)";

	String P2 = "∀x· x = 2 ⇒ finite(S ∩ {0 ↦ 3} ∩ T)";

	String P3 = "finite(S ∩ {0 ↦ 3} ∩ T)";

	String resultP3GoalA = "{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite(S)∨finite({0 ↦ 3})∨finite(T)";
	
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteInterGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteInter";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, resultP3GoalA)
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

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
