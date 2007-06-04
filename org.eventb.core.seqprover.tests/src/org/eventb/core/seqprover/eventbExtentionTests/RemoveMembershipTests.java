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

	// E |-> F : S ** T  == E : S & F : T
	String P1 = "(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)";

	String resultP1 = "0=1⇒1∈ℕ∧2∈ℕ";
	
	String P2 = "∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ";

	String resultP2 = "∀x·x=0⇒x∈ℕ∧x∈ℕ";
	
	
	// E : POW(S)  == E <: S
	String P3 = "(0 = 1) ⇒ {1} ∈ ℙ(ℕ)";

	String resultP3 = "0=1⇒{1}⊆ℕ";
	
	String P4 = "∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)";

	String resultP4 = "∀x·x=0⇒{x}⊆ℕ";
	
	
	// E : S \/ ... \/ T  == E : S or ... or E : T
	String P5 = "(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}";

	String resultP5 = "0=1⇒1∈{1}∨1∈{2}∨1∈{3}";

	String P6 = "∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}";

	String resultP6 = "∀x·x=0⇒x∈{1}∨x∈{2}∨x∈{3}";
	
	
	// E : S /\ ... /\ T == E : S & ... & E : T
	String P7 = "(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}";
	
	String resultP7 = "0=1⇒1∈{1}∧1∈{2}∧1∈{3}";
	
	String P8 = "∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}";

	String resultP8 = "∀x·x=0⇒x∈{1}∧x∈{2}∧x∈{3}";
	
	
	// E : S \ T == E : S & not(E : T) 
	String P9 = "(0 = 1) ⇒ 1 ∈ {1} ∖ {2}";

	String resultP9 = "0=1⇒1∈{1}∧¬1∈{2}";
	
	String P10 = "∀x·x = 0 ⇒ x ∈ {x} ∖ {1}";

	String resultP10 = "∀x·x=0⇒x∈{x}∧¬x∈{1}";
	
	
	// E : {A, ..., B} == E = A or ... or E = B
	String P11 = "(0 = 1) ⇒ 0 ∈ {1, 2, 3}";

	String resultP11 = "0=1⇒0=1∨0=2∨0=3";
	
	String P12 = "∀x·x = 0 ⇒ x ∈ {1, 2, 3}";

	String resultP12 = "∀x·x=0⇒x=1∨x=2∨x=3";
	
	
	// B : {A, ..., B, ..., C} == true
	String P13 = "(0 = 1) ⇒ 0 ∈ {0, 1, 2}";

	String resultP13 = "0=1⇒⊤";
	
	String P14 = "∀x·x = 0 ⇒ x ∈ {1, x, 3}";

	String resultP14 = "∀x·x=0⇒⊤";
	

	// E : {F} == E = F (where F is a single expression)
	String P15 = "(0 = 1) ⇒ 0 ∈ {1}";

	String resultP15 = "0=1⇒0=1";
	
	String P16 = "∀x·x = 0 ⇒ x ∈ {1}";

	String resultP16 = "∀x·x=0⇒x=1";
	

	// E : union(S) == #s.s : S & E : s 
	String P17 = "(0 = 1) ⇒ 0 ∈ union({{1},{2}})";

	String resultP17 = "0=1⇒(∃s·s∈{{1},{2}}∧0∈s)";
	
	String P18 = "∀x·x = 0 ⇒ x ∈ union({{1},{2}})";
	
	String resultP18 = "∀x·x=0⇒(∃s·s∈{{1},{2}}∧x∈s)";
	

	// E : inter(S) == !s.s : S => E :s 
	String P19 = "(0 = 1) ⇒ 0 ∈ inter({{1},{2}})";

	String resultP19 = "0=1⇒(∀s·s∈{{1},{2}}⇒0∈s)";
	
	String P20 = "∀x·x = 0 ⇒ x ∈ inter({{1},{2}})";

	String resultP20 = "∀x·x=0⇒(∀s·s∈{{1},{2}}⇒x∈s)";
	
	
	// E : (UNION x. P | T) == #x. P & E : T
	String P21 = "(0 = 1) ⇒ (0 ∈ (\u22c3 x · x ∈ ℕ \u2223 {x+1}))";

	String resultP21 = "0=1⇒(∃x·x∈ℕ∧0∈{x+1})";
	
	String P22 = "∀x·x = 0 ⇒ x ∈ (\u22c3 y·y∈ℕ \u2223 {x + y})";

	String resultP22 = "∀x·x=0⇒(∃y·y∈ℕ∧x∈{x+y})";
	
	
	// E : (INTER x. P | T) == !x. P => E : T
	String P23 = "(0 = 1) ⇒ (0 ∈ (\u22c2 x · x ∈ ℕ \u2223 {x+1}))";

	String resultP23 = "0=1⇒(∀x·x∈ℕ⇒0∈{x+1})"; 
		
	String P24 = "∀x·x = 0 ⇒ x ∈ (\u22c2 y·y∈ℕ \u2223 {x + y})";

	String resultP24 = "∀x·x=0⇒(∀y·y∈ℕ⇒x∈{x+y})";
	
	
	// E : dom(r) == #y. E |-> y : r
	String P25 = "(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})";

	String resultP25 = "0=1⇒(∃y·0 ↦ y∈{0 ↦ 1})";
	
	String P26 = "∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})";

	String resultP26 = "∀x·x=0⇒(∃y·x ↦ y∈{x ↦ 1,x ↦ 2})";
	
	
	// F : ran(r) == #y. y |-> F : r
	String P27 = "(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})";

	String resultP27 = "0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})";
	
	String P28 = "∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})";
	
	String resultP28 = "∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})";

	
	// E |-> F :r~ == F |-> E : r
	String P29 = "(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)";

	String resultP29 = "0=1⇒1 ↦ 0∈{1 ↦ 0}";
	
	String P30 = "∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)";

	String resultP30 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}";
	

	// E |-> F : S <| r == E : S & E |-> F : r
	String P31 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})";

	String resultP31 = "0=1⇒1∈{1}∧1 ↦ 0∈{1 ↦ 0}";
	
	String P32 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})";

	String resultP32 = "∀x·x=0⇒1∈{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	
	// E |-> F : S <<| r == E /: S & E |-> F : r
	String P33 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})";

	String resultP33 = "0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}";
	
	String P34 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})";

	String resultP34 = "∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	
	//	 E |-> F : r |> T == E |-> F : r & F : T
	String P35 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})";

	String resultP35 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}";
	
	String P36 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})";

	String resultP36 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}";
	

	//	 E |-> F : r |>> T == E |-> F : r & F /: T
	String P37 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})";

	String resultP37 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}";
	
	String P38 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})";
	
	String resultP38 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}";

	
	// F : r[w] = #x.x : w & x |-> F : r
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

	
	// E |-> F : id(S) == E : S & F = E
	String P45 = "(0 = 1) ⇒ x ↦ 1 ∈ id({1, 2})";

	String resultP45 = "0=1⇒x∈{1,2}∧1=x";
	
	String P46 = "∀x·x = 0 ⇒ x ↦ y ∈ id({x, y})";
	
	String resultP46 = "∀x·x=0⇒x∈{x,y}∧y=x";

	
	// E |-> F : (p_1; p_2;...; p_n) == 
	//      #x_1, x_2, ..., x_(n-1) . E |-> x_1 : p1 &
    //                            x_1 |-> x_2 : p2 &
	//                            ... &
	//                            x_(n-1) |-> F : pn &
	String P47 = "(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}";
	
	String resultP47 = "0=1⇒(∃x·0 ↦ x∈{0 ↦ TRUE,1 ↦ FALSE}∧x ↦ 1∈{TRUE ↦ 1,FALSE ↦ 0})";
	
	String P48 = "∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}";
	
	String resultP48 = "∀x·x=0⇒(∃x0·x ↦ x0∈{0 ↦ TRUE,1 ↦ FALSE}∧x0 ↦ 1∈{TRUE ↦ 1,FALSE ↦ 0})";

	String P49 = "(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}";
	
	String resultP49 = "0=1⇒(∃x,x0·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x ↦ x0 ↦ 1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0})";
	
	String P50 = "∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}";
	
	String resultP50 = "∀x·x=0⇒(∃x0,x1·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x0 ↦ x1 ↦ 1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0})";

	String P51 = "(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}";
	
	String resultP51 = "0=1⇒(∃x,x0,x1·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x ↦ x0 ↦ x1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x1 ↦ 1∈{0 ↦ 0,1 ↦ 1})";
	
	String P52 = "∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}";
	
	String resultP52 = "∀x·x=0⇒(∃x0,x1,x2·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x0 ↦ x1 ↦ x2∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x2 ↦ 1∈{0 ↦ 0,1 ↦ 1})";

	String P53 = "(0 = 1) ⇒ (0 ↦ (0 ↦ 1)) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}";
	
	String resultP53 = "0=1⇒(∃x,x0,x1,x2·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x ↦ x0 ↦ x1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x1 ↦ x2∈{0 ↦ FALSE,1 ↦ TRUE}∧x2 ↦ (0 ↦ 1)∈{TRUE ↦ (0 ↦ 1)})";
	
	String P54 = "∀x·x = 0 ⇒ x ↦ (0 ↦ 1) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}";
	
	String resultP54 = "∀x·x=0⇒(∃x0,x1,x2,x3·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x0 ↦ x1 ↦ x2∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x2 ↦ x3∈{0 ↦ FALSE,1 ↦ TRUE}∧x3 ↦ (0 ↦ 1)∈{TRUE ↦ (0 ↦ 1)})";

	
	// r : S <<-> T == r : S <-> T & dom(r) = S
	String P55 = "(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ";
	
	String resultP55 = "0=1⇒r∈ℕ × BOOL ↔ ℕ∧dom(r)=ℕ × BOOL";
	
	String P56 = "∀x·x = 0 ⇒ r ∈ {x}×BOOL  ℕ";
	
	String resultP56 = "∀x·x=0⇒r∈{x} × BOOL ↔ ℕ∧dom(r)={x} × BOOL";


	// r : S <->> T == r : S <-> T & ran(r) = T
	String P57 = "(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ";
	
	String resultP57 = "0=1⇒r∈ℕ × BOOL ↔ ℕ∧ran(r)=ℕ";
	
	String P58 = "∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL";
	
	String resultP58 = "∀x·x=0⇒r∈ℕ ↔ {x} × BOOL∧ran(r)={x} × BOOL";


	// r : S <<->> T == r : S <->> T & r : S <<-> T
	String P59 = "(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ";
	
	String resultP59 = "0=1⇒r∈ℕ × BOOL  ℕ∧r∈ℕ × BOOL  ℕ";
	
	String P60 = "∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL";
	
	String resultP60 = "∀x·x=0⇒r∈ℕ  {x} × BOOL∧r∈ℕ  {x} × BOOL";

	// f : S +-> T == f : S <-> T & !x,y,z. x |-> y : f & x |-> z : f => y = z
	String P61 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ ℕ";
	
	String resultP61 = "0=1⇒f∈ℕ × BOOL ↔ ℕ∧(∀x,x0,x1,x2·x ↦ x0 ↦ x1∈f∧x ↦ x0 ↦ x2∈f⇒x1=x2)";
	
	String P62 = "∀x·x = 0 ⇒ {x ↦ TRUE ↦ 1} ∈ {x}×BOOL ⇸ ℕ";
	
	String resultP62 = "∀x·x=0⇒{x ↦ TRUE ↦ 1}∈{x} × BOOL ↔ ℕ∧(∀x0,x1,x2,x3·x0 ↦ x1 ↦ x2∈{x ↦ TRUE ↦ 1}∧x0 ↦ x1 ↦ x3∈{x ↦ TRUE ↦ 1}⇒x2=x3)";

	String P63 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ BOOL×ℕ";
	
	String resultP63 = "0=1⇒f∈ℕ × BOOL ↔ BOOL × ℕ∧(∀x,x0,x1,x2,x3,x4·x ↦ x0 ↦ (x1 ↦ x2)∈f∧x ↦ x0 ↦ (x3 ↦ x4)∈f⇒x1 ↦ x2=x3 ↦ x4)";
	
	String P64 = "∀x·x = 0 ⇒ {x ↦ TRUE ↦ (FALSE ↦ 1)} ∈ {x}×BOOL ⇸ BOOL×ℕ";
	
	String resultP64 = "∀x·x=0⇒{x ↦ TRUE ↦ (FALSE ↦ 1)}∈{x} × BOOL ↔ BOOL × ℕ∧(∀x0,x1,x2,x3,x4,x5·x0 ↦ x1 ↦ (x2 ↦ x3)∈{x ↦ TRUE ↦ (FALSE ↦ 1)}∧x0 ↦ x1 ↦ (x4 ↦ x5)∈{x ↦ TRUE ↦ (FALSE ↦ 1)}⇒x2 ↦ x3=x4 ↦ x5)";

	
	// f : S --> T == f : S +-> T & dom(f) = S
	String P65 = "(0 = 1) ⇒ f ∈ ℕ×BOOL → ℕ";
	
	String resultP65 = "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧dom(f)=ℕ × BOOL";
	
	String P66 = "∀x·x = 0 ⇒ f ∈ ℕ → {x}×BOOL";
	
	String resultP66 = "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧dom(f)=ℕ";


	// f : S >+> T == f : S +-> T & f : T +-> S
	String P67 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⤔ ℕ";
	
	String resultP67 = "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧f∼∈ℕ ⇸ ℕ × BOOL";
	
	String P68 = "∀x·x = 0 ⇒ f ∈ ℕ ⤔ {x}×BOOL";
	
	String resultP68 = "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧f∼∈{x} × BOOL ⇸ ℕ";


	// f : S >-> T == f : S >+> T & dom(f) = S
	String P69 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ↣ ℕ";
	
	String resultP69 = "0=1⇒f∈ℕ × BOOL ⤔ ℕ∧dom(f)=ℕ × BOOL";
	
	String P70 = "∀x·x = 0 ⇒ f ∈ ℕ ↣ {x}×BOOL";
	
	String resultP70 = "∀x·x=0⇒f∈ℕ ⤔ {x} × BOOL∧dom(f)=ℕ";


	// f : S +>> T == f : S +-> T & ran(f) = T
	String P71 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⤀ ℕ";
	
	String resultP71 = "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧ran(f)=ℕ";
	
	String P72 = "∀x·x = 0 ⇒ f ∈ ℕ ⤀ {x}×BOOL";
	
	String resultP72 = "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧ran(f)={x} × BOOL";


	// f : S ->> T == f : S +>> T & dom(f) = S
	String P73 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ↠ ℕ";
	
	String resultP73 = "0=1⇒f∈ℕ × BOOL ⤀ ℕ∧dom(f)=ℕ × BOOL";
	
	String P74 = "∀x·x = 0 ⇒ f ∈ ℕ ↠ {x}×BOOL";
	
	String resultP74 = "∀x·x=0⇒f∈ℕ ⤀ {x} × BOOL∧dom(f)=ℕ";


	// f : S >->> T == f : S >-> T & f : S ->> T
	String P75 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⤖ ℕ";
	
	String resultP75 = "0=1⇒f∈ℕ × BOOL ↣ ℕ∧f∈ℕ × BOOL ↠ ℕ";
	
	String P76 = "∀x·x = 0 ⇒ f ∈ ℕ ⤖ {x}×BOOL";
	
	String resultP76 = "∀x·x=0⇒f∈ℕ ↣ {x} × BOOL∧f∈ℕ ↠ {x} × BOOL";


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
		testGetPosition(P45, "1");
		testGetPosition(P46, "1.1");
		testGetPosition(P47, "1");
		testGetPosition(P48, "1.1");
		testGetPosition(P49, "1");
		testGetPosition(P50, "1.1");
		testGetPosition(P51, "1");
		testGetPosition(P52, "1.1");
		testGetPosition(P53, "1");
		testGetPosition(P54, "1.1");
		testGetPosition(P55, "1");
		testGetPosition(P56, "1.1");
		testGetPosition(P57, "1");
		testGetPosition(P58, "1.1");
		testGetPosition(P59, "1");
		testGetPosition(P60, "1.1");
		testGetPosition(P61, "1");
		testGetPosition(P62, "1.1");
		testGetPosition(P63, "1");
		testGetPosition(P64, "1.1");
		testGetPosition(P65, "1");
		testGetPosition(P66, "1.1");
		testGetPosition(P67, "1");
		testGetPosition(P68, "1.1");
		testGetPosition(P69, "1");
		testGetPosition(P70, "1.1");
		testGetPosition(P71, "1");
		testGetPosition(P72, "1.1");
		testGetPosition(P73, "1");
		testGetPosition(P74, "1.1");
		testGetPosition(P75, "1");
		testGetPosition(P76, "1.1");
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.rmGetPositions(predicate);
	}

	@Override
	protected String[] getSuccessfulTests() {
		return new String[] {
				P1, "1", resultP1,
				P2, "1.1", resultP2,
				P3, "1", resultP3,
				P4, "1.1", resultP4,
				P5, "1", resultP5,
				P6, "1.1", resultP6,
				P7, "1", resultP7,
				P8, "1.1", resultP8,
				P9, "1", resultP9,
				P10, "1.1", resultP10,
				P11, "1", resultP11,
				P12, "1.1", resultP12,
				P13, "1", resultP13,
				P14, "1.1", resultP14,
				P15, "1", resultP15,
				P16, "1.1", resultP16,
				P17, "1", resultP17,
				P18, "1.1", resultP18,
				P19, "1", resultP19,
				P20, "1.1", resultP20,
				P21, "1", resultP21,
				P22, "1.1", resultP22,
				P23, "1", resultP23,
				P24, "1.1", resultP24,
				P25, "1", resultP25,
				P26, "1.1", resultP26,
				P27, "1", resultP27,
				P28, "1.1", resultP28,
				P29, "1", resultP29,
				P30, "1.1", resultP30,
				P31, "1", resultP31,
				P32, "1.1", resultP32,
				P33, "1", resultP33,
				P34, "1.1", resultP34,
				P35, "1", resultP35,
				P36, "1.1", resultP36,
				P37, "1", resultP37,
				P38, "1.1", resultP38,
				P39, "1", resultP39,
				P40, "1.1", resultP40,
				P41, "1", resultP41,
				P42, "1.1", resultP42,
				P43, "1", resultP43,
				P44, "1.1", resultP44,
				P45, "1", resultP45,
				P46, "1.1", resultP46,
				P47, "1", resultP47,
				P48, "1.1", resultP48,
				P49, "1", resultP49,
				P50, "1.1", resultP50,
				P51, "1", resultP51,
				P52, "1.1", resultP52,
				P53, "1", resultP53,
				P54, "1.1", resultP54,
				P55, "1", resultP55,
				P56, "1.1", resultP56,
				P57, "1", resultP57,
				P58, "1.1", resultP58,
				P59, "1", resultP59,
				P60, "1.1", resultP60,
				P61, "1", resultP61,
				P62, "1.1", resultP62,
				P63, "1", resultP63,
				P64, "1.1", resultP64,
				P65, "1", resultP65,
				P66, "1.1", resultP66,
				P67, "1", resultP67,
				P68, "1.1", resultP68,
				P69, "1", resultP69,
				P70, "1.1", resultP70,
				P71, "1", resultP71,
				P72, "1.1", resultP72,
				P73, "1", resultP73,
				P74, "1.1", resultP74,
				P75, "1", resultP75,
				P76, "1.1", resultP76
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
				P12, "1.0",
				P13, "0",
				P14, "1.0",
				P15, "0",
				P16, "1.0",
				P17, "0",
				P18, "1.0",
				P19, "0",
				P20, "1.0",
				P21, "0",
				P22, "1.0",
				P23, "0",
				P24, "1.0",
				P25, "0",
				P26, "1.0",
				P27, "0",
				P28, "1.0",
				P29, "0",
				P30, "1.0",
				P31, "0",
				P32, "1.0",
				P33, "0",
				P34, "1.0",
				P35, "0",
				P36, "1.0",
				P37, "0",
				P38, "1.0",
				P39, "0",
				P40, "1.0",
				P41, "0",
				P42, "1.0",
				P43, "0",
				P44, "1.0",
				P45, "0",
				P46, "1.0",
				P47, "0",
				P48, "1.0",
				P49, "0",
				P50, "1.0",
				P51, "0",
				P52, "1.0",
				P53, "0",
				P54, "1.0",
				P55, "0",
				P56, "1.0",
				P57, "0",
				P58, "1.0",
				P59, "0",
				P60, "1.0",
				P61, "0",
				P62, "1.0",
				P63, "0",
				P64, "1.0",
				P65, "0",
				P66, "1.0",
				P67, "0",
				P68, "1.0",
				P69, "0",
				P70, "1.0",
				P71, "0",
				P72, "1.0",
				P73, "0",
				P74, "1.0",
				P75, "0",
				P76, "1.0"
		};
	}

}
