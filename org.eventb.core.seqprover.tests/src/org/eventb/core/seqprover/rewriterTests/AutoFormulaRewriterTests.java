package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.junit.Before;
import org.junit.Test;

public class AutoFormulaRewriterTests {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final IntegerLiteral L1 =
		ff.makeIntegerLiteral(new BigInteger("1"), null);
	private static final IntegerLiteral L2 =
		ff.makeIntegerLiteral(new BigInteger("2"), null);
	private static final IntegerLiteral L3 =
		ff.makeIntegerLiteral(new BigInteger("3"), null);
	private static final IntegerLiteral L4 =
		ff.makeIntegerLiteral(new BigInteger("4"), null);

	private static final Predicate P =
		ff.makeRelationalPredicate(Predicate.EQUAL, L1, L2, null);		
	private static final Predicate Q =
		ff.makeRelationalPredicate(Predicate.EQUAL, L2, L3, null);		
	private static final Predicate R =
		ff.makeRelationalPredicate(Predicate.EQUAL, L3, L4, null);		

	private static final Predicate notP =
		ff.makeUnaryPredicate(Predicate.NOT, P, null);
	private static final Predicate notQ =
		ff.makeUnaryPredicate(Predicate.NOT, Q, null);
	private static final Predicate notR =
		ff.makeUnaryPredicate(Predicate.NOT, R, null);

	private IFormulaRewriter r;
	

	@Before
	public void setUp() throws Exception {
		r = new AutoRewriterImpl();
	}

	private void assertAssociativePredicate(String message, Predicate expected,
			int tag, Predicate... predicates) {
		AssociativePredicate predicate = ff.makeAssociativePredicate(tag,
				predicates, null);
		assertEquals(message, expected, r.rewrite(predicate));
	}

	@Test
	public void testConjunction() {
		Predicate[] predicates = new Predicate[] { P, Q, R };
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, predicates, null);

		// P & ... & true & ... & Q == P & ... & ... & Q
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
		assertAssociativePredicate("P ∧ P = P", P, Predicate.LAND, P, P);
		assertAssociativePredicate("P ∧ P ∧ Q ∧ R = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, P, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ P ∧ R = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, P, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ P = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, P);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ Q = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, Q);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ R = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ Q ∧ R = P ∧ Q ∧ R", expected,
				Predicate.LAND, P, Q, R, Q, R);

		// P & ... & Q & ... & not(Q) & ... & R == false
		assertAssociativePredicate("P ∧ ¬P = ⊥", Lib.False, Predicate.LAND, P,
				notP);
		assertAssociativePredicate("P ∧ ¬P ∧ Q ∧ R = ⊥", Lib.False,
				Predicate.LAND, P, notP, Q, R);
		assertAssociativePredicate("P ∧ Q ∧ ¬P ∧ R = ⊥", Lib.False,
				Predicate.LAND, P, Q, notP, R);
		assertAssociativePredicate("P ∧ Q ∧ R ∧ ¬P = ⊥", Lib.False,
				Predicate.LAND, P, Q, R, notP);
		assertAssociativePredicate("P ∧ ¬Q ∧ R ∧ Q = ⊥", Lib.False,
				Predicate.LAND, P, notQ, R, Q);
		assertAssociativePredicate("P ∧ Q ∧ ¬R ∧ R = ⊥", Lib.False,
				Predicate.LAND, P, Q, notR, R);
		assertAssociativePredicate("P ∧ ¬Q ∧ R ∧ Q ∧ ¬R = ⊥", Lib.False,
				Predicate.LAND, P, Q, R, Q, notR);
	}

	@Test
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

		// P or ... or false or ... or Q == P or ... or ... or Q
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

		// P or ... or Q or ... or Q or ... or R == P or ... or Q or ... or ...
		// or R
		assertAssociativePredicate("P ⋁ P = P", P, Predicate.LOR, P, P);
		assertAssociativePredicate("P ⋁ P ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, P, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ P ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, P, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ P = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R, P);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ Q = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R, Q);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ Q ⋁ R = P ⋁ Q ⋁ R", expected,
				Predicate.LOR, P, Q, R, Q, R);

		// P or ... or Q or ... or not(Q) or ... or R == true
		assertAssociativePredicate("P ⋁ ¬P = T", Lib.True, Predicate.LOR, P,
				notP);
		assertAssociativePredicate("P ⋁ ¬P ⋁ Q ⋁ R = T", Lib.True,
				Predicate.LOR, P, notP, Q, R);
		assertAssociativePredicate("P ⋁ Q ⋁ ¬P ⋁ R = T", Lib.True,
				Predicate.LOR, P, Q, notP, R);
		assertAssociativePredicate("P ⋁ Q ⋁ R ⋁ ¬P = T", Lib.True,
				Predicate.LOR, P, Q, R, notP);
		assertAssociativePredicate("P ⋁ ¬Q ⋁ R ⋁ Q = T", Lib.True,
				Predicate.LOR, P, notQ, R, Q);
		assertAssociativePredicate("P ⋁ Q ⋁ ¬R ⋁ R = T", Lib.True,
				Predicate.LOR, P, Q, notR, R);
		assertAssociativePredicate("P ⋁ ¬Q ⋁ R ⋁ Q ⋁ ¬R = T", Lib.True,
				Predicate.LOR, P, Q, R, Q, notR);
	}

	private void assertBinaryPredicate(String message, Predicate expected,
			Predicate left, int tag, Predicate right) {
		assertEquals(message, expected, r.rewrite(ff.makeBinaryPredicate(tag,
				left, right, null)));
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
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
				new Predicate[] { pred1, pred2 }, null);
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

	@Test
	public void testEquality() {
		// E = E == true
		Expression E = ff.makeAtomicExpression(Expression.TRUE, null);
		Expression F = ff.makeAtomicExpression(Expression.FALSE, null);
		Expression G = ff.makeAtomicExpression(Expression.FALSE, null);
		Expression H = ff.makeAtomicExpression(Expression.TRUE, null);
		assertRelationalPredicate("E = E == ⊤", Lib.True, E, Predicate.EQUAL, E);

		// E /= E == false
		assertRelationalPredicate("E ≠ E == ⊥", Lib.False, E,
				Predicate.NOTEQUAL, E);

		// E |-> F = G |-> H == E = G & F = H
		Predicate pred1 = ff.makeRelationalPredicate(Expression.EQUAL, E, G,
				null);
		Predicate pred2 = ff.makeRelationalPredicate(Expression.EQUAL, F, H,
				null);
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, new Predicate[] { pred1, pred2 }, null);
		BinaryExpression left = ff.makeBinaryExpression(Expression.MAPSTO, E,
				F, null);
		BinaryExpression right = ff.makeBinaryExpression(Expression.MAPSTO, G,
				H, null);
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

	private void assertUnaryExpression(String message, Expression expected,
			int tag, Expression expression) {
		assertEquals(message, expected, r.rewrite(ff.makeUnaryExpression(tag,
				expression, null)));
	}

	@Test
	public void testSetTheory() {
		Expression fTrue = ff.makeAtomicExpression(Expression.TRUE, null);
		Expression fFalse = ff.makeAtomicExpression(Expression.FALSE, null);
		Expression S = ff.makeSetExtension(fTrue, null);
		Expression T = ff.makeSetExtension(fFalse, null);
		Expression R = ff.makeSetExtension(new Expression[] { fTrue, fFalse },
				null);

		// S /\ ... /\ {} /\ ... /\ T == {}
		Expression expected = ff.makeAssociativeExpression(Expression.BINTER,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∩ ∅ == ∅", Lib.emptySet,
				Expression.BINTER, S, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S == ∅", Lib.emptySet,
				Expression.BINTER, Lib.emptySet, S);
		assertAssociativeExpression("S ∩ T ∩ R == S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R == ∅", Lib.emptySet,
				Expression.BINTER, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R == ∅", Lib.emptySet,
				Expression.BINTER, Lib.emptySet, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Expression.BINTER, S, T, R, Lib.emptySet);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Expression.BINTER, S, T, Lib.emptySet, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Expression.BINTER, Lib.emptySet, S, T, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R == ∅", Lib.emptySet,
				Expression.BINTER, Lib.emptySet, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ R ∩ ∅ == ∅", Lib.emptySet,
				Expression.BINTER, Lib.emptySet, S, T, Lib.emptySet, R,
				Lib.emptySet);

		// S /\ ... /\ T /\ ... /\ T /\ ... /\ R == S /\ ... /\ T /\ ... /\ ...
		// /\ R
		assertAssociativeExpression("S ∩ S = S", S, Expression.BINTER, S, S);
		assertAssociativeExpression("S ∩ S ∩ T ∩ R = S ∩ T ∩ R", expected,
				Expression.BINTER, S, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ S ∩ R = S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, S, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ S = S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, R, S);
		assertAssociativeExpression("S ∩ T ∩ R ∩ T = S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, R, T);
		assertAssociativeExpression("S ∩ T ∩ R ∩ R = S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, R, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ T ∩ R = S ∩ T ∩ R", expected,
				Expression.BINTER, S, T, R, T, R);

		// S \/ ... \/ {} \/ ... \/ T == S ... \/ ... \/ T
		expected = ff.makeAssociativeExpression(Expression.BUNION,
				new Expression[] { S, T, R }, null);
		assertAssociativeExpression("S ∪ ∅ == S", S, Expression.BUNION, S,
				Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S == S", S, Expression.BUNION,
				Lib.emptySet, S);
		assertAssociativeExpression("S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Expression.BUNION, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Expression.BUNION, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R == S ∪ T ∪ R", expected,
				Expression.BUNION, Lib.emptySet, S, T, R);
		assertAssociativeExpression("S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Expression.BUNION, S, T, R, Lib.emptySet);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Expression.BUNION, S, T, Lib.emptySet, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ R ∪ ∅ == S ∪ T ∪ R", expected,
				Expression.BUNION, Lib.emptySet, S, T, R, Lib.emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R == S ∪ T ∪ R", expected,
				Expression.BUNION, Lib.emptySet, S, T, Lib.emptySet, R);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ R ∪ ∅ == S ∪ T ∪ R",
				expected, Expression.BUNION, Lib.emptySet, S, T, Lib.emptySet,
				R, Lib.emptySet);

		// S \/ ... \/ T \/ ... \/ T \/ ... \/ R == S \/ ... \/ T \/ ... \/ ...
		// \/ R
		assertAssociativeExpression("S ∩ S = S", S, Expression.BUNION, S, S);
		assertAssociativeExpression("S ∩ S ∩ T ∩ R = S ∩ T ∩ R", expected,
				Expression.BUNION, S, S, T, R);
		assertAssociativeExpression("S ∩ T ∩ S ∩ R = S ∩ T ∩ R", expected,
				Expression.BUNION, S, T, S, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ S = S ∩ T ∩ R", expected,
				Expression.BUNION, S, T, R, S);
		assertAssociativeExpression("S ∩ T ∩ R ∩ T = S ∩ T ∩ R", expected,
				Expression.BUNION, S, T, R, T);
		assertAssociativeExpression("S ∩ T ∩ R ∩ R = S ∩ T ∩ R", expected,
				Expression.BUNION, S, T, R, R);
		assertAssociativeExpression("S ∩ T ∩ R ∩ T ∩ R = S ∩ T ∩ R", expected,
				Expression.BUNION, S, T, R, T, R);

		// {} <: S == true
		assertRelationalPredicate("∅ ⊆ S == ⊤", Lib.True, Lib.emptySet,
				Expression.SUBSETEQ, S);

		// S <: S == true
		assertRelationalPredicate("S ⊆ S == ⊤", Lib.True, S,
				Expression.SUBSETEQ, S);

		// E : {} == false
		assertRelationalPredicate("E ∈ ∅ == ⊥", Lib.False, fTrue,
				Expression.IN, Lib.emptySet);

		// A : {A} == true
		assertRelationalPredicate("A ∈ {A} == ⊤", Lib.True, fTrue,
				Expression.IN, S);

		// B : {A, ..., B, ..., C} == true
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True,
				fTrue, Expression.IN, R);
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True,
				fFalse, Expression.IN, R);

		// E : {x | P(x)} == P(E)
		BoundIdentDecl xDecl = ff.makeBoundIdentDecl("x", null);
		Expression E = ff.makeIntegerLiteral(new BigInteger("4"), null);
		Expression x = ff.makeBoundIdentifier(0, null);
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, x, ff
				.makeAtomicExpression(Expression.NATURAL, null), null);
		Predicate pred2 = ff.makeRelationalPredicate(Predicate.LE, x, ff
				.makeIntegerLiteral(new BigInteger("3"), null), null);

		Predicate pred = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { pred1, pred2 }, null);
		Expression cSet = ff.makeQuantifiedExpression(Expression.CSET,
				new BoundIdentDecl[] { xDecl }, pred, x, null,
				QuantifiedExpression.Form.Implicit);

		Predicate result = TestLib.genPred("4 ∈ ℕ ∧ 4 ≤ 3");
		assertRelationalPredicate("", result, E, Expression.IN, cSet);

		// S \ S == {}
		assertBinaryExpression("S ∖ S == ∅", Lib.emptySet, S,
				Expression.SETMINUS, S);
		assertBinaryExpression("T ∖ T == ∅", Lib.emptySet, T,
				Expression.SETMINUS, T);
		assertBinaryExpression("R ∖ R == ∅", Lib.emptySet, R,
				Expression.SETMINUS, R);

		// r~~ == r
		Expression m1 = ff.makeBinaryExpression(Expression.MAPSTO, fTrue,
				fFalse, null);
		Expression m2 = ff.makeBinaryExpression(Expression.MAPSTO, fFalse,
				fTrue, null);
		Expression m3 = ff.makeBinaryExpression(Expression.MAPSTO, fFalse,
				fFalse, null);
		Expression m4 = ff.makeBinaryExpression(Expression.MAPSTO, fTrue,
				fTrue, null);
		Expression f = ff.makeSetExtension(new Expression[] { m1 }, null);
		assertUnaryExpression("f∼∼ = f", f, Expression.CONVERSE, ff
				.makeUnaryExpression(Expression.CONVERSE, f, null));
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		assertUnaryExpression("f∼∼ = f", f, Expression.CONVERSE, ff
				.makeUnaryExpression(Expression.CONVERSE, f, null));

		// dom({x |-> a, ..., y |-> b}) == {x, ..., y}
		f = ff.makeSetExtension(new Expression[] { m1 }, null);
		assertUnaryExpression("", S, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		assertUnaryExpression("", R, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m4 }, null);
		assertUnaryExpression("", S, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2, m3, m4 }, null);
		assertUnaryExpression("", R, Expression.KDOM, f);

		// ran({x |-> a, ..., y |-> b}) == {a, ..., b}
		f = ff.makeSetExtension(new Expression[] { m1 }, null);
		assertUnaryExpression("", T, Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		assertUnaryExpression("", R, Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m1, m3 }, null);
		assertUnaryExpression("", T, Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2, m3, m4 }, null);
		assertUnaryExpression("", R, Expression.KRAN, f);

		// (f <+ {E |-> F})(E) = F
		f = ff.makeSetExtension(new Expression[] { m1 }, null);

		Expression g = ff.makeAssociativeExpression(Expression.OVR,
				new Expression[] { f, ff.makeSetExtension(m2, null) }, null);

		assertBinaryExpression("{f  {E ↦ F})(E) = F", fTrue, g,
				Expression.FUNIMAGE, fFalse);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, fTrue, null), g, Expression.FUNIMAGE,
				fTrue);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m4, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", fTrue, g,
				Expression.FUNIMAGE, fTrue);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, fFalse, null), g, Expression.FUNIMAGE,
				fFalse);

		f = ff.makeSetExtension(new Expression[] { m1, m3 }, null);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m2, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", fTrue, g,
				Expression.FUNIMAGE, fFalse);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, fTrue, null), g, Expression.FUNIMAGE,
				fTrue);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m4, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", fTrue, g,
				Expression.FUNIMAGE, fTrue);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, fFalse, null), g, Expression.FUNIMAGE,
				fFalse);

		// E : {F} == E = F (if F is a single expression)
		assertRelationalPredicate(
				"E ∈ {F} == E = F   if F is a single expression", ff
						.makeRelationalPredicate(Predicate.EQUAL, fTrue,
								fFalse, null), fTrue, Predicate.IN, ff
						.makeSetExtension(fFalse, null));

		// {E} = {F} == E = F if E, F is a single expression
		assertRelationalPredicate(
				"{E} = {F} == E = F   if E, F is a single expression", ff
						.makeRelationalPredicate(Predicate.EQUAL, fTrue,
								fFalse, null),
				ff.makeSetExtension(fTrue, null), Predicate.EQUAL, ff
						.makeSetExtension(fFalse, null));
		
		// S \ {} == S
		assertBinaryExpression("S ∖ ∅ == S", S, S, Expression.SETMINUS, Lib.emptySet);
		assertBinaryExpression("T ∖ ∅ == T", T, T, Expression.SETMINUS, Lib.emptySet);
		assertBinaryExpression("R ∖ ∅ == R", R, R, Expression.SETMINUS, Lib.emptySet);
	}

	@Test
	public void testArithmetic() {
		Expression number0 = ff.makeIntegerLiteral(new BigInteger("0"), null);
		Expression number1 = L1;
		Expression number2 = ff.makeIntegerLiteral(new BigInteger("2"), null);
		Expression number3 = ff.makeIntegerLiteral(new BigInteger("3"), null);
		Expression numberMinus1 = ff.makeUnaryExpression(Expression.UNMINUS,
				number1, null);
		Expression numberMinus2 = ff.makeUnaryExpression(Expression.UNMINUS,
				number2, null);
		Expression numberMinus3 = ff.makeUnaryExpression(Expression.UNMINUS,
				number3, null);

		// E + ... + 0 + ... + F == E + ... + ... + F
		Expression sum11 = ff.makeAssociativeExpression(Expression.PLUS,
				new Expression[] { number1, number1 }, null);
		Expression sum12 = ff.makeAssociativeExpression(Expression.PLUS,
				new Expression[] { number1, number2 }, null);
		assertAssociativeExpression("0 + 0 == 0", number0, Expression.PLUS,
				number0, number0);
		assertAssociativeExpression("0 + 1 == 1", number1, Expression.PLUS,
				number0, number1);
		assertAssociativeExpression("1 + 0 == 1", number1, Expression.PLUS,
				number1, number0);
		assertAssociativeExpression("1 + 0 + 1 == 1 + 1", sum11,
				Expression.PLUS, number1, number0, number1);
		assertAssociativeExpression("0 + 1 + 2 == 1 + 2", sum12,
				Expression.PLUS, number0, number1, number2);
		assertAssociativeExpression("1 + 0 + 2 == 1 + 2", sum12,
				Expression.PLUS, number1, number0, number2);
		assertAssociativeExpression("1 + 2 + 0== 1 + 2", sum12,
				Expression.PLUS, number1, number2, number0);
		assertAssociativeExpression("1 + 0 + 2 + 0 == 1 + 2", sum12,
				Expression.PLUS, number1, number0, number2, number0);
		assertAssociativeExpression("0 + 1 + 2 + 0 == 1 + 2", sum12,
				Expression.PLUS, number0, number1, number2, number0);
		assertAssociativeExpression("0 + 1 + 0 + 2 == 1 + 2", sum12,
				Expression.PLUS, number0, number1, number0, number2);

		// E - 0 == E
		assertBinaryExpression("E − 0 == E", number1, number1,
				Expression.MINUS, number0);

		// 0 - E == -E
		assertBinaryExpression("0 − E == −E", numberMinus1, number0,
				Expression.MINUS, number1);

		// --E == E
		assertUnaryExpression("−(−E) = E", number1, Expression.UNMINUS,
				numberMinus1);

		// E * ... * 1 * ... * F == E * ... * ... * F
		Expression prod23 = ff.makeAssociativeExpression(Expression.MUL,
				new Expression[] { number2, number3 }, null);
		assertAssociativeExpression("1 * 1 == 1", number1, Expression.MUL,
				number1, number1);
		assertAssociativeExpression("1 * 2 == 2", number2, Expression.MUL,
				number1, number2);
		assertAssociativeExpression("2 * 1 == 2", number2, Expression.MUL,
				number2, number1);
		assertAssociativeExpression("2 * 1 * 3 == 2 * 3", prod23,
				Expression.MUL, number2, number1, number3);
		assertAssociativeExpression("1 * 2 * 3 == 2 * 3", prod23,
				Expression.MUL, number1, number2, number3);
		assertAssociativeExpression("2 * 1 * 3 == 2 * 3", prod23,
				Expression.MUL, number2, number1, number3);
		assertAssociativeExpression("2 * 3 * 1== 2 * 3", prod23,
				Expression.MUL, number2, number3, number1);
		assertAssociativeExpression("2 * 1 * 3 * 1 == 2 * 3", prod23,
				Expression.MUL, number2, number1, number3, number1);
		assertAssociativeExpression("1 * 2 * 3 * 1 == 2 * 3", prod23,
				Expression.MUL, number1, number2, number3, number1);
		assertAssociativeExpression("1 * 2 * 1 * 3 == 2 * 3", prod23,
				Expression.MUL, number1, number2, number1, number3);

		// E * ... * 0 * ... * F == 0
		assertAssociativeExpression("0 * 0 == 0", number0, Expression.MUL,
				number0, number0);
		assertAssociativeExpression("0 * 1 == 0", number0, Expression.MUL,
				number0, number1);
		assertAssociativeExpression("1 * 0 == 0", number0, Expression.MUL,
				number1, number0);
		assertAssociativeExpression("1 * 0 * 1 == 0", number0, Expression.MUL,
				number1, number0, number1);
		assertAssociativeExpression("0 * 1 * 2 == 0", number0, Expression.MUL,
				number0, number1, number2);
		assertAssociativeExpression("1 * 0 * 2 == 0", number0, Expression.MUL,
				number1, number0, number2);
		assertAssociativeExpression("1 * 2 * 0== 0", number0, Expression.MUL,
				number1, number2, number0);
		assertAssociativeExpression("1 * 0 * 2 * 0 == 0", number0,
				Expression.MUL, number1, number0, number2, number0);
		assertAssociativeExpression("0 * 1 * 2 * 0 == 0", number0,
				Expression.MUL, number0, number1, number2, number0);
		assertAssociativeExpression("0 * 1 * 0 * 2 == 0", number0,
				Expression.MUL, number0, number1, number0, number2);

		// (-E) * (-F) == E * F
		assertAssociativeExpression("(−2) ∗ (−3) == 2 ∗ 3", prod23,
				Expression.MUL, numberMinus2, numberMinus3);
		assertAssociativeExpression("(−1) ∗ (−2) ∗ 3 == 2 ∗ 3", prod23,
				Expression.MUL, numberMinus1, numberMinus2, number3);
		assertAssociativeExpression("(−1) ∗ (−2) ∗ (−3) == −(2 ∗ 3)", ff
				.makeUnaryExpression(Expression.UNMINUS, prod23, null),
				Expression.MUL, numberMinus1, numberMinus2, numberMinus3);

		// E / 1 == E
		assertBinaryExpression("E ÷ 1 = E", number2, number2, Expression.DIV,
				number1);

		// 0 / E == 0
		assertBinaryExpression("0 ÷ E = 0", number0, number0, Expression.DIV,
				number2);

		// (-E) /(-F) == E / F
		Expression expected = ff.makeBinaryExpression(Expression.DIV, number3,
				number2, null);
		assertBinaryExpression("(−E) ÷ (−F) == E ÷ F", expected, numberMinus3,
				Expression.DIV, numberMinus2);

		// E^1 == E
		assertBinaryExpression("E^1 == E", number2, number2, Expression.EXPN,
				number1);

		// E^0 == 1
		assertBinaryExpression("E^0 == 1", number1, number2, Expression.EXPN,
				number0);

		// 1^E == 1
		assertBinaryExpression("1^E == 1", number1, number1, Expression.EXPN,
				number0);
		assertBinaryExpression("1^E == 1", number1, number1, Expression.EXPN,
				number1);
		assertBinaryExpression("1^E == 1", number1, number1, Expression.EXPN,
				number2);
	}
}
