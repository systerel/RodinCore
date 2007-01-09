package org.eventb.core.seqprover.rewriterTests;

import junit.framework.TestCase;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
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

	private void assertAssociative(String message, Predicate expected, int tag,
			Predicate... predicates) {
		AssociativePredicate predicate = ff.makeAssociativePredicate(tag,
				predicates, null);
		assertEquals(message, expected, r.rewrite(predicate));
	}

	public void testConjunction() {
		// P & ... & true & ... & Q == P & ... & Q
		Predicate[] predicates = new Predicate[] { P, Q, R };
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, predicates, null);
		assertAssociative("⊤ == ⊤", Lib.True, Predicate.LAND, Lib.True);
		assertAssociative("P ∧ ⊤ == P", P, Predicate.LAND, P, Lib.True);
		assertAssociative("⊤ ∧ P == P", P, Predicate.LAND, Lib.True, P);
		assertAssociative("P ∧ Q ∧ R == P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, R);
		assertAssociative("P ∧ Q ∧ ⊤ ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, Lib.True, R);
		assertAssociative("⊤ ∧ P ∧ Q ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, R);
		assertAssociative("P ∧ Q ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, Lib.True);
		assertAssociative("P ∧ Q ∧ ⊤ ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, Lib.True, R, Lib.True);
		assertAssociative("⊤ ∧ P ∧ Q ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, R, Lib.True);
		assertAssociative("⊤ ∧ P ∧ Q ∧ ⊤ ∧ R == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, Lib.True, R);
		assertAssociative("⊤ ∧ P ∧ Q ∧ ⊤ ∧ R ∧ ⊤ == P ∧ Q ∧ R", expected,
				Predicate.LAND, Lib.True, P, Q, Lib.True, R, Lib.True);

		// P & ... & false & ... & Q == false
		assertAssociative("⊥ = ⊥", Lib.False, Predicate.LAND, Lib.False);
		assertAssociative("P ∧ ⊥ = ⊥", Lib.False, Predicate.LAND, P, Lib.False);
		assertAssociative("⊥ ∧ P = ⊥", Lib.False, Predicate.LAND, Lib.False, P);
		assertAssociative("P ∧ Q ∧ ⊥ ∧ R = ⊥", Lib.False, Predicate.LAND, P, Q,
				Lib.False, R);
		assertAssociative("⊥ ∧ P ∧ Q ∧ R = ⊥", Lib.False, Predicate.LAND,
				Lib.False, P, Q, R);
		assertAssociative("P ∧ Q ∧ R ∧ ⊥ = ⊥", Lib.False, Predicate.LAND, P, Q,
				R, Lib.False);
		assertAssociative("P ∧ Q ∧ ⊥ ∧ R ∧ ⊥ = ⊥", Lib.False, Predicate.LAND,
				P, Q, Lib.False, R, Lib.False);
		assertAssociative("⊥ ∧ P ∧ Q ∧ R ∧ ⊥ = ⊥", Lib.False, Predicate.LAND,
				Lib.False, P, Q, R, Lib.False);
		assertAssociative("⊥ ∧ P ∧ Q ∧ ⊥ ∧ R = ⊥", Lib.False, Predicate.LAND,
				Lib.False, P, Q, Lib.False, R);
		assertAssociative("⊥ ∧ P ∧ Q ∧ ⊥ ∧ R ∧ ⊥ = ⊥", Lib.False,
				Predicate.LAND, Lib.False, P, Q, Lib.False, R, Lib.False);
	}

	public void testDisjunction() {
		// P or ... or true or ... or Q == true
		Predicate[] predicates = new Predicate[] { P, Q, R };
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LOR, predicates, null);
		assertAssociative("⊤ = ⊤", Lib.True, Predicate.LOR, Lib.True);
		assertAssociative("P ⋁ ⊤ = ⊤", Lib.True, Predicate.LOR, P, Lib.True);
		assertAssociative("⊤ ⋁ P = ⊤", Lib.True, Predicate.LOR, Lib.True, P);
		assertAssociative("P ⋁ Q ⋁ R == P ⋁ Q ⋁ R", expected, Predicate.LOR, P,
				Q, R);
		assertAssociative("P ⋁ Q ⋁ ⊤ ⋁ R == ⊤", Lib.True, Predicate.LOR, P, Q,
				Lib.True, R);
		assertAssociative("⊤ ⋁ P ⋁ Q ⋁ R == ⊤", Lib.True, Predicate.LOR,
				Lib.True, P, Q, R);
		assertAssociative("P ⋁ Q ⋁ R ⋁ ⊤ == ⊤", Lib.True, Predicate.LOR, P, Q,
				R, Lib.True);
		assertAssociative("P ⋁ Q ⋁ ⊤ ⋁ R ⋁ ⊤ == ⊤", Lib.True, Predicate.LOR, P,
				Q, Lib.True, R, Lib.True);
		assertAssociative("⊤ ⋁ P ⋁ Q ⋁ R ⋁ ⊤ == ⊤", Lib.True, Predicate.LOR,
				Lib.True, P, Q, R, Lib.True);
		assertAssociative("⊤ ⋁ P ⋁ Q ⋁ ⊤ ⋁ R == ⊤", Lib.True, Predicate.LOR,
				Lib.True, P, Q, Lib.True, R);
		assertAssociative("⊤ ⋁ P ⋁ Q ⋁ ⊤ ⋁ R ⋁ ⊤ == ⊤", Lib.True,
				Predicate.LOR, Lib.True, P, Q, Lib.True, R, Lib.True);

		// P or ... or true or ... or Q == P or ... or Q
		assertAssociative("⊥ = ⊥", Lib.False, Predicate.LOR, Lib.False);
		assertAssociative("P ⋁ ⊥ = P", P, Predicate.LOR, P, Lib.False);
		assertAssociative("⊥ ⋁ P = P", P, Predicate.LOR, Lib.False, P);
		assertAssociative("P ⋁ Q ⋁ ⊥ ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, Lib.False, R);
		assertAssociative("⊥ ⋁ P ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				Lib.False, P, Q, R);
		assertAssociative("P ⋁ Q ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, R, Lib.False);
		assertAssociative("P ⋁ Q ⋁ ⊥ ∧ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, Lib.False, R, Lib.False);
		assertAssociative("⊥ P ⋁ Q ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, R, Lib.False);
		assertAssociative("⊥ P ⋁ Q ⋁ ⊥ ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, Lib.False, R);
		assertAssociative("⊥ P ⋁ Q ⋁ ⊥ ⋁ R ⋁ ⊥ = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, Lib.False, P, Q, Lib.False, R, Lib.False);
	}

	private void assertBinary(String message, Predicate expected,
			Predicate left, int tag, Predicate right) {
		assertEquals(message, expected, r.rewrite(ff.makeBinaryPredicate(tag,
				left, right, null)));
	}

	public void testImplication() {
		// true => P == P
		assertBinary("⊤ ⇒ P == P", P, Lib.True, Predicate.LIMP, P);

		// false => P == true
		assertBinary("⊥ ⇒ P == ⊤", Lib.True, Lib.False, Predicate.LIMP, P);

		// P => true == true
		assertBinary("P ⇒ ⊤ == ⊤", Lib.True, P, Predicate.LIMP, Lib.True);

		// P => false == not(P)
		assertBinary("P ⇒ ⊥ == ¬P", Lib.makeNeg(P), P, Predicate.LIMP,
				Lib.False);

		// P => P == true
		assertBinary("P ⇒ P == ⊤", Lib.True, P, Predicate.LIMP, P);
	}

	public void testEquivalent() {
		// P <=> true == P
		assertBinary("P ⇔ ⊤ == P", P, P, Predicate.LEQV, Lib.True);

		// true <=> P == P
		assertBinary("⊤ ⇔ P == P", P, Lib.True, Predicate.LEQV, P);

		// P <=> false == not(P)
		assertBinary("P ⇔ ⊥ = ¬P", Lib.makeNeg(P), P, Predicate.LEQV, Lib.False);

		// false <=> P == not(P)
		assertBinary("⊥ ⇔ P = ¬P", Lib.makeNeg(P), Lib.False, Predicate.LEQV, P);

		// P <=> P == true
		assertBinary("P ⇔ P = ⊤", Lib.True, P, Predicate.LEQV, P);
	}

	private void assertUnary(String message, Predicate expected, int tag,
			Predicate predicate) {
		assertEquals(message, expected, r.rewrite(ff.makeUnaryPredicate(tag,
				predicate, null)));
	}

	public void testNegation() {
		// not(true) == false
		assertUnary("¬⊤ == ⊥", Lib.False, Predicate.NOT, Lib.True);

		// not(false) == true
		assertUnary("¬⊥ == ⊤", Lib.False, Predicate.NOT, Lib.True);

		// not(not(P)) == not(P)
		assertUnary("¬¬P = P", P, Predicate.NOT, Lib.makeNeg((P)));
	}

	private void assertQuantification(String message, Predicate expected,
			int tag, BoundIdentDecl[] boundIdentifiers, Predicate predicate) {
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
		assertQuantification("∀x·P == ∀x·P", expected, Predicate.FORALL,
				new BoundIdentDecl[] { x }, ff.makeAssociativePredicate(
						Predicate.LAND, new Predicate[] { P }, null));

		expected = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2 }, null);
		assertQuantification("∀x·P ∧ Q == (∀x·P) ∧ (∀x·Q)", expected,
				Predicate.FORALL, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LAND,
								new Predicate[] { P, Q }, null));

		expected = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2, pred3 }, null);
		assertQuantification("∀x·P ∧ Q ∧ R == (∀x·P) ∧ (∀x·Q) ∧ (∀x·R)",
				expected, Predicate.FORALL, new BoundIdentDecl[] { x }, ff
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
		assertQuantification("∃x·P == ∃x·P", expected, Predicate.EXISTS,
				new BoundIdentDecl[] { x }, ff.makeAssociativePredicate(
						Predicate.LOR, new Predicate[] { P }, null));

		expected = ff.makeAssociativePredicate(Predicate.LOR, new Predicate[] {
				pred1, pred2 }, null);
		assertQuantification("∃x·P ⋁ Q == (∃x·P) ⋁ (∃x·Q)", expected,
				Predicate.EXISTS, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LOR,
								new Predicate[] { P, Q }, null));

		expected = ff.makeAssociativePredicate(Predicate.LOR, new Predicate[] {
				pred1, pred2, pred3 }, null);
		assertQuantification("∃x·P ⋁ Q ⋁ R == (∃x·P) ⋁ (∃x·Q) ⋁ (∃x·R)",
				expected, Predicate.EXISTS, new BoundIdentDecl[] { x }, ff
						.makeAssociativePredicate(Predicate.LOR,
								new Predicate[] { P, Q, R }, null));
		
	}

}
