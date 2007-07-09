package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSet;

/**
 * Unit tests for the Finite Set reasoner
 * {@link FiniteSet}
 * 
 * @author htson
 */
public class FiniteSetTests extends AbstractSingleExpressionInputReasonerTests {

	String P1 = "finite({x ∣ x ∈ ℕ})";

	String resultP1GoalA = "{}[][][⊤] |- finite(ℕ)";

	String resultP1GoalB = "{}[][][⊤] |- {x ∣ x∈ℕ}⊆ℕ";
		
	String P2 = "finite({x ↦ (y ↦ z) ∣ x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℕ})";

	String resultP2GoalA = "{}[][][⊤] |- finite(ℕ × (BOOL × ℕ))";

	String resultP2GoalB = "{}[][][⊤] |- {x ↦ (y ↦ z) ∣ x∈ℕ∧y∈BOOL∧z∈ℕ}⊆ℕ × (BOOL × ℕ)";

	String P3= "a = 1 ⇒ finite({x ↦ (y ↦ z) ∣ x ∈ ℕ ∧ y ∈ BOOL ∧ z ∈ ℕ})";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteSetGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteSet";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, null, "ℕ", resultP1GoalA,
						resultP1GoalB),
				// P2 in goal
				new SuccessfulTest(" ⊤ |- " + P2, null, "ℕ × (BOOL × ℕ)",
						resultP2GoalA, resultP2GoalB),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P2 in goal
				" ⊤ |- " + P2,
				null,
				"ℕ × BOOL × ℕ",
				"Incorrect input type",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"ℕ",
				"Goal is not a finiteness"
		};
	}

}
