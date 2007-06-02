package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.junit.Test;

/**
 * Unit tests for the rm reasoner {@link RemoveMembership}
 * 
 * @author htson
 */
public class RemoveMembershipTests extends AbstractManualRewriterTests {

	String P1 = "(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)";

	String resultP1 = "0=1⇒1∈ℕ∧2∈ℕ";
	
	String P2 = "∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ";

	String resultP2 = "∀x·x=0⇒x∈ℕ∧x∈ℕ";
	
	String P3 = "(0 = 1) ⇒ {1} ∈ ℙ(ℕ)";

	String resultP3 = "0=1⇒{1}⊆ℕ";
	
	String P4 = "∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)";

	String resultP4 = "∀x·x=0⇒{x}⊆ℕ";
	
	String P5 = "(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}";

	String resultP5 = "0=1⇒1∈{1}∨1∈{2}∨1∈{3}";

	String P6 = "∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}";

	String resultP6 = "∀x·x=0⇒x∈{1}∨x∈{2}∨x∈{3}";
	
	String P7 = "(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}";
	
	String resultP7 = "0=1⇒1∈{1}∧1∈{2}∧1∈{3}";
	
	String P8 = "∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}";

	String resultP8 = "∀x·x=0⇒x∈{1}∧x∈{2}∧x∈{3}";
	
	String P9 = "(0 = 1) ⇒ 1 ∈ {1} ∖ {2}";

	String resultP9 = "0=1⇒1∈{1}∧¬1∈{2}";
	
	String P10 = "∀x·x = 0 ⇒ x ∈ {x} ∖ {1}";

	String resultP10 = "∀x·x=0⇒x∈{x}∧¬x∈{1}";
	
	String P11 = "(0 = 1) ⇒ 0 ∈ {1, 2, 3}";

	String resultP11 = "0=1⇒0=1∨0=2∨0=3";
	
	String P12 = "∀x·x = 0 ⇒ x ∈ {1, 2, 3}";

	String resultP12 = "∀x·x=0⇒x=1∨x=2∨x=3";
	
	String P13 = "(0 = 1) ⇒ 0 ∈ {0, 1, 2}";

	String resultP13 = "0=1⇒⊤";
	
	String P14 = "∀x·x = 0 ⇒ x ∈ {1, x, 3}";

	String resultP14 = "∀x·x=0⇒⊤";
	
	String P15 = "(0 = 1) ⇒ 0 ∈ {1}";

	String resultP15 = "0=1⇒0=1";
	
	String P16 = "∀x·x = 0 ⇒ x ∈ {1}";

	String resultP16 = "∀x·x=0⇒x=1";
	
	String P17 = "(0 = 1) ⇒ 0 ∈ union({{1},{2}})";

	String resultP17 = "0=1⇒(∃s·s∈{{1},{2}}∧0∈s)";
	
	String P18 = "∀x·x = 0 ⇒ x ∈ union({{1},{2}})";
	
	String resultP18 = "∀x·x=0⇒(∃s·s∈{{1},{2}}∧x∈s)";
	
	String P19 = "(0 = 1) ⇒ 0 ∈ inter({{1},{2}})";

	String resultP19 = "0=1⇒(∀s·s∈{{1},{2}}⇒0∈s)";
	
	String P20 = "∀x·x = 0 ⇒ x ∈ inter({{1},{2}})";

	String resultP20 = "∀x·x=0⇒(∀s·s∈{{1},{2}}⇒x∈s)";
	
	String P21 = "(0 = 1) ⇒ (0 ∈ (\u22c3 x · x ∈ ℕ \u2223 {x+1}))";

	String resultP21 = "0=1⇒(∃x·x∈ℕ∧0∈{x+1})";
	
	String P22 = "∀x·x = 0 ⇒ x ∈ (\u22c3 y·y∈ℕ \u2223 {x + y})";

	String resultP22 = "∀x·x=0⇒(∃y·y∈ℕ∧x∈{x+y})";
	
	String P23 = "(0 = 1) ⇒ (0 ∈ (\u22c2 x · x ∈ ℕ \u2223 {x+1}))";

	String resultP23 = "0=1⇒(∀x·x∈ℕ⇒0∈{x+1})"; 
		
	String P24 = "∀x·x = 0 ⇒ x ∈ (\u22c2 y·y∈ℕ \u2223 {x + y})";

	String resultP24 = "∀x·x=0⇒(∀y·y∈ℕ⇒x∈{x+y})";
	
	String P25 = "(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})";

	String resultP25 = "0=1⇒(∃y·0 ↦ y∈{0 ↦ 1})";
	
	String P26 = "∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})";

	String resultP26 = "∀x·x=0⇒(∃y·x ↦ y∈{x ↦ 1,x ↦ 2})";
	
	String P27 = "(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})";

	String resultP27 = "0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})";
	
	String P28 = "∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})";
	
	String resultP28 = "∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})";

	String P29 = "(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)";

	String resultP29 = "0=1⇒1 ↦ 0∈{1 ↦ 0}";
	
	String P30 = "∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)";

	String resultP30 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	String P31 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})";

	String resultP31 = "0=1⇒1∈{1}∧1 ↦ 0∈{1 ↦ 0}";
	
	String P32 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})";

	String resultP32 = "∀x·x=0⇒1∈{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	String P33 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})";

	String resultP33 = "0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}";
	
	String P34 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})";

	String resultP34 = "∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	String P35 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})";

	String resultP35 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}";
	
	String P36 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})";

	String resultP36 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}";
	
	String P37 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})";

	String resultP37 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}";
	
	String P38 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})";
	
	String resultP38 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}";

	String P39 = "(0 = 1) ⇒ 1 ∈ r[{0, 1}]";

	String resultP39 = "0=1⇒(∃x·x∈{0,1}∧x ↦ 1∈r)";
	
	String P40 = "∀x·x = 0 ⇒ x ∈ r[{0, x}]";
	
	String resultP40 = "∀x·x=0⇒(∃x0·x0∈{0,x}∧x0 ↦ x∈r)";

	String P41 = "(0 = 1) ⇒ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]";

	String resultP41 = "0=1⇒(∃x,x0·x ↦ x0∈{0 ↦ 1,1 ↦ 2}∧x ↦ x0 ↦ 1∈r)";
	
	String P42 = "∀x·x = 0 ⇒ x ∈ r[{0 ↦ 1, 1 ↦ 2}]";
	
	String resultP42 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{0 ↦ 1,1 ↦ 2}∧x0 ↦ x1 ↦ x∈r)";

	String P43 = "(0 = 1) ⇒ 1 ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]";

	String resultP43 = "0=1⇒(∃x,x0·x ↦ x0∈{0 ↦ 1,1 ↦ 2}∧x ↦ x0 ↦ (1 ↦ 1)∈r)";
	
	String P44 = "∀x·x = 0 ⇒ x ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]";
	
	String resultP44 = "∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{0 ↦ 1,1 ↦ 2}∧x0 ↦ x1 ↦ (x ↦ 1)∈r)";

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.rm";
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
		testGetPosition(P13, "1");
		testGetPosition(P14, "1.1");
		testGetPosition(P15, "1");
		testGetPosition(P16, "1.1");
		testGetPosition(P17, "1");
		testGetPosition(P18, "1.1");
		testGetPosition(P19, "1");
		testGetPosition(P20, "1.1");
		testGetPosition(P21, "1");
		testGetPosition(P22, "1.1");
		testGetPosition(P23, "1");
		testGetPosition(P24, "1.1");
		testGetPosition(P25, "1");
		testGetPosition(P26, "1.1");
		testGetPosition(P27, "1");
		testGetPosition(P28, "1.1");
		testGetPosition(P29, "1");
		testGetPosition(P30, "1.1");
		testGetPosition(P31, "1");
		testGetPosition(P32, "1.1");
		testGetPosition(P33, "1");
		testGetPosition(P34, "1.1");
		testGetPosition(P35, "1");
		testGetPosition(P36, "1.1");
		testGetPosition(P37, "1");
		testGetPosition(P38, "1.1");
		testGetPosition(P39, "1");
		testGetPosition(P40, "1.1");
		testGetPosition(P41, "1");
		testGetPosition(P42, "1.1");
		testGetPosition(P43, "1");
		testGetPosition(P44, "1.1");
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.rmGetPositions(predicate);
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
				P22, "1.1", resultP22,
				// Test for P23
				P23, "1", resultP23,
				// Test for P24
				P24, "1.1", resultP24,
				// Test for P25
				P25, "1", resultP25,
				// Test for P26
				P26, "1.1", resultP26,
				// Test for P27
				P27, "1", resultP27,
				// Test for P28
				P28, "1.1", resultP28,
				// Test for P29
				P29, "1", resultP29,
				// Test for P30
				P30, "1.1", resultP30,
				// Test for P31
				P31, "1", resultP31,
				// Test for P32
				P32, "1.1", resultP32,
				// Test for P33
				P33, "1", resultP33,
				// Test for P34
				P34, "1.1", resultP34,
				// Test for P35
				P35, "1", resultP35,
				// Test for P36
				P36, "1.1", resultP36,
				// Test for P37
				P37, "1", resultP37,
				// Test for P38
				P38, "1.1", resultP38,
				// Test for P39
				P39, "1", resultP39,
				// Test for P40
				P40, "1.1", resultP40,
				// Test for P41
				P41, "1", resultP41,
				// Test for P42
				P42, "1.1", resultP42,
				// Test for P43
				P43, "1", resultP43,
				// Test for P44
				P44, "1.1", resultP44
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
				P22, "1.0",
				// Test for P23
				P23, "0",
				// Test for P24
				P24, "1.0",
				// Test for P25
				P25, "0",
				// Test for P26
				P26, "1.0",
				// Test for P27
				P27, "0",
				// Test for P28
				P28, "1.0",
				// Test for P29
				P29, "0",
				// Test for P30
				P30, "1.0",
				// Test for P31
				P31, "0",
				// Test for P32
				P32, "1.0",
				// Test for P33
				P33, "0",
				// Test for P34
				P34, "1.0",
				// Test for P35
				P35, "0",
				// Test for P36
				P36, "1.0",
				// Test for P37
				P37, "0",
				// Test for P38
				P38, "1.0",
				// Test for P39
				P39, "0",
				// Test for P40
				P40, "1.0",
				// Test for P41
				P41, "0",
				// Test for P42
				P42, "1.0",
				// Test for P43
				P43, "0",
				// Test for P44
				P44, "1.0"
		};
	}

}
