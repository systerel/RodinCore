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
public class CardComparisonTests extends AbstractManualRewriterTests {

	// card(S) <= card(T) == S <: T
	String P1 = "card({2,x}) ≤ card({1,x})";

	String resultP1 = "{2,x}⊆{1,x}";

	String P2 = "(1 = x) ⇒ card({2,x}) ≤ card({1,x})";

	String resultP2 = "1=x⇒{2,x}⊆{1,x}";

	String P3 = "∀x·x = 0 ⇒ card({2,x}) ≤ card({1,x})";

	String resultP3 = "∀x·x=0⇒{2,x}⊆{1,x}";

	
	// card(S) >= card(T) == T <: S
	String P4 = "card({2,x}) ≥ card({1,x})";

	String resultP4 = "{1,x}⊆{2,x}";

	String P5 = "(1 = x) ⇒ card({2,x}) ≥ card({1,x})";

	String resultP5 = "1=x⇒{1,x}⊆{2,x}";

	String P6 = "∀x·x = 0 ⇒ card({2,x}) ≥ card({1,x})";

	String resultP6 = "∀x·x=0⇒{1,x}⊆{2,x}";

	
	// card(S) < card(T) == S <<: T
	String P7 = "card({2,x}) < card({1,x})";

	String resultP7 = "{2,x}⊂{1,x}";

	String P8 = "(1 = x) ⇒ card({2,x}) < card({1,x})";

	String resultP8 = "1=x⇒{2,x}⊂{1,x}";

	String P9 = "∀x·x = 0 ⇒ card({2,x}) < card({1,x})";

	String resultP9 = "∀x·x=0⇒{2,x}⊂{1,x}";

	
	// card(S) > card(T) == T <<: S
	String P10 = "card({2,x}) > card({1,x})";

	String resultP10 = "{1,x}⊂{2,x}";

	String P11 = "(1 = x) ⇒ card({2,x}) > card({1,x})";

	String resultP11 = "1=x⇒{1,x}⊂{2,x}";

	String P12 = "∀x·x = 0 ⇒ card({2,x}) > card({1,x})";

	String resultP12 = "∀x·x=0⇒{1,x}⊂{2,x}";


	// card(S) > card(T) == T <<: S
	String P13 = "card({2,x}) = card({1,x})";

	String resultP13 = "{2,x}={1,x}";

	String P14 = "(1 = x) ⇒ card({2,x}) = card({1,x})";

	String resultP14 = "1=x⇒{2,x}={1,x}";

	String P15 = "∀x·x = 0 ⇒ card({2,x}) = card({1,x})";

	String resultP15 = "∀x·x=0⇒{2,x}={1,x}";

	
	// Failure
	String P16 = "card(BOOL) ≤ card({1,x})";

	String P17 = "(1 = x) ⇒ card({2,x}) ≤ card(BOOL)";

	String P18 = "∀x·x = 0 ⇒ card(BOOL) ≤ card({1,x})";

	String P19 = "card(BOOL) ≥ card({1,x})";

	String P20 = "(1 = x) ⇒ card({2,x}) ≥ card(BOOL)";

	String P21 = "∀x·x = 0 ⇒ card(BOOL) ≥ card({1,x})";

	String P22 = "card(BOOL) < card({1,x})";

	String P23 = "(1 = x) ⇒ card({2,x}) < card(BOOL)";

	String P24 = "∀x·x = 0 ⇒ card(BOOL) < card({1,x})";

	String P25 = "card(BOOL) > card({1,x})";

	String P26 = "(1 = x) ⇒ card({2,x}) > card(BOOL)";

	String P27 = "∀x·x = 0 ⇒ card(BOOL) > card({1,x})";

	String P28 = "card(BOOL) = card({1,x})";

	String P29 = "(1 = x) ⇒ card({2,x}) = card(BOOL)";

	String P30 = "∀x·x = 0 ⇒ card(BOOL) = card({1,x})";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardComparisonRewrites";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.cardComparisonGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "", resultP1),
				new SuccessfulTest(P2, "1", resultP2),
				new SuccessfulTest(P3, "1.1", resultP3),
				new SuccessfulTest(P4, "", resultP4),
				new SuccessfulTest(P5, "1", resultP5),
				new SuccessfulTest(P6, "1.1", resultP6),
				new SuccessfulTest(P7, "", resultP7),
				new SuccessfulTest(P8, "1", resultP8),
				new SuccessfulTest(P9, "1.1", resultP9),
				new SuccessfulTest(P10, "", resultP10),
				new SuccessfulTest(P11, "1", resultP11),
				new SuccessfulTest(P12, "1.1", resultP12),
				new SuccessfulTest(P13, "", resultP13),
				new SuccessfulTest(P14, "1", resultP14),
				new SuccessfulTest(P15, "1.1", resultP15)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "1",
				P2, "0",
				P3, "1.0",
				P4, "1",
				P5, "0",
				P6, "1.0",
				P7, "1",
				P8, "0",
				P9, "1.0",
				P10, "1",
				P11, "0",
				P12, "1.0",
				P13, "1",
				P14, "0",
				P15, "1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] {
				P1, "",
				P2, "1",
				P3, "1.1",
				P4, "",
				P5, "1",
				P6, "1.1",
				P7, "",
				P8, "1",
				P9, "1.1",
				P10, "",
				P11, "1",
				P12, "1.1",
				P13, "",
				P14, "1",
				P15, "1.1",
				P16, "",
				P17, "1",
				P18, "1.1",
				P19, "",
				P20, "1",
				P21, "1.1",
				P22, "",
				P23, "1",
				P24, "1.1",
				P25, "",
				P26, "1",
				P27, "1.1",
				P28, "",
				P29, "1",
				P30, "1.1"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
