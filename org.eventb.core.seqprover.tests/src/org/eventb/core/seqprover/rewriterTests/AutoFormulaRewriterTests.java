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

		// P & ... & Q & ... & Q & ... & R == P & ... & Q & ... & ... & R
		assertAssociativePredicate("P ∧ P = P", P, Predicate.LAND, P,
				P);
		assertAssociativePredicate("P ∧ P ∧ Q ∧ R = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, P, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ P ∧ R = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, P, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ P = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, R, P);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ Q = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, R, Q);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ R = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, R, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ Q ∧ R = P ∧ Q ∧ R", expected, Predicate.LAND,
				P, Q, R, R);
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

		// P or ... or false or ... or Q == P or ... or Q
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

		// P or ... or Q or ... or Q or ... or R == P or ... or Q or ... or ... or R
		assertAssociativePredicate("P ⋁ P = P", P, Predicate.LOR, P,
				P);
		assertAssociativePredicate("P ⋁ P ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, P, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ P ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, P, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ P = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, R, P);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ Q = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, R, Q);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, R, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected, Predicate.LOR,
				P, Q, R, R);	}

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

		Predicate expected = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
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

	private void assertBinaryExpression(String message, Expression expected,
			Expression left, int tag, Expression right) {
		BinaryExpression expression = ff.makeBinaryExpression(tag, left, right,
				null);
		assertEquals(message, expected, r.rewrite(expression));
	}

	private void assertUnaryExpression(String message, Expression expected, int tag, Expression expression) {
		assertEquals(message, expected, r.rewrite(ff.makeUnaryExpression(tag,
				expression, null)));
	}

	public void testSetTheory() {
		Expression fTrue = ff.makeAtomicExpression(Formula.TRUE, null);
		Expression fFalse = ff.makeAtomicExpression(Formula.FALSE, null);
		Expression S = ff.makeSetExtension(fTrue, null);
		Expression T = ff.makeSetExtension(fFalse, null);
		Expression R = ff.makeSetExtension(new Expression[] { fTrue, fFalse },
				null);
		// S /\ ... /\ {} /\ ... /\ T == {}
		Expression expected = ff.makeAssociativeExpression(Formula.BINTER,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∩ ∅ == ∅", Lib.emptySet, Formula.BINTER,
				S, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S == ∅", Lib.emptySet, Formula.BINTER,
				Lib.emptySet, S);
		assertAssociativeExpression("S ∩ T ∩ R == S ∩ T ∩ R", expected,
				Formula.BINTER, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R == ∅", Lib.emptySet,
				Formula.BINTER, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R == ∅", Lib.emptySet,
				Formula.BINTER, Lib.emptySet, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Formula.BINTER, S, T, R, Lib.emptySet);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Formula.BINTER, S, T, Lib.emptySet, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Formula.BINTER, Lib.emptySet, S, T, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R == ∅", Lib.emptySet,
				Formula.BINTER, Lib.emptySet, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Formula.BINTER, Lib.emptySet, S, T, Lib.emptySet, R,
				Lib.emptySet);

		// S /\ ... /\ {} /\ ... /\ T == {}
		expected = ff.makeAssociativeExpression(Formula.BUNION,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∪ ∅ == S", S, Formula.BUNION, S,
				Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S == S", S, Formula.BUNION,
				Lib.emptySet, S);
		assertAssociativeExpression("S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, Lib.emptySet, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, R, Lib.emptySet);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, S, T, Lib.emptySet, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Formula.BUNION, Lib.emptySet, S, T, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Formula.BUNION, Lib.emptySet, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R",
				expected, Formula.BUNION, Lib.emptySet, S, T, Lib.emptySet, R,
				Lib.emptySet);

		// {} <: S == true
		assertRelationalPredicate("∅ ⊆ S == ⊤", Lib.True, Lib.emptySet,
				Formula.SUBSETEQ, S);

		// S <: S == true
		assertRelationalPredicate("S ⊆ S == ⊤", Lib.True, S, Formula.SUBSETEQ,
				S);

		// E : {} == false
		assertRelationalPredicate("E ∈ ∅ == ⊥", Lib.False, fTrue, Formula.IN,
				Lib.emptySet);

		// A : {A} == true
		assertRelationalPredicate("A ∈ {A} == ⊤", Lib.True, fTrue, Formula.IN,
				S);

		// B : {A, ..., B, ..., C} == true
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True,
				fTrue, Formula.IN, R);
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True,
				fFalse, Formula.IN, R);

		// S \ S == {}
		assertBinaryExpression("S ∖ S == ∅", Lib.emptySet, S, Formula.SETMINUS,
				S);
		assertBinaryExpression("T ∖ T == ∅", Lib.emptySet, T, Formula.SETMINUS,
				T);
		assertBinaryExpression("R ∖ R == ∅", Lib.emptySet, R, Formula.SETMINUS,
				R);

		// r~~ == r
		Expression m1 = ff.makeBinaryExpression(Formula.MAPSTO, fTrue, fFalse, null);
		Expression m2 = ff.makeBinaryExpression(Formula.MAPSTO, fFalse, fTrue, null);
		Expression f = ff.makeSetExtension(new Expression[] {m1}, null);
		assertUnaryExpression("", f, Formula.CONVERSE, ff.makeUnaryExpression(
				Formula.CONVERSE, f, null));
		f = ff.makeSetExtension(new Expression[] {m1, m2}, null);
		assertUnaryExpression("", f, Formula.CONVERSE, ff.makeUnaryExpression(
				Formula.CONVERSE, f, null));
		
		
		
	}

}
