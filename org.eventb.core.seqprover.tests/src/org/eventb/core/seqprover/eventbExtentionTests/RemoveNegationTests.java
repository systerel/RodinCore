package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;

/**
 * Unit tests for the rn reasoner {@link RemoveNegation}
 * 
 * @author htson
 */
public class RemoveNegationTests extends AbstractManualRewriterTests {

	String P1 = "(0 = 1) ⇒ (¬ (1 = 2 ∧ 2 = 3 ∧ 3 = 4))";

	String resultP1 = "0=1⇒¬1=2∨¬2=3∨¬3=4";
	
	String P2 = "∀x·x = 0 ⇒ (¬ (x = 1 ∧ x = 2 ∧ x = 3))";

	String resultP2 = "∀x·x=0⇒¬x=1∨¬x=2∨¬x=3";
	
	String P3 = "(0 = 1) ⇒ (¬ (1 = 2 \u2228 2 = 3 \u2228 3 = 4))";

	String resultP3 = "0=1⇒¬1=2∧¬2=3∧¬3=4";
	
	String P4 = "∀x·x = 0 ⇒ (¬ (x = 1 \u2228 x = 2 \u2228 x = 3))";
	
	String resultP4 = "∀x·x=0⇒¬x=1∧¬x=2∧¬x=3"; 
	
	String P5 = "(0 = 1) ⇒ (¬⊤)";

	String resultP5 = "0=1⇒⊥";

	String P6 = "∀x·x = 0 ⇒ (¬⊤)";

	String resultP6 = "∀x·x=0⇒⊥";
	
	String P7 = "(0 = 1) ⇒ (¬⊥)";

	String resultP7 = "0=1⇒⊤";
	
	String P8 = "∀x·x = 0 ⇒ (¬⊥)";

	String resultP8 = "∀x·x=0⇒⊤";
	
	String P9 = "(0 = 1) ⇒ (¬¬(1=2))";

	String resultP9 = "0=1⇒1=2";
	
	String P10 = "∀x·x = 0 ⇒ (¬¬(x=1))";

	String resultP10 = "∀x·x=0⇒x=1";
	
	String P11 = "(0 = 1) ⇒ (¬(1 = 2 ⇒ 2 = 3))";
	
	String resultP11 = "0=1⇒1=2∧¬2=3";
	
	String P12 = "∀x·x = 0 ⇒ (¬(x = 1 ⇒ x = 2))";

	String resultP12 = "∀x·x=0⇒x=1∧¬x=2";
	
	String P13 = "(0 = 1) ⇒ (¬(∀y·y ∈ ℕ ⇒ 0 ≤ y))";

	String resultP13 = "0=1⇒(∃y·¬(y∈ℕ⇒0≤y))";
	
	String P14 = "∀x·x = 0 ⇒ (¬(∀y·y ∈ ℕ ⇒ x ≤ y))";

	String resultP14 = "∀x·x=0⇒(∃y·¬(y∈ℕ⇒x≤y))";
	
	String P15 = "(0 = 1) ⇒ ¬({1} = ∅)";

	String resultP15 = "0=1⇒(∃x·x∈{1})";
	
	String P16 = "∀x·x = 0 ⇒ ¬({x} = ∅)";

	String resultP16 = "∀x·x=0⇒(∃x0·x0∈{x})";

	String P17 = "(0 = 1) ⇒ ¬({1 ↦ 2} = ∅)";
	
	String resultP17 = "0=1⇒(∃x,x0·x ↦ x0∈{1 ↦ 2})";
	
	String P18 = "∀x·x = 0 ⇒ ¬({x ↦ 0} = ∅)";
	
	String resultP18 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{x ↦ 0})";

	String P19 = "(0 = 1) ⇒ ¬({(1 ↦ 2) ↦ (3 ↦ 4)} = ∅)";

	String resultP19 = "0=1⇒(∃x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (3 ↦ 4)})";
	
	String P20 = "∀x·x = 0 ⇒ ¬({1 ↦ ((x ↦ 0) ↦ x)} = ∅)";

	String resultP20 = "∀x·x=0⇒(∃x0,x1,x2,x3·x0 ↦ (x1 ↦ x2 ↦ x3)∈{1 ↦ (x ↦ 0 ↦ x)})";
	
	String P21 = "(0 = 1) ⇒ ¬({1 ↦ {2}} = ∅)";

	String resultP21 = "0=1⇒(∃x,x0·x ↦ x0∈{1 ↦ {2}})";
	
	String P22 = "∀x·x = 0 ⇒ ¬({{x} ↦ 0} = ∅)";
	
	String resultP22 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{{x} ↦ 0})";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.rn";
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.rnGetPositions(predicate);
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
				P12, "1.1", resultP12,
				// Test for P13
				P13, "1", resultP13,
				// Test for P14
				P14, "1.1", resultP14,
				// Test for P15
				P15, "1", resultP15,
				// Test for P16
				P16, "1.1", resultP16,
				// Test for P17
				P17, "1", resultP17,
				// Test for P18
				P18, "1.1", resultP18,
				// Test for P19
				P19, "1", resultP19,
				// Test for P20
				P20, "1.1", resultP20,
				// Test for P21
				P21, "1", resultP21,
				// Test for P22
				P22, "1.1", resultP22
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
				P12, "1.0",
				// Test for P13
				P13, "0",
				// Test for P14
				P14, "1.0",
				// Test for P15
				P15, "0",
				// Test for P16
				P16, "1.0",
				// Test for P17
				P17, "0",
				// Test for P18
				P18, "1.0",
				// Test for P19
				P19, "0",
				// Test for P20
				P20, "1.0",
				// Test for P21
				P21, "0",
				// Test for P22
				P22, "1.0"
		};
	}

	@Override
	protected String[] getTestGetPositions() {
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
				P12, "1.1",
				P13, "1",
				P14, "1.1",
				P15, "1",
				P16, "1.1",
				P17, "1",
				P18, "1.1",
				P19, "1",
				P20, "1.1",
				P21, "1",
				P22, "1.1"
		};
	}

}
