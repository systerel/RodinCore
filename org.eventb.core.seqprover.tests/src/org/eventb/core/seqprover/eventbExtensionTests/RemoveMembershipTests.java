/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
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
 *     Systerel - refactored to introduce level L1
 *     UPEC - refactored to use new test methods
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership.RMLevel;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipRewriterImpl;
import org.junit.Test;

/**
 * Unit tests for the rm reasoner {@link RemoveMembership}
 * 
 * @author htson
 */
public abstract class RemoveMembershipTests extends AbstractManualRewriterTests {

	private final String reasonerId;

	private final IFormulaFilter posFilter;

	private final RMLevel level;

	public RemoveMembershipTests(RemoveMembership rewriter) {
		super(rewriter);
		this.reasonerId = rewriter.getReasonerID();
		this.posFilter = new DefaultFilter() {
			@Override
			public boolean select(RelationalPredicate predicate) {
				return new RemoveMembershipRewriterImpl(level, false)
						.isApplicableOrRewrite(predicate);
			}
		};
		this.level = rewriter.getLevel();
	}

	@Override
	protected final List<IPosition> getPositions(Predicate predicate) {
		return predicate.getPositions(posFilter);
	}

	@Override
	public String getReasonerID() {
		return reasonerId;
	}
		
	@Test
	public void testPositions() {
		// E |-> F : S ** T == E : S & F : T
		assertGetPositions("(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ", "1.1");

		// E : POW(S) == E <: S
		assertGetPositions("(0 = 1) ⇒ {1} ∈ ℙ(ℕ)", "1");
		assertGetPositions("∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)", "1.1");

		// E : S \/ ... \/ T == E : S or ... or E : T
		assertGetPositions("(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}", "1.1");

		// E : S /\ ... /\ T == E : S & ... & E : T
		assertGetPositions("(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}", "1.1");

		// E : S \ T == E : S & not(E : T)
		assertGetPositions("(0 = 1) ⇒ 1 ∈ {1} ∖ {2}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {x} ∖ {1}", "1.1");

		// E : {A, ..., B} == E = A or ... or E = B
		assertGetPositions("(0 = 1) ⇒ 0 ∈ {1, 2, 3}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {1, 2, 3}", "1.1");

		// B : {A, ..., B, ..., C} == true
		assertGetPositions("(0 = 1) ⇒ 0 ∈ {0, 1, 2}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {1, x, 3}", "1.1");

		// E : {F} == E = F (where F is a single expression)
		assertGetPositions("(0 = 1) ⇒ 0 ∈ {1}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {1}", "1.1");

		// E : union(S) == #s.s : S & E : s
		assertGetPositions("(0 = 1) ⇒ 0 ∈ union({{1},{2}})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ union({{1},{2}})", "1.1");

		// E : inter(S) == !s.s : S => E :s
		assertGetPositions("(0 = 1) ⇒ 0 ∈ inter({{1},{2}})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ inter({{1},{2}})", "1.1");

		// E : (UNION x. P | T) == #x. P & E : T
		assertGetPositions("(0 = 1) ⇒ (0 ∈ (⋃ x · x ∈ ℕ ∣ {x+1}))", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ (⋃ y·y∈ℕ ∣ {x + y})", "1.1");

		// E : (INTER x. P | T) == !x. P => E : T
		assertGetPositions("(0 = 1) ⇒ (0 ∈ (⋂ x · x ∈ ℕ ∣ {x+1}))", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ (⋂ y·y∈ℕ ∣ {x + y})", "1.1");

		// E : dom(r) == #y. E |-> y : r
		assertGetPositions("(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})", "1.1");
		assertGetPositions("(0 = 1) ⇒ 0 ∈ dom({0 ↦ (1↦BOOL↦0)})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ dom({x ↦ (1↦BOOL↦0), x ↦ (2↦BOOL↦0)})", "1.1");

		// F : ran(r) == #y. y |-> F : r
		assertGetPositions("(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})", "1.1");
		assertGetPositions("(0 = 1) ⇒ 0 ∈ ran({1 ↦ BOOL ↦ 0 ↦ 1})", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ ran({1 ↦ BOOL ↦ x ↦ 1, 2 ↦ BOOL ↦ 0 ↦ x})", "1.1");

		// E |-> F :r~ == F |-> E : r
		assertGetPositions("(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)", "1");
		assertGetPositions("∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)", "1.1");

		// E |-> F : S <| r == E : S & E |-> F : r
		assertGetPositions("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})", "1");
		assertGetPositions("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})", "1.1");

		// E |-> F : S <<| r == E /: S & E |-> F : r
		assertGetPositions("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})", "1");
		assertGetPositions("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})", "1.1");

		// E |-> F : r |> T == E |-> F : r & F : T
		assertGetPositions("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})", "1");
		assertGetPositions("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})", "1.1");

		// E |-> F : r |>> T == E |-> F : r & F /: T
		assertGetPositions("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})", "1");
		assertGetPositions("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})", "1.1");

		// F : r[w] = #x.x : w & x |-> F : r
		assertGetPositions("(0 = 1) ⇒ 1 ∈ r[{0, 1}]", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ r[{0, x}]", "1.1");
		assertGetPositions("(0 = 1) ⇒ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.1");
		assertGetPositions("(0 = 1) ⇒ 1 ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.1");

		// E |-> F : id == E = F
		assertGetPositions("(0 = 1) ⇒ x ↦ 1 ∈ id", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ y ∈ id", "1.1");

		// E |-> F : (p_1; p_2;...; p_n) ==
		// #x_1, x_2, ..., x_(n-1) . E |-> x_1 : p1 &
		//                            x_1 |-> x_2 : p2 &
		// ... &
		// x_(n-1) |-> F : pn &
		assertGetPositions("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "1.1");
		assertGetPositions("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}", "1.1");
		assertGetPositions(
				"(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}",
				"1");
		assertGetPositions(
				"∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}",
				"1.1");
		assertGetPositions("(0 = 1) ⇒ (0 ↦ (0 ↦ 1)) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};"
				+ "{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ (0 ↦ 1) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};"
				+ "{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}", "1.1");
		assertGetPositions("∀x, x0 · x ↦ x0 ∈ t ⇒ x ↦ x0 ∈ ℕ × ℕ ∧ x ↦ x0 ∈ t∼;((ℕ × ℕ) ∖ t)", "2.1.0", "2.1.1");

		// r : S <<-> T == r : S <-> T & dom(r) = S
		assertGetPositions("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ r ∈ {x}×BOOL  ℕ", "1.1");

		// r : S <->> T == r : S <-> T & ran(r) = T
		assertGetPositions("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.1");

		// r : S <<->> T == r : S <->> T & r : S <<-> T
		assertGetPositions("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.1");

		// f : S +-> T == f : S <-> T & !x,y,z. x |-> y : f & x |-> z : f => y = z
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ {x ↦ TRUE ↦ 1} ∈ {x}×BOOL ⇸ ℕ", "1.1");
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ BOOL×ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ {x ↦ TRUE ↦ (FALSE ↦ 1)} ∈ {x}×BOOL ⇸ BOOL×ℕ", "1.1");

		// f : S --> T == f : S +-> T & dom(f) = S
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL → ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ → {x}×BOOL", "1.1");

		// f : S >+> T == f : S +-> T & f : T +-> S
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤔ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ ⤔ {x}×BOOL", "1.1");

		// f : S >-> T == f : S >+> T & dom(f) = S
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ↣ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ ↣ {x}×BOOL", "1.1");

		// f : S +>> T == f : S +-> T & ran(f) = T
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤀ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ ⤀ {x}×BOOL", "1.1");

		// f : S ->> T == f : S +>> T & dom(f) = S
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ↠ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ ↠ {x}×BOOL", "1.1");

		// f : S >->> T == f : S >-> T & ran(f) = T
		assertGetPositions("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤖ ℕ", "1");
		assertGetPositions("∀x·x = 0 ⇒ f ∈ ℕ ⤖ {x}×BOOL", "1.1");

		// E |-> (F |-> G) : p >< q == E |-> F : p & E |-> G : q
		assertGetPositions("(0 = x) ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "1.1");

		// E |-> G |-> (F |-> H) : p || q == E |-> F : p & G |-> H : q
		assertGetPositions("(0 = x) ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "1.1");

		// S : POW1(T) == S : POW(T) & S /= {}
		assertGetPositions("(0 = x) ⇒ {x, 1} ∈ ℙ1(T)", "1");
		assertGetPositions("∀x·x = 0 ⇒ {x, 1} ∈ ℙ1(T)", "1.1");

		// E : a .. b == a <= E & E <=b
		assertGetPositions("0 = x ⇒ x ∈ 0‥1", "1");

		assertGetPositions("e ∈ {1} ◁ {1 ↦ 0}");
		assertGetPositions("e ∈ {1} ⩤ {1 ↦ 0}");
		assertGetPositions("e ∈ {1 ↦ 0} ▷ {0}");
		assertGetPositions("e ∈ {1 ↦ 0} ⩥ {0}");
	}

	@Test
	public void testSuccessful() throws Exception {
		// E |-> F : S ** T == E : S & F : T
		assertReasonerSuccess("(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)", "1", "0=1⇒1∈ℕ∧2∈ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ", "1.1", "∀x·x=0⇒x∈ℕ∧x∈ℕ");

		// E : POW(S) == E <: S
		assertReasonerSuccess("(0 = 1) ⇒ {1} ∈ ℙ(ℕ)", "1", "0=1⇒{1}⊆ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)", "1.1", "∀x·x=0⇒{x}⊆ℕ");

		// E : S \/ ... \/ T == E : S or ... or E : T
		assertReasonerSuccess("(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}", "1", "0=1⇒1∈{1}∨1∈{2}∨1∈{3}");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}", "1.1", "∀x·x=0⇒x∈{1}∨x∈{2}∨x∈{3}");

		// E : S /\ ... /\ T == E : S & ... & E : T
		assertReasonerSuccess("(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}", "1", "0=1⇒1∈{1}∧1∈{2}∧1∈{3}");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}", "1.1", "∀x·x=0⇒x∈{1}∧x∈{2}∧x∈{3}");

		// E : S \ T == E : S & not(E : T)
		assertReasonerSuccess("(0 = 1) ⇒ 1 ∈ {1} ∖ {2}", "1", "0=1⇒1∈{1}∧¬1∈{2}");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {x} ∖ {1}", "1.1", "∀x·x=0⇒x∈{x}∧¬x∈{1}");

		// E : {A, ..., B} == E = A or ... or E = B
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ {1, 2, 3}", "1", "0=1⇒0=1∨0=2∨0=3");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {1, 2, 3}", "1.1", "∀x·x=0⇒x=1∨x=2∨x=3");

		// B : {A, ..., B, ..., C} == true
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ {0, 1, 2}", "1", "0=1⇒⊤");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {1, x, 3}", "1.1", "∀x·x=0⇒⊤");

		// E : {F} == E = F (where F is a single expression)
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ {1}", "1", "0=1⇒0=1");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ {1}", "1.1", "∀x·x=0⇒x=1");

		// E : union(S) == #s.s : S & E : s
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ union({{1},{2}})", "1", "0=1⇒(∃s·s∈{{1},{2}}∧0∈s)");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ union({{1},{2}})", "1.1", "∀x·x=0⇒(∃s·s∈{{1},{2}}∧x∈s)");

		// E : inter(S) == !s.s : S => E :s
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ inter({{1},{2}})", "1", "0=1⇒(∀s·s∈{{1},{2}}⇒0∈s)");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ inter({{1},{2}})", "1.1", "∀x·x=0⇒(∀s·s∈{{1},{2}}⇒x∈s)");

		// E : (UNION x. P | T) == #x. P & E : T
		assertReasonerSuccess("(0 = 1) ⇒ (0 ∈ (⋃ x · x ∈ ℕ ∣ {x+1}))", "1", "0=1⇒(∃x·x∈ℕ∧0∈{x+1})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ (⋃ y·y∈ℕ ∣ {x + y})", "1.1", "∀x·x=0⇒(∃y·y∈ℕ∧x∈{x+y})");

		// E : (INTER x. P | T) == !x. P => E : T
		assertReasonerSuccess("(0 = 1) ⇒ (0 ∈ (⋂ x · x ∈ ℕ ∣ {x+1}))", "1", "0=1⇒(∀x·x∈ℕ⇒0∈{x+1})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ (⋂ y·y∈ℕ ∣ {x + y})", "1.1", "∀x·x=0⇒(∀y·y∈ℕ⇒x∈{x+y})");

		// E : dom(r) == #y. E |-> y : r
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})", "1", "0=1⇒(∃x·0 ↦ x∈{0 ↦ 1})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})", "1.1", "∀x·x=0⇒(∃x0·x ↦ x0∈{x ↦ 1,x ↦ 2})");
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ dom({0 ↦ (1↦BOOL↦0)})", "1",
				"0=1⇒(∃x,x0,x1·0 ↦ (x ↦ x0 ↦ x1)∈{0 ↦ (1 ↦ BOOL ↦ 0)})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ dom({x ↦ (1↦BOOL↦0), x ↦ (2↦BOOL↦0)})", "1.1",
				"∀x·x=0⇒(∃x0,x1,x2·x ↦ (x0 ↦ x1 ↦ x2)∈{x ↦ (1 ↦ BOOL ↦ 0),x ↦ (2 ↦ BOOL ↦ 0)})");

		// F : ran(r) == #y. y |-> F : r
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})", "1", "0=1⇒(∃x·x ↦ 0∈{0 ↦ 1})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})", "1.1", "∀x·x=0⇒(∃x0·x0 ↦ x∈{x ↦ 1,2 ↦ x})");
		assertReasonerSuccess("(0 = 1) ⇒ 0 ∈ ran({1 ↦ BOOL ↦ 0 ↦ 1})", "1",
				"0=1⇒(∃x,x0,x1·x ↦ x0 ↦ x1 ↦ 0∈{1 ↦ BOOL ↦ 0 ↦ 1})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ ran({1 ↦ BOOL ↦ x ↦ 1, 2 ↦ BOOL ↦ 0 ↦ x})", "1.1",
				"∀x·x=0⇒(∃x0,x1,x2·x0 ↦ x1 ↦ x2 ↦ x∈{1 ↦ BOOL ↦ x ↦ 1,2 ↦ BOOL ↦ 0 ↦ x})");

		// E |-> F :r~ == F |-> E : r
		assertReasonerSuccess("(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)", "1", "0=1⇒1 ↦ 0∈{1 ↦ 0}");
		assertReasonerSuccess("∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)", "1.1", "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}");

		// E |-> F : S <| r == E : S & E |-> F : r
		assertReasonerSuccess("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})", "1", "0=1⇒1∈{1}∧1 ↦ 0∈{1 ↦ 0}");
		assertReasonerSuccess("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})", "1.1", "∀x·x=0⇒1∈{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}");

		// E |-> F : S <<| r == E /: S & E |-> F : r
		assertReasonerSuccess("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})", "1", "0=1⇒1∉{1}∧1 ↦ 0∈{1 ↦ 0}");
		assertReasonerSuccess("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})", "1.1", "∀x·x=0⇒1∉{1}∧1 ↦ x∈{1 ↦ x,x ↦ 2}");

		// E |-> F : r |> T == E |-> F : r & F : T
		assertReasonerSuccess("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})", "1", "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∈{0}");
		assertReasonerSuccess("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})", "1.1", "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∈{x}");

		// E |-> F : r |>> T == E |-> F : r & F /: T
		assertReasonerSuccess("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})", "1", "0=1⇒1 ↦ 0∈{1 ↦ 0}∧0∉{0}");
		assertReasonerSuccess("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})", "1.1", "∀x·x=0⇒1 ↦ x∈{1 ↦ x,x ↦ 2}∧x∉{x}");

		// F : r[w] = #x.x : w & x |-> F : r
		assertReasonerSuccess("(0 = 1) ⇒ 1 ∈ r[{0, 1}]", "1", "0=1⇒(∃x·x∈{0,1}∧x ↦ 1∈r)");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ r[{0, x}]", "1.1", "∀x·x=0⇒(∃x0·x0∈{0,x}∧x0 ↦ x∈r)");
		assertReasonerSuccess("(0 = 1) ⇒ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1", "0=1⇒(∃x,x0·x ↦ x0∈{0 ↦ 1,1 ↦ 2}∧x ↦ x0 ↦ 1∈r)");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.1",
				"∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{0 ↦ 1,1 ↦ 2}∧x0 ↦ x1 ↦ x∈r)");
		assertReasonerSuccess("(0 = 1) ⇒ 1 ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1",
				"0=1⇒(∃x,x0·x ↦ x0∈{0 ↦ 1,1 ↦ 2}∧x ↦ x0 ↦ (1 ↦ 1)∈r)");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.1",
				"∀x·x=0⇒(∃x0,x1·x0 ↦ x1∈{0 ↦ 1,1 ↦ 2}∧x0 ↦ x1 ↦ (x ↦ 1)∈r)");

		// E |-> F : id == E = F
		assertReasonerSuccess("(0 = 1) ⇒ x ↦ 1 ∈ id", "1", "0=1⇒x=1");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ y ∈ id", "1.1", "∀x·x=0⇒x=y");

		// E |-> F : (p_1; p_2;...; p_n) ==
		// #x_1, x_2, ..., x_(n-1) . E |-> x_1 : p1 &
		//                            x_1 |-> x_2 : p2 &
		// ... &
		// x_(n-1) |-> F : pn &
		assertReasonerSuccess("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "1",
				"0=1⇒(∃x·0 ↦ x∈{0 ↦ TRUE,1 ↦ FALSE}∧x ↦ 1∈{TRUE ↦ 1,FALSE ↦ 0})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "1.1",
				"∀x·x=0⇒(∃x0·x ↦ x0∈{0 ↦ TRUE,1 ↦ FALSE}∧x0 ↦ 1∈{TRUE ↦ 1,FALSE ↦ 0})");
		assertReasonerSuccess("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}", "1",
				"0=1⇒(∃x,x0·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x ↦ x0 ↦ 1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0})");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}",
				"1.1",
				"∀x·x=0⇒(∃x0,x1·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧x0 ↦ x1 ↦ 1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0})");
		assertReasonerSuccess(
				"(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}", "1",
				"0=1⇒(∃x,x0,x1·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧"
						+ "x ↦ x0 ↦ x1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x1 ↦ 1∈{0 ↦ 0,1 ↦ 1})");
		assertReasonerSuccess(
				"∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}",
				"1.1",
				"∀x·x=0⇒(∃x0,x1,x2·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧"
						+ "x0 ↦ x1 ↦ x2∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x2 ↦ 1∈{0 ↦ 0,1 ↦ 1})");
		assertReasonerSuccess(
				"(0 = 1) ⇒ (0 ↦ (0 ↦ 1)) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};"
						+ "{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}",
				"1",
				"0=1⇒(∃x,x0,x1,x2·0 ↦ (x ↦ x0)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧"
						+ "x ↦ x0 ↦ x1∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x1 ↦ x2∈{0 ↦ FALSE,1 ↦ TRUE}∧x2 ↦ (0 ↦ 1)∈{TRUE ↦ (0 ↦ 1)})");
		assertReasonerSuccess(
				"∀x·x = 0 ⇒ x ↦ (0 ↦ 1) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};"
						+ "{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}",
				"1.1", "∀x·x=0⇒(∃x0,x1,x2,x3·x ↦ (x0 ↦ x1)∈{0 ↦ (TRUE ↦ 1),1 ↦ (FALSE ↦ 1)}∧"
						+ "x0 ↦ x1 ↦ x2∈{TRUE ↦ 1 ↦ 1,FALSE ↦ 0 ↦ 0}∧x2 ↦ x3∈{0 ↦ FALSE,1 ↦ TRUE}∧x3 ↦ (0 ↦ 1)∈{TRUE ↦ (0 ↦ 1)})");
		assertReasonerSuccess("∀x, x0 · x ↦ x0 ∈ t ⇒ x ↦ x0 ∈ ℕ × ℕ ∧ x ↦ x0 ∈ t∼;((ℕ × ℕ) ∖ t)", "2.1.1",
				"∀x,x0·x ↦ x0∈t⇒x ↦ x0∈ℕ × ℕ∧(∃x1·x ↦ x1∈t∼∧x1 ↦ x0∈(ℕ × ℕ) ∖ t)");

		// r : S <<-> T == r : S <-> T & dom(r) = S
		assertReasonerSuccess("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1", "0=1⇒r∈ℕ × BOOL ↔ ℕ∧dom(r)=ℕ × BOOL");
		assertReasonerSuccess("∀x·x = 0 ⇒ r ∈ {x}×BOOL  ℕ", "1.1", "∀x·x=0⇒r∈{x} × BOOL ↔ ℕ∧dom(r)={x} × BOOL");

		// r : S <->> T == r : S <-> T & ran(r) = T
		assertReasonerSuccess("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1", "0=1⇒r∈ℕ × BOOL ↔ ℕ∧ran(r)=ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.1", "∀x·x=0⇒r∈ℕ ↔ {x} × BOOL∧ran(r)={x} × BOOL");

		// r : S <<->> T == r : S <->> T & r : S <<-> T
		assertReasonerSuccess("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "1", "0=1⇒r∈ℕ × BOOL ↔ ℕ∧dom(r)=ℕ × BOOL∧ran(r)=ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.1",
				"∀x·x=0⇒r∈ℕ ↔ {x} × BOOL∧dom(r)=ℕ∧ran(r)={x} × BOOL");

		// f : S +-> T == f : S <-> T & !x,y,z. x |-> y : f & x |-> z : f => y = z
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ ℕ", "1",
				"0=1⇒f∈ℕ × BOOL ↔ ℕ∧(∀x,x0,x1,x2·x ↦ x0 ↦ x1∈f∧x ↦ x0 ↦ x2∈f⇒x1=x2)");
		assertReasonerSuccess("∀x·x = 0 ⇒ {x ↦ TRUE ↦ 1} ∈ {x}×BOOL ⇸ ℕ", "1.1",
				"∀x·x=0⇒{x ↦ TRUE ↦ 1}∈{x} × BOOL ↔ ℕ∧(∀x0,x1,x2,x3·x0 ↦ x1 ↦ x2∈{x ↦ TRUE ↦ 1}∧x0 ↦ x1 ↦ x3∈{x ↦ TRUE ↦ 1}⇒x2=x3)");
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ BOOL×ℕ", "1",
				"0=1⇒f∈ℕ × BOOL ↔ BOOL × ℕ∧(∀x,x0,x1,x2,x3,x4·x ↦ x0 ↦ (x1 ↦ x2)∈f∧x ↦ x0 ↦ (x3 ↦ x4)∈f⇒x1 ↦ x2=x3 ↦ x4)");
		assertReasonerSuccess("∀x·x = 0 ⇒ {x ↦ TRUE ↦ (FALSE ↦ 1)} ∈ {x}×BOOL ⇸ BOOL×ℕ", "1.1",
				"∀x·x=0⇒{x ↦ TRUE ↦ (FALSE ↦ 1)}∈{x} × BOOL ↔ BOOL × ℕ∧"
						+ "(∀x0,x1,x2,x3,x4,x5·x0 ↦ x1 ↦ (x2 ↦ x3)∈{x ↦ TRUE ↦ (FALSE ↦ 1)}∧"
						+ "x0 ↦ x1 ↦ (x4 ↦ x5)∈{x ↦ TRUE ↦ (FALSE ↦ 1)}⇒x2 ↦ x3=x4 ↦ x5)");

		// f : S --> T == f : S +-> T & dom(f) = S
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL → ℕ", "1", "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧dom(f)=ℕ × BOOL");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ → {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧dom(f)=ℕ");

		// f : S >+> T == f : S +-> T & f : T +-> S
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤔ ℕ", "1", "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧f∼∈ℕ ⇸ ℕ × BOOL");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ ⤔ {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧f∼∈{x} × BOOL ⇸ ℕ");

		// f : S >-> T == f : S >+> T & dom(f) = S
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ↣ ℕ", "1", "0=1⇒f∈ℕ × BOOL ⤔ ℕ∧dom(f)=ℕ × BOOL");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ ↣ {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ⤔ {x} × BOOL∧dom(f)=ℕ");

		// f : S +>> T == f : S +-> T & ran(f) = T
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤀ ℕ", "1", "0=1⇒f∈ℕ × BOOL ⇸ ℕ∧ran(f)=ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ ⤀ {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ⇸ {x} × BOOL∧ran(f)={x} × BOOL");

		// f : S ->> T == f : S +>> T & dom(f) = S
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ↠ ℕ", "1", "0=1⇒f∈ℕ × BOOL ⤀ ℕ∧dom(f)=ℕ × BOOL");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ ↠ {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ⤀ {x} × BOOL∧dom(f)=ℕ");

		// f : S >->> T == f : S >-> T & ran(f) = T
		assertReasonerSuccess("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤖ ℕ", "1", "0=1⇒f∈ℕ × BOOL ↣ ℕ∧ran(f)=ℕ");
		assertReasonerSuccess("∀x·x = 0 ⇒ f ∈ ℕ ⤖ {x}×BOOL", "1.1", "∀x·x=0⇒f∈ℕ ↣ {x} × BOOL∧ran(f)={x} × BOOL");

		// E |-> (F |-> G) : p >< q == E |-> F : p & E |-> G : q
		assertReasonerSuccess("(0 = x) ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "1", "0=x⇒x ↦ (1 ↦ 2)∈p∧x ↦ 3∈q");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "1.1", "∀x·x=0⇒x ↦ (1 ↦ 2)∈p∧x ↦ 3∈q");

		// E |-> G |-> (F |-> H) : p || q == E |-> F : p & G |-> H : q
		assertReasonerSuccess("(0 = x) ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "1", "0=x⇒x ↦ (1 ↦ 2)∈p∧2 ↦ x ↦ 3∈q");
		assertReasonerSuccess("∀x·x = 0 ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "1.1",
				"∀x·x=0⇒x ↦ (1 ↦ 2)∈p∧2 ↦ x ↦ 3∈q");

		// S : POW1(T) == S : POW(T) & S /= {}
		assertReasonerSuccess("(0 = x) ⇒ {x, 1} ∈ ℙ1(T)", "1", "0=x⇒{x,1}∈ℙ(T)∧{x,1}≠∅");
		assertReasonerSuccess("∀x·x = 0 ⇒ {x, 1} ∈ ℙ1(T)", "1.1", "∀x·x=0⇒{x,1}∈ℙ(T)∧{x,1}≠∅");

		// E : a .. b == a <= E & E <=b
		assertReasonerSuccess("0 = x ⇒ x ∈ 0‥1", "1", "0=x⇒0≤x∧x≤1");
	}

	@Test
	public void testUnsuccessful() {
		assertReasonerFailure("e ∈ {1} ◁ {1 ↦ 0}", "");
		assertReasonerFailure("e ∈ {1} ⩤ {1 ↦ 0}", "");
		assertReasonerFailure("e ∈ {1 ↦ 0} ▷ {0}", "");
		assertReasonerFailure("e ∈ {1 ↦ 0} ⩥ {0}", "");

		// E |-> F : S ** T == E : S & F : T
		assertReasonerFailure("(0 = 1) ⇒ (1 ↦ 2 ∈ ℕ × ℕ)", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ x ∈ ℕ × ℕ", "1.0");

		// E : POW(S) == E <: S
		assertReasonerFailure("(0 = 1) ⇒ {1} ∈ ℙ(ℕ)", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ {x} ∈ ℙ(ℕ)", "1.0");

		// E : S \/ ... \/ T == E : S or ... or E : T
		assertReasonerFailure("(0 = 1) ⇒ 1 ∈ {1} ∪ {2} ∪ {3}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {1} ∪ {2} ∪ {3}", "1.0");

		// E : S /\ ... /\ T == E : S & ... & E : T
		assertReasonerFailure("(0 = 1) ⇒ 1 ∈ {1} ∩ {2} ∩ {3}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {1} ∩ {2} ∩ {3}", "1.0");

		// E : S \ T == E : S & not(E : T)
		assertReasonerFailure("(0 = 1) ⇒ 1 ∈ {1} ∖ {2}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {x} ∖ {1}", "1.0");

		// E : {A, ..., B} == E = A or ... or E = B
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ {1, 2, 3}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {1, 2, 3}", "1.0");

		// B : {A, ..., B, ..., C} == true
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ {0, 1, 2}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {1, x, 3}", "1.0");

		// E : {F} == E = F (where F is a single expression)
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ {1}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ {1}", "1.0");

		// E : union(S) == #s.s : S & E : s
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ union({{1},{2}})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ union({{1},{2}})", "1.0");

		// E : inter(S) == !s.s : S => E :s
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ inter({{1},{2}})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ inter({{1},{2}})", "1.0");

		// E : (UNION x. P | T) == #x. P & E : T
		assertReasonerFailure("(0 = 1) ⇒ (0 ∈ (⋃ x · x ∈ ℕ ∣ {x+1}))", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ (⋃ y·y∈ℕ ∣ {x + y})", "1.0");

		// E : (INTER x. P | T) == !x. P => E : T
		assertReasonerFailure("(0 = 1) ⇒ (0 ∈ (⋂ x · x ∈ ℕ ∣ {x+1}))", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ (⋂ y·y∈ℕ ∣ {x + y})", "1.0");

		// E : dom(r) == #y. E |-> y : r
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ dom({0 ↦ 1})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ dom({x ↦ 1, x ↦ 2})", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ dom({0 ↦ (1↦BOOL↦0)})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ dom({x ↦ (1↦BOOL↦0), x ↦ (2↦BOOL↦0)})", "1.0");

		// F : ran(r) == #y. y |-> F : r
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ ran({0 ↦ 1})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ ran({x ↦ 1, 2 ↦ x})", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ 0 ∈ ran({1 ↦ BOOL ↦ 0 ↦ 1})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ ran({1 ↦ BOOL ↦ x ↦ 1, 2 ↦ BOOL ↦ 0 ↦ x})", "1.0");

		// E |-> F :r~ == F |-> E : r
		assertReasonerFailure("(0 = 1) ⇒ (0 ↦ 1 ∈ {1 ↦ 0}∼)", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ (x ↦ 1 ∈ {1 ↦ x, x ↦ 2}∼)", "1.0");

		// E |-> F : S <| r == E : S & E |-> F : r
		assertReasonerFailure("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ◁ {1 ↦ 0})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ◁ {1 ↦ x, x ↦ 2})", "1.0");

		// E |-> F : S <<| r == E /: S & E |-> F : r
		assertReasonerFailure("(0 = 1) ⇒ (1 ↦ 0 ∈ {1} ⩤ {1 ↦ 0})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ (1 ↦ x ∈ {1} ⩤ {1 ↦ x, x ↦ 2})", "1.0");

		// E |-> F : r |> T == E |-> F : r & F : T
		assertReasonerFailure("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ▷ {0})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ▷ {x})", "1.0");

		// E |-> F : r |>> T == E |-> F : r & F /: T
		assertReasonerFailure("(0 = 1) ⇒ (1 ↦ 0 ∈ {1 ↦ 0} ⩥ {0})", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ (1 ↦ x ∈ {1 ↦ x, x ↦ 2} ⩥ {x})", "1.0");

		// F : r[w] = #x.x : w & x |-> F : r
		assertReasonerFailure("(0 = 1) ⇒ 1 ∈ r[{0, 1}]", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ r[{0, x}]", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ 1 ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ 1 ∈ r[{0 ↦ 1, 1 ↦ 2}]", "1.0");

		// E |-> F : id == E = F
		assertReasonerFailure("(0 = 1) ⇒ x ↦ 1 ∈ id", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ y ∈ id", "1.0");

		// E |-> F : (p_1; p_2;...; p_n) ==
		// #x_1, x_2, ..., x_(n-1) . E |-> x_1 : p1 &
		//                            x_1 |-> x_2 : p2 &
		// ... &
		// x_(n-1) |-> F : pn &
		assertReasonerFailure("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ TRUE, 1 ↦ FALSE};{TRUE ↦ 1, FALSE ↦ 0}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0}",
				"1.0");
		assertReasonerFailure(
				"(0 = 1) ⇒ 0 ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}",
				"0");
		assertReasonerFailure(
				"∀x·x = 0 ⇒ x ↦ 1 ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ 0, 1 ↦ 1}",
				"1.0");
		assertReasonerFailure(
				"(0 = 1) ⇒ (0 ↦ (0 ↦ 1)) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};"
						+ "{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}",
				"0");
		assertReasonerFailure(
				"∀x·x = 0 ⇒ x ↦ (0 ↦ 1) ∈ {0 ↦ (TRUE ↦ 1), 1 ↦ (FALSE ↦ 1)};"
						+ "{TRUE ↦ 1 ↦ 1, FALSE ↦ 0 ↦ 0};{0 ↦ FALSE, 1 ↦ TRUE};{TRUE ↦ (0 ↦ 1)}",
				"1.0");
		assertReasonerFailure("∀x, x0 · x ↦ x0 ∈ t ⇒ x ↦ x0 ∈ ℕ × ℕ ∧ x ↦ x0 ∈ t∼;((ℕ × ℕ) ∖ t)", "2.0.0");

		// r : S <<-> T == r : S <-> T & dom(r) = S
		assertReasonerFailure("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ r ∈ {x}×BOOL  ℕ", "1.0");

		// r : S <->> T == r : S <-> T & ran(r) = T
		assertReasonerFailure("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.0");

		// r : S <<->> T == r : S <->> T & r : S <<-> T
		assertReasonerFailure("(0 = 1) ⇒ r ∈ ℕ×BOOL  ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ r ∈ ℕ  {x}×BOOL", "1.0");

		// f : S +-> T == f : S <-> T & !x,y,z. x |-> y : f & x |-> z : f => y = z
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ {x ↦ TRUE ↦ 1} ∈ {x}×BOOL ⇸ ℕ", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ⇸ BOOL×ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ {x ↦ TRUE ↦ (FALSE ↦ 1)} ∈ {x}×BOOL ⇸ BOOL×ℕ", "1.0");

		// f : S --> T == f : S +-> T & dom(f) = S
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL → ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ → {x}×BOOL", "1.0");

		// f : S >+> T == f : S +-> T & f : T +-> S
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤔ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ ⤔ {x}×BOOL", "1.0");

		// f : S >-> T == f : S >+> T & dom(f) = S
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ↣ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ ↣ {x}×BOOL", "1.0");

		// f : S +>> T == f : S +-> T & ran(f) = T
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤀ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ ⤀ {x}×BOOL", "1.0");

		// f : S ->> T == f : S +>> T & dom(f) = S
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ↠ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ ↠ {x}×BOOL", "1.0");

		// f : S >->> T == f : S >-> T & ran(f) = T
		assertReasonerFailure("(0 = 1) ⇒ f ∈ ℕ×BOOL ⤖ ℕ", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ f ∈ ℕ ⤖ {x}×BOOL", "1.0");

		// E |-> (F |-> G) : p >< q == E |-> F : p & E |-> G : q
		assertReasonerFailure("(0 = x) ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ (1 ↦ 2 ↦ 3) ∈ p ⊗ q", "1.0");

		// E |-> G |-> (F |-> H) : p || q == E |-> F : p & G |-> H : q
		assertReasonerFailure("(0 = x) ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ x ↦ (2 ↦ x) ↦ (1 ↦ 2 ↦ 3) ∈ p ∥ q", "1.0");

		// S : POW1(T) == S : POW(T) & S /= {}
		assertReasonerFailure("(0 = x) ⇒ {x, 1} ∈ ℙ1(T)", "0");
		assertReasonerFailure("∀x·x = 0 ⇒ {x, 1} ∈ ℙ1(T)", "1.0");
	}

	// Commented out, makes the tests NOT succeed
	// TODO: Verify with another external prover
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
