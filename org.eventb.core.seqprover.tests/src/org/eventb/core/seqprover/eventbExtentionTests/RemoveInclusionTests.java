package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.junit.Test;

/**
 * Unit tests for the ri (remove inclusion) reasoner
 * 
 * @author htson
 */
public class RemoveInclusionTests extends AbstractManualRewriterTests {

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
		

	/**
	 * Tests for applicable positions
	 */
	@Test
	public void testGetPositions() {
		testGetPosition(P1, "1");
		testGetPosition(P2, "1.1");
		testGetPosition(P3, "1");
		testGetPosition(P4, "1.1");
		testGetPosition(P5, "1");
		testGetPosition(P6, "1.1");
		testGetPosition(P7, "1");
		testGetPosition(P8, "1.1");
		testGetPosition(P9, "1");
		testGetPosition(P10, "1.1");
		testGetPosition(P11, "1");
		testGetPosition(P12, "1.1");
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.riGetPositions(predicate);
	}

	@Override
	protected String[] getSuccessfulTests() {
		return new String[] {
				// Test for P1
				P1, "1", resultP1,
				// Test for P2
				P2, "1.1", resultP2,
				// Test for P3
				P3, "1", resultP3,
				// Test for P4
				P4, "1.1", resultP4,
				// Test for P5
				P5, "1", resultP5,
				// Test for P6
				P6, "1.1", resultP6,
				// Test for P7
				P7, "1", resultP7,
				// Test for P8
				P8, "1.1", resultP8,
				// Test for P9
				P9, "1", resultP9,
				// Test for P10
				P10, "1.1", resultP10,
				// Test for P11
				P11, "1", resultP11,
				// Test for P12
				P12, "1.1", resultP12
		};
	}


	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				// Test for P1
				P1, "0",
				// Test for P2
				P2, "1.0",
				// Test for P3
				P3, "0",
				// Test for P4
				P4, "1.0",
				// Test for P5
				P5, "0",
				// Test for P6
				P6, "1.0",
				// Test for P7
				P7, "0",
				// Test for P8
				P8, "1.0",
				// Test for P9
				P9, "0",
				// Test for P10
				P10, "1.0",
				// Test for P11
				P11, "0",
				// Test for P12
				P12, "1.0"
		};
	}

}
