package org.eventb.core.seqprover.rewriterTests;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;

public class AutoFormulaRewriterTests extends TestCase {

	private IFormulaRewriter r;

	private Predicate P;

	private Predicate Q;

	private Predicate R;

	private FormulaFactory ff;

	@Override
	protected void setUp() throws Exception {
		r = new AutoRewriterImpl();
		P = TestLib.genPredicate("1 = 2");
		Q = TestLib.genPredicate("2 = 3");
		R = TestLib.genPredicate("3 = 4");
		ff = FormulaFactory.getDefault();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	private void assertAssociativePredicate(String message, Predicate expected,
			int tag, Predicate... predicates) {
		AssociativePredicate predicate = ff.makeAssociativePredicate(tag,
				predicates, null);
		assertEquals(message, expected, r.rewrite(predicate));
	}

	public void testConjunction() {
		Predicate[] predicates = new Predicate[] { P, Q, R };
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, predicates, null);

		// P & ... & true & ... & Q == P & ... & Q
		assertAssociativePredicate("P ∧ ⊤ == P", P, Predicate.LAND, P, Lib.True);
		assertAssociativePredicate("⊤ ∧ P == P", P, Predicate.LAND, Lib.True, P);
		assertAssociativePredicate("P ∧ Q ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ ⊤ ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, Lib.True, R);
		assertAssociativePredicate("⊤ ∧ P ∧ Q ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, Lib.True);
		assertAssociativePredicate("P ∧ Q ∧ ⊤ ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, Lib.True, R, Lib.True);
		assertAssociativePredicate("⊤ ∧ P ∧ Q ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, R, Lib.True);
		assertAssociativePredicate("⊤ ∧ P ∧ Q ∧ ⊤ ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, Lib.True, R);
		assertAssociativePredicate("⊤ ∧ P ∧ Q ∧ ⊤ ∧ R ∧ ⊤ == P ∧ Q ∧ R",
				expected, Predicate.LAND, Lib.True, P, Q, Lib.True, R, Lib.True);

		// P & ... & false & ... & Q == false
		assertAssociativePredicate("P ∧ ⊥ = ⊥", Lib.False, Predicate.LAND, P,
				Lib.False);
		assertAssociativePredicate("⊥ ∧ P = ⊥", Lib.False, Predicate.LAND,
				Lib.False, P);
		assertAssociativePredicate("P ∧ Q ∧ ⊥ ∧ R = ⊥", Lib.False,
				Predicate.LAND, P, Q, Lib.False, R);
		assertAssociativePredicate("⊥ ∧ P ∧ Q ∧ R = ⊥", Lib.False,
				Predicate.LAND, Lib.False, P, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ ⊥ = ⊥", Lib.False,
				Predicate.LAND, P, Q, R, Lib.False);
		assertAssociativePredicate("P ∧ Q ∧ ⊥ ∧ R ∧ ⊥ = ⊥", Lib.False,
				Predicate.LAND, P, Q, Lib.False, R, Lib.False);
		assertAssociativePredicate("⊥ ∧ P ∧ Q ∧ R ∧ ⊥ = ⊥", Lib.False,
				Predicate.LAND, Lib.False, P, Q, R, Lib.False);
		assertAssociativePredicate("⊥ ∧ P ∧ Q ∧ ⊥ ∧ R = ⊥", Lib.False,
				Predicate.LAND, Lib.False, P, Q, Lib.False, R);
		assertAssociativePredicate("⊥ ∧ P ∧ Q ∧ ⊥ ∧ R ∧ ⊥ = ⊥", Lib.False,
				Predicate.LAND, Lib.False, P, Q, Lib.False, R, Lib.False);
	}

	public void testDisjunction() {
		// P or ... or true or ... or Q == true
		Predicate[] predicates = new Predicate[] { P, Q, R };
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LOR, predicates, null);
		assertAssociativePredicate("P ⋁ ⊤ = ⊤", Lib.True, Predicate.LOR, P,
				Lib.True);
		assertAssociativePredicate("⊤ ⋁ P = ⊤", Lib.True, Predicate.LOR,
				Lib.True, P);
		assertAssociativePredicate("P ⋁ Q ⋁ R == P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ ⊤ ⋁ R == ⊤", Lib.True,
				Predicate.LOR, P, Q, Lib.True, R);
		assertAssociativePredicate("⊤ ⋁ P ⋁ Q ⋁ R == ⊤", Lib.True,
				Predicate.LOR, Lib.True, P, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ ⊤ == ⊤", Lib.True,
				Predicate.LOR, P, Q, R, Lib.True);
		assertAssociativePredicate("P ⋁ Q ⋁ ⊤ ⋁ R ⋁ ⊤ == ⊤", Lib.True,
				Predicate.LOR, P, Q, Lib.True, R, Lib.True);
		assertAssociativePredicate("⊤ ⋁ P ⋁ Q ⋁ R ⋁ ⊤ == ⊤", Lib.True,
				Predicate.LOR, Lib.True, P, Q, R, Lib.True);
		assertAssociativePredicate("⊤ ⋁ P ⋁ Q ⋁ ⊤ ⋁ R == ⊤", Lib.True,
				Predicate.LOR, Lib.True, P, Q, Lib.True, R);
		assertAssociativePredicate("⊤ ⋁ P ⋁ Q ⋁ ⊤ ⋁ R ⋁ ⊤ == ⊤", Lib.True,
				Predicate.LOR, Lib.True, P, Q, Lib.True, R, Lib.True);

		// P or ... or true or ... or Q == P or ... or Q
		assertAssociativePredicate("P ⋁ ⊥ = P", P, Predicate.LOR, P, Lib.False);
		assertAssociativePredicate("⊥ ⋁ P = P", P, Predicate.LOR, Lib.False, P);
		assertAssociativePredicate("P ⋁ Q ⋁ ⊥ ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, Lib.False, R);
		assertAssociativePredicate("⊥ ⋁ P ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R, Lib.False);
		assertAssociativePredicate("P ⋁ Q ⋁ ⊥ ∧ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, Lib.False, R, Lib.False);
		assertAssociativePredicate("⊥ P ⋁ Q ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, R, Lib.False);
		assertAssociativePredicate("⊥ P ⋁ Q ⋁ ⊥ ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, Lib.False, R);
		assertAssociativePredicate("⊥ P ⋁ Q ⋁ ⊥ ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, Lib.False, R, Lib.False);
	}

	private void assertBinaryPredicate(String message, Predicate expected,
			Predicate left, int tag, Predicate right) {
		assertEquals(message, expected, r.rewrite(ff.makeBinaryPredicate(tag,
				left, right, null)));
	}

	public void testImplication() {
		// true => P == P
		assertBinaryPredicate("⊤ ⇒ P == P", P, Lib.True, Predicate.LIMP, P);

		// false => P == true
		assertBinaryPredicate("⊥ ⇒ P == ⊤", Lib.True, Lib.False,
				Predicate.LIMP, P);

		// P => true == true
		assertBinaryPredicate("P ⇒ ⊤ == ⊤", Lib.True, P, Predicate.LIMP,
				Lib.True);

		// P => false == not(P)
		assertBinaryPredicate("P ⇒ ⊥ == ¬P", Lib.makeNeg(P), P, Predicate.LIMP,
				Lib.False);

		// P => P == true
		assertBinaryPredicate("P ⇒ P == ⊤", Lib.True, P, Predicate.LIMP, P);
	}

	public void testEquivalent() {
		// P <=> true == P
		assertBinaryPredicate("P ⇔ ⊤ == P", P, P, Predicate.LEQV, Lib.True);

		// true <=> P == P
		assertBinaryPredicate("⊤ ⇔ P == P", P, Lib.True, Predicate.LEQV, P);

		// P <=> false == not(P)
		assertBinaryPredicate("P ⇔ ⊥ = ¬P", Lib.makeNeg(P), P, Predicate.LEQV,
				Lib.False);

		// false <=> P == not(P)
		assertBinaryPredicate("⊥ ⇔ P = ¬P", Lib.makeNeg(P), Lib.False,
				Predicate.LEQV, P);

		// P <=> P == true
		assertBinaryPredicate("P ⇔ P = ⊤", Lib.True, P, Predicate.LEQV, P);
	}

	private void assertUnaryPredicate(String message, Predicate expected,
			int tag, Predicate predicate) {
		assertEquals(message, expected, r.rewrite(ff.makeUnaryPredicate(tag,
				predicate, null)));
	}

	public void testNegation() {
		// not(true) == false
		assertUnaryPredicate("¬⊤ == ⊥", Lib.False, Predicate.NOT, Lib.True);

		// not(false) == true
		assertUnaryPredicate("¬⊥ == ⊤", Lib.False, Predicate.NOT, Lib.True);

		// not(not(P)) == not(P)
		assertUnaryPredicate("¬¬P = P", P, Predicate.NOT, Lib.makeNeg((P)));
	}

	private void assertQuantificationPredicate(String message,
			Predicate expected, int tag, BoundIdentDecl[] boundIdentifiers,
			Predicate predicate) {
		assertEquals(message, expected, r.rewrite(ff.makeQuantifiedPredicate(
				tag, boundIdentifiers, predicate, null)));
	}

	public void testQuantification() {
		// !x.(P & Q) == (!x.P) & (!x.Q)
		BoundIdentDecl x = ff.makeBoundIdentDecl("x", null);
		Predicate pred1 = ff.makeQuantifiedPredicate(Predicate.FORALL,
				new BoundIdentDecl[] { x }, P, null);
		Predicate pred2 = ff.makeQuantifiedPredicate(Predicate.FORALL,
				new BoundIdentDecl[] { x }, Q, null);
		Predicate pred3 = ff.makeQuantifiedPredicate(Predicate.FORALL,
				new BoundIdentDecl[] { x }, R, null);

		Predicate expected = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { pred1 }, null);
		assertQuantificationPredicate("∀x·P == ∀x·P", expected,
				Predicate.FORALL, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LAND,
								new Predicate[] { P }, null));

		expected = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2 }, null);
		assertQuantificationPredicate("∀x·P ∧ Q == (∀x·P) ∧ (∀x·Q)", expected,
				Predicate.FORALL, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LAND,
								new Predicate[] { P, Q }, null));

		expected = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2, pred3 }, null);
		assertQuantificationPredicate(
				"∀x·P ∧ Q ∧ R == (∀x·P) ∧ (∀x·Q) ∧ (∀x·R)", expected,
				Predicate.FORALL, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LAND,
								new Predicate[] { P, Q, R }, null));

		// #x.(P or Q) == (#x.P) or (#x.Q)
		pred1 = ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { x }, P, null);
		pred2 = ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { x }, Q, null);
		pred3 = ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { x }, R, null);
		expected = ff.makeAssociativePredicate(Predicate.LOR,
				new Predicate[] { pred1 }, null);
		assertQuantificationPredicate("∃x·P == ∃x·P", expected,
				Predicate.EXISTS, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LOR,
								new Predicate[] { P }, null));

		expected = ff.makeAssociativePredicate(Predicate.LOR, new Predicate[] {
				pred1, pred2 }, null);
		assertQuantificationPredicate("∃x·P ⋁ Q == (∃x·P) ⋁ (∃x·Q)", expected,
				Predicate.EXISTS, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LOR,
								new Predicate[] { P, Q }, null));

		expected = ff.makeAssociativePredicate(Predicate.LOR, new Predicate[] {
				pred1, pred2, pred3 }, null);
		assertQuantificationPredicate(
				"∃x·P ⋁ Q ⋁ R == (∃x·P) ⋁ (∃x·Q) ⋁ (∃x·R)", expected,
				Predicate.EXISTS, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LOR,
								new Predicate[] { P, Q, R }, null));

	}

	private void assertRelationalPredicate(String message, Predicate expected,
			Expression left, int tag, Expression right) {
		assertEquals(message, expected, r.rewrite(ff.makeRelationalPredicate(
				tag, left, right, null)));
	}

	public void testEquality() {
		// E = E == true
		Expression E = ff.makeAtomicExpression(Formula.TRUE, null);
		Expression F = ff.makeAtomicExpression(Formula.FALSE, null);
		Expression G = ff.makeAtomicExpression(Formula.FALSE, null);
		Expression H = ff.makeAtomicExpression(Formula.TRUE, null);
		assertRelationalPredicate("E = E == ⊤", Lib.True, E, Predicate.EQUAL, E);

		// E /= E == false
		assertRelationalPredicate("E ≠ E == ⊥", Lib.False, E,
				Predicate.NOTEQUAL, E);

		// E |-> F = G |-> H == E = G & F = H
		Predicate pred1 = ff.makeRelationalPredicate(Formula.EQUAL, E, G, null);
		Predicate pred2 = ff.makeRelationalPredicate(Formula.EQUAL, F, H, null);
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, new Predicate[] { pred1, pred2 }, null);
		BinaryExpression left = ff.makeBinaryExpression(Formula.MAPSTO, E, F,
				null);
		BinaryExpression right = ff.makeBinaryExpression(Formula.MAPSTO, G, H,
				null);
		assertRelationalPredicate("E ↦ F = G ↦ H == E = G ∧ F = H", expected,
				left, Predicate.EQUAL, right);
	}

	private void assertAssociativeExpression(String message,
			Expression expected, int tag, Expression... expressions) {
		AssociativeExpression expression = ff.makeAssociativeExpression(tag,
				expressions, null);
		assertEquals(message, expected, r.rewrite(expression));
	}

	public void testSetTheory() {
		Expression empty = ff.makeAtomicExpression(Formula.EMPTYSET, null);
		Expression fTrue = ff.makeAtomicExpression(Formula.TRUE, null);
		Expression fFalse = ff.makeAtomicExpression(Formula.FALSE, null);
		Expression S = ff.makeSetExtension(fTrue, null);
		Expression T = ff.makeSetExtension(fFalse, null);
		Expression R = ff.makeSetExtension(new Expression[] { fTrue, fFalse },
				null);
		// S /\ ... /\ {} /\ ... /\ T == {}
		Expression expected = ff.makeAssociativeExpression(Formula.BINTER,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∩ ∅ == ∅", empty, Formula.BINTER, S,
				empty);
		assertAssociativeExpression("∅ ∩ S == ∅", empty, Formula.BINTER, empty,
				S);
		assertAssociativeExpression("S ∩ T ∩ R == S ∩ T ∩ R", expected,
				Formula.BINTER, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R == ∅", empty,
				Formula.BINTER, S, T, empty, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R == ∅", empty,
				Formula.BINTER, empty, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ ∅ == ∅", empty,
				Formula.BINTER, S, T, R, empty);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", empty,
				Formula.BINTER, S, T, empty, R, empty);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R ∩ ∅ == ∅", empty,
				Formula.BINTER, empty, S, T, R, empty);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R == ∅", empty,
				Formula.BINTER, empty, S, T, empty, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", empty,
				Formula.BINTER, empty, S, T, empty, R, empty);

		// S /\ ... /\ {} /\ ... /\ T == {}
		expected = ff.makeAssociativeExpression(Formula.BUNION,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∪ ∅ == S", S, Formula.BUNION, S,
				empty);
		assertAssociativeExpression("∅ ∪ S == S", S, Formula.BUNION, empty,
				S);
		assertAssociativeExpression("S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, empty, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, empty, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, R, empty);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, empty, R, empty);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, empty, S, T, R, empty);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, empty, S, T, empty, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, empty, S, T, empty, R, empty);
	}

}
