package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteRelation;

/**
 * Unit tests for the Finite of relation reasoner
 * {@link FiniteRelation}
 * 
 * @author htson
 */
public class FiniteRelationTests extends AbstractSingleExpressionInputReasonerTests {

	String P1 = "finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	String resultP1GoalA = "{x=ℤ}[][][⊤] |- {0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℕ ↔ ℕ × ℕ";

	String resultP1GoalB = "{x=ℤ}[][][⊤] |- finite(ℕ)";
		
	String resultP1GoalC = "{x=ℤ}[][][⊤] |- finite(ℕ × ℕ)";

	String P2 = "x = 1 ⇒ finite({0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)})";

	String P3 = "finite({0 ↦ 3,1 ↦ x,1 ↦ 2}[{x}])";

	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteRelationGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteRelation";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P1 in goal
				new SuccessfulTest(" ⊤ |- " + P1, null, "ℕ↔ℕ × ℕ", resultP1GoalA,
						resultP1GoalB, resultP1GoalC),
		};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P2 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ × BOOL × ℕ",
				"Expected a set of all relations S ↔ T",
				// P1 in goal
				" ⊤ |- " + P1,
				null,
				"ℕ ↔ BOOL × ℕ",
				"Type check failed for " + "{0 ↦ (3 ↦ 2),1 ↦ (3 ↦ x),1 ↦ (2 ↦ 3)}∈ℕ ↔ BOOL × ℕ",
				// P2 in goal
				" ⊤ |- " + P2,
				null,
				"ℕ",
				"Goal is not a finiteness",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"ℕ",
				"Goal is not a finiteness of a relation"
		};
	}

}
