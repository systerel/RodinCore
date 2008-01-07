package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ConvRewrites;

/**
 * Unit tests for the Converse Relation Rewrites reasoner
 * {@link ConvRewrites}
 * 
 * @author htson
 */
public class CardComparisonTests extends AbstractManualInferenceTests {

	String P1 = "(x = 2) ⇒ card(1‥x) = card(x‥3)";

	String P2 = "∀x· x = 2 ⇒ card(1‥x) = card(x‥3)";

	String P3 = "card(1‥x) = card(x‥3)";

	String resultP3Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x=x ‥ 3";

	String P4 = "(x = 2) ⇒ card(1‥x) ≤ card(x‥3)";

	String P5 = "∀x· x = 2 ⇒ card(1‥x) ≤ card(x‥3)";

	String P6 = "card(1‥x) ≤ card(x‥3)";

	String resultP6Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x⊆x ‥ 3";
	
	String P7 = "(x = 2) ⇒ card(1‥x) < card(x‥3)";

	String P8 = "∀x· x = 2 ⇒ card(1‥x) < card(x‥3)";

	String P9 = "card(1‥x) < card(x‥3)";

	String resultP9Goal = "{x=ℤ}[][][⊤] |- 1 ‥ x⊂x ‥ 3";
	
	String P10 = "(x = 2) ⇒ card(1‥x) ≥ card(x‥3)";

	String P11 = "∀x· x = 2 ⇒ card(1‥x) ≥ card(x‥3)";

	String P12 = "card(1‥x) ≥ card(x‥3)";

	String resultP12Goal = "{x=ℤ}[][][⊤] |- x ‥ 3⊆1 ‥ x";
	
	String P13 = "(x = 2) ⇒ card(1‥x) > card(x‥3)";

	String P14 = "∀x· x = 2 ⇒ card(1‥x) > card(x‥3)";

	String P15 = "card(1‥x) > card(x‥3)";

	String resultP15Goal = "{x=ℤ}[][][⊤] |- x ‥ 3⊂1 ‥ x";
	
	protected String [] getTestGetPositions() {
		return new String [] {
				P1, "",
				P2, "",
				P3, "",
				P4, "",
				P5, "",
				P6, "",
				P7, "",
				P8, "",
				P9, "",
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.cardComparisonGetPositions(predicate);
	}
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardComparison";
	}

	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				// P3 in goal
				new SuccessfulTest(" ⊤ |- " + P3, null, "", resultP3Goal),
				// P6 in goal
				new SuccessfulTest(" ⊤ |- " + P6, null, "", resultP6Goal),
				// P9 in goal
				new SuccessfulTest(" ⊤ |- " + P9, null, "", resultP9Goal),
			};
	}

	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// P3 in hyp
				P3 + " |- ⊤ ",
				P3,
				"",
				// P3 in goal
				" ⊤ |- " + P3,
				null,
				"1",
				// P6 in hyp
				P6 + " |- ⊤ ",
				P6,
				"",
				// P6 in goal
				" ⊤ |- " + P6,
				null,
				"1",
				// P9 in hyp
				P9 + " |- ⊤ ",
				P9,
				"",
				// P9 in goal
				" ⊤ |- " + P9,
				null,
				"1",
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4FreeCore.externalPP(false);
//	}

}
