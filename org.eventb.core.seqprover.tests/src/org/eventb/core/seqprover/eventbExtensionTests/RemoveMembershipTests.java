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

import static org.eventb.core.seqprover.eventbExtensions.Tactics.rmGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership.RMLevel;
import org.junit.Test;

/**
 * Unit tests for the rm reasoner {@link RemoveMembership}
 * 
 * @author htson
 */
public abstract class RemoveMembershipTests extends AbstractManualRewriterTests {

	private final RMLevel level;

	public RemoveMembershipTests(RemoveMembership rewriter) {
		super(rewriter);
		this.level = rewriter.getLevel();
	}

	@Override
	protected final List<IPosition> getPositions(Predicate predicate) {
		return rmGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		switch (level) {
		case L0:
			return "org.eventb.core.seqprover.rm";
		case L1:
			return "org.eventb.core.seqprover.rmL1";
		default:
			return null;
		}
	}

	private void rewriteRoot(String inputImage, String expectedImage) {
		rewritePred(inputImage, "", expectedImage);
	}

	private void noRewriteRoot(String inputImage) {
		noRewritePred(inputImage, "");
	}

	@Test
	public void testPositions() {
		// General machinery
		assertGetPositions("x ∈ {0}", "ROOT");
		assertGetPositions("0 = 1 ⇒ x ∈ {0}", "1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {0}", "1.1");
		assertGetPositions("∀x·x = 0 ⇒ x ∈ {0} ∪ {x ∣ x ∈ {0}}", "1.1", "1.1.1.1.1");

		// Predicates where this reasoner is not applicable
		assertGetPositions("x = {0}");
		assertGetPositions("e ∈ {1} ◁ {1 ↦ 0}");
		assertGetPositions("e ∈ {1} ⩤ {1 ↦ 0}");
		assertGetPositions("e ∈ {1 ↦ 0} ▷ {0}");
		assertGetPositions("e ∈ {1 ↦ 0} ⩥ {0}");

		// Ensures that level 1 positions are computed
		assertGetPositions("1 ∈ ℕ", "ROOT");
	}

	// E |-> F : S ** T == E : S & F : T
	@Test
	public void testDEF_IN_MAPSTO() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ ℕ × ℕ", "1 ∈ ℕ ∧ 2 ∈ ℕ");
		noRewriteRoot("1 ↦ 2 ∈ A");
		noRewriteRoot("a ∈ ℕ × ℕ");
	}

	// E : POW(S) == E <: S
	@Test
	public void testDEF_IN_POW() throws Exception {
		rewriteRoot("{1} ∈ ℙ(ℕ)", "{1} ⊆ ℕ");
		rewriteRoot("{x} ∈ ℙ(ℕ)", "{x} ⊆ ℕ");
		rewriteRoot("A ∪ B ∈ ℙ(ℕ)", "A ∪ B ⊆ ℕ");
	}

	// E : S \/ ... \/ T == E : S or ... or E : T
	@Test
	public void testDEF_IN_BUNION() throws Exception {
		rewriteRoot("x ∈ {1} ∪ {2}", "x ∈ {1} ∨ x ∈ {2}");
		rewriteRoot("x ∈ {1} ∪ {2} ∪ {3}", "x ∈ {1} ∨ x ∈ {2} ∨ x ∈ {3}");
		rewriteRoot("1 ∈ A ∪ B", "1 ∈ A ∨ 1 ∈ B");
	}

	// E : S /\ ... /\ T == E : S & ... & E : T
	@Test
	public void testDEF_IN_BINTER() throws Exception {
		rewriteRoot("x ∈ {1} ∩ {2}", "x ∈ {1} ∧ x ∈ {2}");
		rewriteRoot("x ∈ {1} ∩ {2} ∩ {3}", "x ∈ {1} ∧ x ∈ {2} ∧ x ∈ {3}");
		rewriteRoot("1 ∈ A ∩ B", "1 ∈ A ∧ 1 ∈ B");
	}

	// E : S \ T == E : S & not(E : T)
	@Test
	public void testDEF_IN_SETMINUS() throws Exception {
		rewriteRoot("x ∈ {1} ∖ {2}", "x ∈ {1} ∧ ¬ x ∈ {2}");
		rewriteRoot("x ∈ {1} ∖ ({2} ∖ {3})", "x ∈ {1} ∧ ¬ x ∈ {2} ∖ {3}");
	}

	// E : {A, ..., B} == E = A or ... or E = B
	@Test
	public void testDEF_IN_SETENUM() throws Exception {
		rewriteRoot("x ∈ {1, 2}", "x = 1 ∨ x = 2");
		rewriteRoot("x ∈ {1, 2, 3}", "x = 1 ∨ x = 2 ∨ x = 3");
	}

	// B : {A, ..., B, ..., C} == true
	@Test
	public void testSIMP_MULTI_IN() throws Exception {
		rewriteRoot("1 ∈ {1}", "⊤");
		rewriteRoot("1 ∈ {1, 2}", "⊤");
		rewriteRoot("2 ∈ {1, 2}", "⊤");
		rewriteRoot("2 ∈ {1, 2, 3}", "⊤");
	}

	// E : {F} == E = F (where F is a single expression)
	@Test
	public void testSIMP_IN_SING() throws Exception {
		rewriteRoot("1 ∈ {1}", "⊤");
		rewriteRoot("A ∪ ℕ ∈ {A ∪ ℕ}", "⊤");
		rewriteRoot("x ∈ {1}", "x=1");
	}

	// E : union(S) == #s.s : S & E : s
	@Test
	public void testDEF_IN_KUNION() throws Exception {
		rewriteRoot("1 ∈ union(A)", "(∃s· s ∈ A ∧ 1 ∈ s)");
		rewriteRoot("1 ∈ union(s)", "(∃t· t ∈ s ∧ 1 ∈ t)");
	}

	// E : inter(S) == !s.s : S => E :s
	@Test
	public void testDEF_IN_KINTER() throws Exception {
		rewriteRoot("1 ∈ inter(A)", "(∀s· s ∈ A ⇒ 1 ∈ s)");
		rewriteRoot("1 ∈ inter(s)", "(∀t· t ∈ s ⇒ 1 ∈ t)");
	}

	// E : (UNION x. P | T) == #x. P & E : T
	@Test
	public void testDEF_IN_QUNION() throws Exception {
		rewriteRoot("y ∈ (⋃x· x ∈ ℕ ∣ {x+1})", "(∃x· x ∈ ℕ ∧ y ∈ {x+1})");
		rewriteRoot("y ∈ (⋃y· y ∈ ℕ ∣ {y+1})", "(∃x· x ∈ ℕ ∧ y ∈ {x+1})");
		rewriteRoot("z ∈ (⋃x,y· x ∈ ℕ ∣ {x+y})", "(∃x,y· x ∈ ℕ ∧ z ∈ {x+y})");
	}

	// E : (INTER x. P | T) == !x. P => E : T
	@Test
	public void testDEF_IN_QINTER() throws Exception {
		rewriteRoot("y ∈ (⋂x· x ∈ ℕ ∣ {x+1})", "(∀x· x ∈ ℕ ⇒ y ∈ {x+1})");
		rewriteRoot("y ∈ (⋂y· y ∈ ℕ ∣ {y+1})", "(∀x· x ∈ ℕ ⇒ y ∈ {x+1})");
		rewriteRoot("z ∈ (⋂x,y· x ∈ ℕ ∣ {x+y})", "(∀x,y· x ∈ ℕ ⇒ z ∈ {x+y})");
	}

	// E : dom(r) == #y. E |-> y : r
	@Test
	public void testDEF_IN_DOM() throws Exception {
		rewriteRoot("0 ∈ dom(R ∪ succ)", "∃x· 0 ↦ x ∈ R ∪ succ");
		rewriteRoot("x ∈ dom(R ∪ succ)", "∃y· x ↦ y ∈ R ∪ succ");
		rewriteRoot("x ∈ dom({x ↦ 1, 1 ↦ 2})", "∃y· x ↦ y ∈ {x ↦ 1, 1 ↦ 2}");
		rewriteRoot("x ∈ dom({1 ↦ {2 ↦ 3}})", "∃y· x ↦ y ∈ {1 ↦ {2 ↦ 3}}");

		// LHS may be a pair
		rewriteRoot("pair ∈ dom({1 ↦ 2 ↦ 3})", "∃y· pair ↦ y ∈ {1 ↦ 2 ↦ 3}");

		// We create as many variables as components in the range.
		rewriteRoot("x ∈ dom({1 ↦ (2 ↦ 3)})", //
				"∃y, z· x ↦ (y ↦ z) ∈ {1 ↦ (2 ↦ 3)}");
		rewriteRoot("x ∈ dom({1 ↦ (2 ↦ 3 ↦ 4)})", //
				"∃y, z, t· x ↦ (y ↦ z ↦ t) ∈ {1 ↦ (2 ↦ 3 ↦ 4)}");
		rewriteRoot("x ∈ dom({1 ↦ (2 ↦ (3 ↦ 4))})", //
				"∃y, z, t· x ↦ (y ↦ (z ↦ t)) ∈ {1 ↦ (2 ↦ (3 ↦ 4))}");
	}

	// F : ran(r) == #y. y |-> F : r
	@Test
	public void testDEF_IN_RAN() throws Exception {
		rewriteRoot("0 ∈ ran(R ∪ succ)", "∃x· x ↦ 0 ∈ R ∪ succ");
		rewriteRoot("y ∈ ran(R ∪ succ)", "∃x· x ↦ y ∈ R ∪ succ");
		rewriteRoot("y ∈ ran({1 ↦ y, 1 ↦ 2})", "∃x· x ↦ y ∈ {1 ↦ y, 1 ↦ 2}");
		rewriteRoot("y ∈ ran({{1 ↦ 2} ↦ 3})", "∃x· x ↦ y ∈ {{1 ↦ 2} ↦ 3}");

		// LHS may be a pair
		rewriteRoot("pair ∈ ran({1 ↦ (2 ↦ 3)})", //
				"∃x· x ↦ pair ∈ {1 ↦ (2 ↦ 3)}");

		// We create as many variables as components in the domain.
		rewriteRoot("x ∈ ran({1 ↦ 2 ↦ 3})", //
				"∃y, z· y ↦ z ↦ x ∈ {1 ↦ 2 ↦ 3}");
		rewriteRoot("x ∈ ran({1 ↦ 2 ↦ 3 ↦ 4})", //
				"∃y, z, t· y ↦ z ↦ t ↦ x ∈ {1 ↦ 2 ↦ 3 ↦ 4}");
		rewriteRoot("x ∈ ran({1 ↦ (2 ↦ 3) ↦ 4})", //
				"∃y, z, t· y ↦ (z ↦ t) ↦ x ∈ {1 ↦ (2 ↦ 3) ↦ 4}");
	}

	// E |-> F :r~ == F |-> E : r
	@Test
	public void testDEF_IN_CONVERSE() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ R∼", "2 ↦ 1 ∈ R");
		noRewriteRoot("a ∈ succ∼");
	}

	// E |-> F : S <| r == E : S & E |-> F : r
	@Test
	public void testDEF_IN_DOMRES() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ A ◁ R", "1 ∈ A ∧ 1 ↦ 2 ∈ R");
		noRewriteRoot("a ∈ A ◁ {1 ↦ 2}");
	}

	// E |-> F : S <<| r == E /: S & E |-> F : r
	@Test
	public void testDEF_IN_DOMSUB() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ A ⩤ R", "1 ∉ A ∧ 1 ↦ 2 ∈ R");
		noRewriteRoot("a ∈ A ◁ {1 ↦ 2}");
	}

	// E |-> F : r |> T == E |-> F : r & F : T
	@Test
	public void testDEF_IN_RANRES() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ R ▷ A", "1 ↦ 2 ∈ R ∧ 2 ∈ A");
		noRewriteRoot("a ∈ {1 ↦ 2} ▷ A");
	}

	// E |-> F : r |>> T == E |-> F : r & F /: T
	@Test
	public void testDEF_IN_RANSUB() throws Exception {
		rewriteRoot("1 ↦ 2 ∈ R ⩥ A", "1 ↦ 2 ∈ R ∧ 2 ∉ A");
		noRewriteRoot("a ∈ {1 ↦ 2} ⩥ A");
	}

	// F : r[w] = #x.x : w & x |-> F : r
	@Test
	public void testDEF_IN_RELIMAGE() throws Exception {
		rewriteRoot("1 ∈ R[{0}]", "∃x· x ∈ {0} ∧ x ↦ 1 ∈ R");
		rewriteRoot("x+1 ∈ R[{0}]", "∃y· y ∈ {0} ∧ y ↦ x+1 ∈ R");

		// LHS may be a pair
		rewriteRoot("pair ∈ {1 ↦ (2 ↦ 3)}[A]", //
				"∃x· x ∈ A ∧ x ↦ pair ∈ {1 ↦ (2 ↦ 3)}");

		// We create as many variables as components in the domain.
		rewriteRoot("x ∈ {1 ↦ 2 ↦ 3}[A]", //
				"∃y, z· y ↦ z ∈ A ∧ y ↦ z ↦ x ∈ {1 ↦ 2 ↦ 3}");
		rewriteRoot("x ∈ {1 ↦ 2 ↦ 3 ↦ 4}[A]", //
				"∃y, z, t· y ↦ z ↦ t ∈ A ∧ y ↦ z ↦ t ↦ x ∈ {1 ↦ 2 ↦ 3 ↦ 4}");
		rewriteRoot("x ∈ {1 ↦ (2 ↦ 3) ↦ 4}[A]", //
				"∃y, z, t· y ↦ (z ↦ t) ∈ A ∧ y ↦ (z ↦ t) ↦ x ∈ {1 ↦ (2 ↦ 3) ↦ 4}");
	}

	// E |-> F : id == E = F
	@Test
	public void testDEF_IN_ID() throws Exception {
		rewriteRoot("1 ↦ x ∈ id", "1 = x");
		noRewriteRoot("{1 ↦ (2 ↦ 3)}(1) ∈ id");
	}

	// E |-> F : (p_1; p_2;...; p_n) ==
	// #x_1, x_2, ..., x_(n-1) . E |-> x_1 : p1 &
	// x_1 |-> x_2 : p2 &
	// ... &
	// x_(n-1) |-> F : pn &
	@Test
	public void testDEF_IN_FCOMP() throws Exception {
		rewriteRoot("x ↦ y ∈ id ; {0 ↦ 1}", //
				"∃a· x ↦ a ∈ id ∧ a ↦ y ∈ {0 ↦ 1}");
		rewriteRoot("x ↦ y ∈ id ; {0 ↦ 1} ; succ", //
				"∃a,b· x ↦ a ∈ id ∧ a ↦ b ∈ {0 ↦ 1} ∧ b ↦ y ∈ succ");

		// We create as many variables as components in the intermediate sets.
		rewriteRoot("x ↦ y ∈ {1 ↦ (2 ↦ 3)} ; {0 ↦ 1 ↦ (2 ↦ 3)}", //
				"∃a,b· x ↦ (a ↦ b) ∈ {1 ↦ (2 ↦ 3)} " + //
						"∧ a ↦ b ↦ y ∈ {0 ↦ 1 ↦ (2 ↦ 3)}");
		rewriteRoot("x ↦ y ∈ {1 ↦ (2 ↦ 3)} ; {0 ↦ 1 ↦ (2 ↦ 3)} ; {1 ↦ 2 ↦ 3}", //
				"∃a,b,c,d· x ↦ (a ↦ b) ∈ {1 ↦ (2 ↦ 3)} " + //
						"∧ a ↦ b ↦ (c ↦ d) ∈ {0 ↦ 1 ↦ (2 ↦ 3)} " + //
						"∧ c ↦ d ↦ y ∈ {1 ↦ 2 ↦ 3}");

		// LHS must be an explicit pair
		noRewriteRoot("pair ∈ id ; {0 ↦ 1}");
	}

	// r : S <<-> T == r : S <-> T & dom(r) = S
	@Test
	public void testDEF_IN_RELDOM() throws Exception {
		rewriteRoot("R ∈ {0}  ℕ", "R ∈ {0} ↔ ℕ ∧ dom(R) = {0}");
	}

	// r : S <->> T == r : S <-> T & ran(r) = T
	@Test
	public void testDEF_IN_RELRAN() throws Exception {
		rewriteRoot("R ∈ {0}  ℕ", "R ∈ {0} ↔ ℕ ∧ ran(R) = ℕ");
	}

	@Test
	public void testSuccessful() throws Exception {
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
