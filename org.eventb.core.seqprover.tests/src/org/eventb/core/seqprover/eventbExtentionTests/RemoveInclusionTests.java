package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;

/**
 * Unit tests for the ri reasoner {@link RemoveInclusion}
 * 
 * @author htson
 */
public class RemoveInclusionTests extends AbstractManualRewriterTests {
	
	// S <: T => !x.x : S => x : T
	String P1 = "(0 = 1) ⇒ {1} ⊆ {1, 2}";

	String resultP1 = "0=1⇒(∀x·x∈{1}⇒x∈{1,2})";

	String P2 = "∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}";

	String resultP2 = "∀x·x=TRUE⇒(∀x0·x0∈{x}⇒x0∈{x,FALSE})";

	String P3 = "(0 = 1) ⇒ ∅ ⊆ {1, 2}";

	String resultP3 = "0=1⇒⊤";

	String P4 = "∀x·x = TRUE ⇒ ∅ ⊆ {x, FALSE}";

	String resultP4 = "∀x·x=TRUE⇒⊤";

	String P5 = "(0 = 1) ⇒ {1, 2} ⊆ {1, 2}";

	String resultP5 = "0=1⇒⊤";

	String P6 = "∀x·x = TRUE ⇒ {x, FALSE} ⊆ {x, FALSE}";

	String resultP6 = "∀x·x=TRUE⇒⊤";

	String P7 = "(0 = 1) ⇒ {1 ↦ 3} ⊆ {1 ↦ 2, 2 ↦ 3}";

	String resultP7 = "0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ 3}⇒x ↦ x0∈{1 ↦ 2,2 ↦ 3})";

	String P8 = "∀x·x = TRUE ⇒ {x ↦ 1} ⊆ {x ↦ 2, x ↦ 3}";

	String resultP8 = "∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{x ↦ 1}⇒x0 ↦ x1∈{x ↦ 2,x ↦ 3})";

	String P9 = "(0 = 1) ⇒ {(0 ↦ 2) ↦ (2 ↦ 3)} ⊆ {(1 ↦ 2) ↦ (2 ↦ 3)}";

	String resultP9 = "0=1⇒(∀x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{0 ↦ 2 ↦ (2 ↦ 3)}⇒x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (2 ↦ 3)})";

	String P10 = "∀x·x = TRUE ⇒ {FALSE ↦ (2 ↦ 2) ↦ x} ⊆ {x ↦ (2 ↦ 2) ↦ TRUE}";

	String resultP10 = "∀x·x=TRUE⇒(∀x0,x1,x2,x3·x0 ↦ (x1 ↦ x2) ↦ x3∈{FALSE ↦ (2 ↦ 2) ↦ x}⇒x0 ↦ (x1 ↦ x2) ↦ x3∈{x ↦ (2 ↦ 2) ↦ TRUE})";

	String P11 = "(0 = 1) ⇒ {1 ↦ {2}} ⊆ {1 ↦ {2}, 2 ↦ {3}}";

	String resultP11 = "0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ {2}}⇒x ↦ x0∈{1 ↦ {2},2 ↦ {3}})";

	String P12 = "∀x·x = TRUE ⇒ {{x} ↦ 1} ⊆ {{x} ↦ 2, {x} ↦ 3}";

	String resultP12 = "∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{{x} ↦ 1}⇒x0 ↦ x1∈{{x} ↦ 2,{x} ↦ 3})";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.ri";
	}

	public String [] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1",
				P3, "1",
				P4, "1.1",
				P5, "1",
				P6, "1.1",
				P7, "1",
				P8, "1.1",
				P9, "1",
				P10, "1.1",
				P11, "1",
				P12, "1.1"
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.riGetPositions(predicate);
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] {
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1", resultP3),
				new SuccessfulTest(P4, "1.1", resultP4),
				new SuccessfulTest(P5, "1", resultP5),
				new SuccessfulTest(P6, "1.1", resultP6),
				new SuccessfulTest(P7, "1", resultP7),
				new SuccessfulTest(P8, "1.1", resultP8),
				new SuccessfulTest(P9, "1", resultP9),
				new SuccessfulTest(P10, "1.1", resultP10),
				new SuccessfulTest(P11, "1", resultP11),
				new SuccessfulTest(P12, "1.1", resultP12)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				P1, "0",
				P2, "1.0",
				P3, "0",
				P4, "1.0",
				P5, "0",
				P6, "1.0",
				P7, "0",
				P8, "1.0",
				P9, "0",
				P10, "1.0",
				P11, "0",
				P12, "1.0"
		};
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
