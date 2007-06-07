package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.FunConvInterImg;

/**
 * Unit tests for the Function Converse apply to intersection image reasoner
 * {@link FunConvInterImg}
 * 
 * @author htson
 */
public class FunConvInterImgTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ f∼[{x,3} ∩ {2}] = {3}";

	String P2 = "∀x· x = 2 ⇒ {3} = f∼[{x,3} ∩ {2}]";

	String P3 = "f∼[{x,3} ∩ {2}] = {3}";

	String P4 = "{3} = f∼[{x,3} ∩ {2}]";

	public String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, "0",
				P4, "1"
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.funConvInterImgGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funConvInterImg";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "0",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∈ℤ ⇸ ℤ",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∼[{x,3}]∩f∼[{2}]={3}"),
				// P3 in hyp
				new SuccessfulTest(P3 + " |- ⊤ ", P3, "0",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][f∼[{x,3}∩{2}]={3}] |- f∈ℤ ⇸ ℤ",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[f∼[{x,3}∩{2}]={3}][][f∼[{x,3}]∩f∼[{2}]={3}] |- ⊤"),
				// P4 in goal
				new SuccessfulTest(" ⊤ |- " + P4, null, "1",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- f∈ℤ ⇸ ℤ",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][⊤] |- {3}=f∼[{x,3}]∩f∼[{2}]"),
				// P4 in hyp
				new SuccessfulTest(P4 + " |- ⊤ ", P4, "1",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[][][{3}=f∼[{x,3}∩{2}]] |- f∈ℤ ⇸ ℤ",
						"{f=ℙ(ℤ×ℤ), x=ℤ}[{3}=f∼[{x,3}∩{2}]][][{3}=f∼[{x,3}]∩f∼[{2}]] |- ⊤")
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

}
