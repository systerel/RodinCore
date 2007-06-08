package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FunInterImg;

/**
 * Unit tests for the Function Converse apply to intersection image reasoner
 * {@link FunInterImg}
 * 
 * @author htson
 */
public class FunOvrTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ (f  g  {x ↦ 3})(y) = 3";

	String P2 = "∀x· x = 2 ⇒ (f  g  {x ↦ 3})(y) = 3";

	String P3 = "(f  g  {2 ↦ 3})(y) = 3 ";

	String P4 = "3 = (f  {2 ↦ 3}  h)(y)";

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funOvrGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funOvr";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0",
						"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤, y=2] |- 3=3",
						"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[][][⊤, ¬y=2] |- (fg)(y)=3"),
				// P3 in hyp
				new SuccessfulTest(
						P3 + " |- ⊤ ",
						P3,
						"0",
						"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][y=2, 3=3] |- ⊤",
						"{f=ℙ(ℤ×ℤ), y=ℤ, g=ℙ(ℤ×ℤ)}[(fg{2 ↦ 3})(y)=3][][¬y=2, (fg)(y)=3] |- ⊤"),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, null, "1",
						"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤, y∈dom(h)] |- 3=h(y)",
						"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[][][⊤, ¬y∈dom(h)] |- 3=(f{2 ↦ 3})(y)"),
				// P4 in hyp
				new SuccessfulTest(
						P4 + " |- ⊤ ",
						P4,
						"1",
						"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][y∈dom(h), 3=h(y)] |- ⊤",
						"{h=ℙ(ℤ×ℤ), f=ℙ(ℤ×ℤ), y=ℤ}[3=(f{2 ↦ 3}h)(y)][][¬y∈dom(h), 3=(f{2 ↦ 3})(y)] |- ⊤")
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
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "",
				P3, "0",
				P4, "1"
		};
	}

}
