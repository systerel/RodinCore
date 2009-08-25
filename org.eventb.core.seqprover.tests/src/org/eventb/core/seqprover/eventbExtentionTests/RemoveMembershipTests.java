/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - allowing subclasses to provide a type environment
 *     Systerel - mathematical language V2
 *     Systerel - added DEF_IN_UPTO
 ******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;

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

	String resultP25 = "0=1⇒(∃x·0 ↦ x∈{0 ↦ 1})";
	
	String P26 = "∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})";

	String resultP26 = "∀x·x=0⇒(∃x0·x ↦ x0∈{x ↦ 1,x ↦ 2})";
	
	String P77 = "(0 = 1) ⇒ 0 ∈ dom({0 ↦ (1↦BOOL↦0)})";

	String resultP77 = "0=1⇒(∃x,x0,x1·0 ↦ (x ↦ x0 ↦ x1)∈{0 ↦ (1 ↦ BOOL ↦ 0)})";
	
	String P78 = "∀x·x = 0 ⇒ x ∈ dom({x ↦ (1↦BOOL↦0), x ↦ (2↦BOOL↦0)})";

	String resultP78 = "∀x·x=0⇒(∃x0,x1,x2·x ↦ (x0 ↦ x1 ↦ x2)∈{x ↦ (1 ↦ BOOL ↦ 0),x ↦ (2 ↦ BOOL ↦ 0)})";
	

	// F : ran(r) == #y. y |-> F : r
	String P27 = "(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})";

	String resultP27 = "0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})";
	
	String P28 = "∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})";
	
	String resultP28 = "∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})";

	String P79 = "(0 = 1) ⇒ 0 ∈ ran({1 ↦ BOOL ↦ 0 ↦ 1})";

	String resultP79 = "0=1⇒(∃x,x0,x1·x ↦ x0 ↦ x1 ↦ 0∈{1 ↦ BOOL ↦ 0 ↦ 1})";
	
	String P80 = "∀x·x = 0 ⇒ x ∈ ran({1 ↦ BOOL ↦ x ↦ 1, 2 ↦ BOOL ↦ 0 ↦ x})";
	
	String resultP80 = "∀x·x=0⇒(∃x0,x1,x2·x0 ↦ x1 ↦ x2 ↦ x∈{1 ↦ BOOL ↦ x ↦ 1,2 ↦ BOOL ↦ 0 ↦ x})";
	
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
	
	String domRes3 = "e ∈ {1} ◁ {1 ↦ 0}";
	
	// E |-> F : S <<| r == E /: S & E |-> F : r
	String P33 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})";

	String resultP33 = "0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}";
	
	String P34 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})";

	String resultP34 = "∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}";
	
	String domSub3 = "e ∈ {1} ⩤ {1 ↦ 0}";
	
	//	 E |-> F : r |> T == E |-> F : r & F : T
	String P35 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})";

	String resultP35 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}";
	
	String P36 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})";

	String resultP36 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}";
	
	String ranRes3 = "e ∈ {1 ↦ 0} ▷ {0}";

	//	 E |-> F : r |>> T == E |-> F : r & F /: T
	String P37 = "(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})";

	String resultP37 = "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}";
	
	String P38 = "∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})";
	
	String resultP38 = "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}";

	String ranSub3 = "e ∈ {1 ↦ 0} ⩥ {0}";
	
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

	
	// E |-> F : id == E = F
	String P45 = "(0 = 1) ⇒ x ↦ 1 ∈ id";

	String resultP45 = "0=1⇒x=1";
	
	String P46 = "∀x·x = 0 ⇒ x ↦ y ∈ id";
	
	String resultP46 = "∀x·x=0⇒x=y";

	
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

	String P85 = "∀x, x0 · x ↦ x0 ∈ t ⇒ x ↦ x0 ∈ ℕ × ℕ ∧ x ↦ x0 ∈ t∼;((ℕ × ℕ) ∖ t)";
	
	String resultP85 = "∀x,x0·x ↦ x0∈t⇒x ↦ x0∈ℕ × ℕ∧(∃x1·x ↦ x1∈t∼∧x1 ↦ x0∈(ℕ × ℕ) ∖ t)";
	
	
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
	
	String resultP59 = "0=1⇒r∈ℕ × BOOL ↔ ℕ∧dom(r)=ℕ × BOOL∧ran(r)=ℕ";
	
	String P60 = "∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL";
	
	String resultP60 = "∀x·x=0⇒r∈ℕ ↔ {x} × BOOL∧dom(r)=ℕ∧ran(r)={x} × BOOL";

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


	// f : S >->> T == f : S >-> T & ran(f) = T
	String P75 = "(0 = 1) ⇒ f ∈ ℕ×BOOL ⤖ ℕ";
	
	String resultP75 = "0=1⇒f∈ℕ × BOOL ↣ ℕ∧ran(f)=ℕ";
	
	String P76 = "∀x·x = 0 ⇒ f ∈ ℕ ⤖ {x}×BOOL";
	
	String resultP76 = "∀x·x=0⇒f∈ℕ ↣ {x} × BOOL∧ran(f)={x} × BOOL";


	// E |-> (F |-> G) : p >< q == E |-> F : p & E |-> G : q
	String P81 = "(0 = x) ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q";
	
	String resultP81 = "0=x⇒x ↦ (1 ↦ 2)∈p∧x ↦ 3∈q";
	
	String P82 = "∀x·x = 0 ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q";
	
	String resultP82 = "∀x·x=0⇒x ↦ (1 ↦ 2)∈p∧x ↦ 3∈q";

	
	// E |-> G |-> (F |-> H) : p || q == E |-> F : p & G |-> H : q
	String P83 = "(0 = x) ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q";
	
	String resultP83 = "0=x⇒x ↦ (1 ↦ 2)∈p∧2 ↦ x ↦ 3∈q";
	
	String P84 = "∀x·x = 0 ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q";
	
	String resultP84 = "∀x·x=0⇒x ↦ (1 ↦ 2)∈p∧2 ↦ x ↦ 3∈q";

	
	// S : POW1(T) == S : POW(T) & S /= {}
	String P86 = "(0 = x) ⇒ {x, 1} ∈ ℙ1(T)";
	
	String resultP86 = "0=x⇒{x,1}∈ℙ(T)∧{x,1}≠∅";
	
	String P87 = "∀x·x = 0 ⇒ {x, 1} ∈ ℙ1(T)";
	
	String resultP87 = "∀x·x=0⇒{x,1}∈ℙ(T)∧{x,1}≠∅";

	// E : a .. b == a <= E & E <=b
	String P88 = "0 = x ⇒ x ∈ 0‥1";

	String resultP88 = "0=x⇒0≤x∧x≤1";

	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.rm";
	}
		
	public String [] getTestGetPositions() {
		return new String [] {
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
				P22, "1.1",
				P23, "1",
				P24, "1.1",
				P25, "1",
				P26, "1.1",
				P77, "1",
				P78, "1.1",
				P27, "1",
				P28, "1.1",
				P79, "1",
				P80, "1.1",
				P29, "1",
				P30, "1.1",
				P31, "1",
				P32, "1.1",
				P33, "1",
				P34, "1.1",
				P35, "1",
				P36, "1.1",
				P37, "1",
				P38, "1.1",
				P39, "1",
				P40, "1.1",
				P41, "1",
				P42, "1.1",
				P43, "1",
				P44, "1.1",
				P45, "1",
				P46, "1.1",
				P47, "1",
				P48, "1.1",
				P49, "1",
				P50, "1.1",
				P51, "1",
				P52, "1.1",
				P53, "1",
				P54, "1.1",
				P85, "2.1.0\n" + "2.1.1",
				P55, "1",
				P56, "1.1",
				P57, "1",
				P58, "1.1",
				P59, "1",
				P60, "1.1",
				P61, "1",
				P62, "1.1",
				P63, "1",
				P64, "1.1",
				P65, "1",
				P66, "1.1",
				P67, "1",
				P68, "1.1",
				P69, "1",
				P70, "1.1",
				P71, "1",
				P72, "1.1",
				P73, "1",
				P74, "1.1",
				P75, "1",
				P76, "1.1",
				P81, "1",
				P82, "1.1",
				P83, "1",
				P84, "1.1",
				P86, "1",
				P87, "1.1",
				P88, "1",
				domRes3, "",
				domSub3, "",
				ranRes3, "",
				ranSub3, ""
		};
	}

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.rmGetPositions(predicate);
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
				new SuccessfulTest(P12, "1.1", resultP12),
				new SuccessfulTest(P13, "1", resultP13),
				new SuccessfulTest(P14, "1.1", resultP14),
				new SuccessfulTest(P15, "1", resultP15),
				new SuccessfulTest(P16, "1.1", resultP16),
				new SuccessfulTest(P17, "1", resultP17),
				new SuccessfulTest(P18, "1.1", resultP18),
				new SuccessfulTest(P19, "1", resultP19),
				new SuccessfulTest(P20, "1.1", resultP20),
				new SuccessfulTest(P21, "1", resultP21),
				new SuccessfulTest(P22, "1.1", resultP22),
				new SuccessfulTest(P23, "1", resultP23),
				new SuccessfulTest(P24, "1.1", resultP24),
				new SuccessfulTest(P25, "1", resultP25),
				new SuccessfulTest(P26, "1.1", resultP26),
				new SuccessfulTest(P77, "1", resultP77),
				new SuccessfulTest(P78, "1.1", resultP78),
				new SuccessfulTest(P27, "1", resultP27),
				new SuccessfulTest(P28, "1.1", resultP28),
				new SuccessfulTest(P79, "1", resultP79),
				new SuccessfulTest(P80, "1.1", resultP80),
				new SuccessfulTest(P29, "1", resultP29),
				new SuccessfulTest(P30, "1.1", resultP30),
				new SuccessfulTest(P31, "1", resultP31),
				new SuccessfulTest(P32, "1.1", resultP32),
				new SuccessfulTest(P33, "1", resultP33),
				new SuccessfulTest(P34, "1.1", resultP34),
				new SuccessfulTest(P35, "1", resultP35),
				new SuccessfulTest(P36, "1.1", resultP36),
				new SuccessfulTest(P37, "1", resultP37),
				new SuccessfulTest(P38, "1.1", resultP38),
				new SuccessfulTest(P39, "1", resultP39),
				new SuccessfulTest(P40, "1.1", resultP40),
				new SuccessfulTest(P41, "1", resultP41),
				new SuccessfulTest(P42, "1.1", resultP42),
				new SuccessfulTest(P43, "1", resultP43),
				new SuccessfulTest(P44, "1.1", resultP44),
				new SuccessfulTest(P45, "1", resultP45),
				new SuccessfulTest(P46, "1.1", resultP46),
				new SuccessfulTest(P47, "1", resultP47),
				new SuccessfulTest(P48, "1.1", resultP48),
				new SuccessfulTest(P49, "1", resultP49),
				new SuccessfulTest(P50, "1.1", resultP50),
				new SuccessfulTest(P51, "1", resultP51),
				new SuccessfulTest(P52, "1.1", resultP52),
				new SuccessfulTest(P53, "1", resultP53),
				new SuccessfulTest(P54, "1.1", resultP54),
				new SuccessfulTest(P85, "2.1.1", resultP85),
				new SuccessfulTest(P55, "1", resultP55),
				new SuccessfulTest(P56, "1.1", resultP56),
				new SuccessfulTest(P57, "1", resultP57),
				new SuccessfulTest(P58, "1.1", resultP58),
				new SuccessfulTest(P59, "1", resultP59),
				new SuccessfulTest(P60, "1.1", resultP60),
				new SuccessfulTest(P61, "1", resultP61),
				new SuccessfulTest(P62, "1.1", resultP62),
				new SuccessfulTest(P63, "1", resultP63),
				new SuccessfulTest(P64, "1.1", resultP64),
				new SuccessfulTest(P65, "1", resultP65),
				new SuccessfulTest(P66, "1.1", resultP66),
				new SuccessfulTest(P67, "1", resultP67),
				new SuccessfulTest(P68, "1.1", resultP68),
				new SuccessfulTest(P69, "1", resultP69),
				new SuccessfulTest(P70, "1.1", resultP70),
				new SuccessfulTest(P71, "1", resultP71),
				new SuccessfulTest(P72, "1.1", resultP72),
				new SuccessfulTest(P73, "1", resultP73),
				new SuccessfulTest(P74, "1.1", resultP74),
				new SuccessfulTest(P75, "1", resultP75),
				new SuccessfulTest(P76, "1.1", resultP76),
				new SuccessfulTest(P81, "1", resultP81),
				new SuccessfulTest(P82, "1.1", resultP82),
				new SuccessfulTest(P83, "1", resultP83),
				new SuccessfulTest(P84, "1.1", resultP84),
				new SuccessfulTest(P86, "1", resultP86),
				new SuccessfulTest(P87, "1.1", resultP87),
				new SuccessfulTest(P88, "1", resultP88),
		};
	}


	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {
				domRes3, "",
				domSub3, "",
				ranRes3, "",
				ranSub3, "",
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
				P77, "0",
				P78, "1.0",
				P27, "0",
				P28, "1.0",
				P79, "0",
				P80, "1.0",
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
				P85, "2.0.0",
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
				P76, "1.0",
				P81, "0",
				P82, "1.0",
				P83, "0",
				P84, "1.0",
				P86, "0",
				P87, "1.0",
		};
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
