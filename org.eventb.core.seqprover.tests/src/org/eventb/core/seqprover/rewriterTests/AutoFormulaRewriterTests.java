package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewriterImpl;
import org.junit.Before;
import org.junit.Test;

public class AutoFormulaRewriterTests {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	protected static Expression emptySet = ff.makeEmptySet(ff.makePowerSetType(ff
			.makeIntegerType()), null);

	protected static Expression number0 = ff.makeIntegerLiteral(new BigInteger(
			"0"), null);

	protected static Expression number1 = ff.makeIntegerLiteral(new BigInteger(
			"1"), null);;

	protected static Expression number2 = ff.makeIntegerLiteral(new BigInteger(
			"2"), null);

	protected static Expression number3 = ff.makeIntegerLiteral(new BigInteger(
			"3"), null);

	protected static Expression numberMinus1 = ff.makeIntegerLiteral(new BigInteger(
			"-1"), null);;

	protected static Expression numberMinus2 = ff.makeIntegerLiteral(new BigInteger(
			"-2"), null);

	protected static Expression numberMinus3 = ff.makeIntegerLiteral(new BigInteger(
			"-3"), null);

	protected static Expression uNumberMinus1 = ff.makeUnaryExpression(Expression.UNMINUS,
			number1, null);

	protected static Expression uNumberMinus2 = ff.makeUnaryExpression(Expression.UNMINUS,
			number2, null);

	protected static Expression uNumberMinus3 = ff.makeUnaryExpression(Expression.UNMINUS,
			number3, null);

	protected static Expression integer = Lib.parseExpression("ℤ");
	
	protected static Expression powInteger = Lib.parseExpression("ℙ(ℤ)");
	
	protected static final Expression E = Lib.parseExpression("x ∗ 2 + 3");

	protected static final Expression F = Lib.parseExpression("y ∗ 3 + 1");
	
	protected static final Expression bE = Lib.parseExpression("x");

	protected static Expression S = Lib.parseExpression("{1}");
	
	protected static Expression T = Lib.parseExpression("{1,2}");

	protected static Expression U = Lib.parseExpression("{1,2,3}");

	protected static final Predicate P = Lib.parsePredicate("x = 2");
			
	protected static final Predicate Q = Lib.parsePredicate("y = 3");
				
	protected static final Predicate R = Lib.parsePredicate("z = 4");		

	protected static final Predicate notP =
		ff.makeUnaryPredicate(Predicate.NOT, P, null);
	protected static final Predicate notQ =
		ff.makeUnaryPredicate(Predicate.NOT, Q, null);
	protected static final Predicate notR =
		ff.makeUnaryPredicate(Predicate.NOT, R, null);

	protected IFormulaRewriter r;
	
	@Before
	public void setUp() throws Exception {
		r = new AutoRewriterImpl();
		P.typeCheck(Lib.makeTypeEnvironment());
		Q.typeCheck(Lib.makeTypeEnvironment());
		R.typeCheck(Lib.makeTypeEnvironment());
		notP.typeCheck(Lib.makeTypeEnvironment());
		notQ.typeCheck(Lib.makeTypeEnvironment());
		notR.typeCheck(Lib.makeTypeEnvironment());
		S.typeCheck(Lib.makeTypeEnvironment());
		T.typeCheck(Lib.makeTypeEnvironment());
		U.typeCheck(Lib.makeTypeEnvironment());
		E.typeCheck(Lib.makeTypeEnvironment());
		F.typeCheck(Lib.makeTypeEnvironment());
		bE.typeCheck(Lib.makeTypeEnvironment());
	}

	protected void assertAssociativePredicate(String message, Predicate expected,
			int tag, Predicate... predicates) {
		AssociativePredicate predicate = ff.makeAssociativePredicate(tag,
				predicates, null);
		assertEquals(message, expected, predicate.rewrite(r));
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

	protected void assertBinaryPredicate(String message, Predicate expected,
			Predicate left, int tag, Predicate right) {
		BinaryPredicate bPred = ff.makeBinaryPredicate(tag,
								left, right, null);
		assertEquals(message, expected, bPred.rewrite(r));
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

	protected void assertUnaryPredicate(String message, Predicate expected,
			int tag, Predicate predicate) {
		UnaryPredicate uPred = ff.makeUnaryPredicate(tag,
								predicate, null);
		assertEquals(message, expected, uPred.rewrite(r));
	}

	@Test
	public void testNegation() {
		// not(true)  ==  false
		assertUnaryPredicate("¬⊤ == ⊥", Lib.False, Predicate.NOT, Lib.True);

		// not(false)  ==  true
		assertUnaryPredicate("¬⊥ == ⊤", Lib.True, Predicate.NOT, Lib.False);

		// not(not(P))  ==  not(P)
		assertUnaryPredicate("¬¬P = P", P, Predicate.NOT, Lib.makeNeg((P)));

		// not(x /: S))  ==  x : S
		RelationalPredicate rPred = ff.makeRelationalPredicate(Predicate.NOTIN,
				number0, S, null);
		assertUnaryPredicate("¬ (x ∉ S) == x ∈ S", ff.makeRelationalPredicate(
				Predicate.IN, number0, S, null), Predicate.NOT, rPred);
		
		// E /= F  ==  not (E = F)
		assertRelationalPredicate("E ≠ F == ¬ E = F", ff.makeUnaryPredicate(Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.EQUAL, E, F, null), null),
				E, Predicate.NOTEQUAL, F);

		// E /: F  ==  not (E : F)
		assertRelationalPredicate("E ∉ F == ¬ E ∈ F", ff.makeUnaryPredicate(Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.IN, E, S, null), null),
				E, Predicate.NOTIN, S);

		// E /<<: F  ==  not (E <<: F)
		assertRelationalPredicate("E ⊄ F == ¬ E ⊂ F", ff.makeUnaryPredicate(Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.SUBSET, S, T, null), null),
				S, Predicate.NOTSUBSET, T);

		// E /<: F  ==  not (E <: F)
		assertRelationalPredicate("E ⊈ F == ¬ E ⊆ F", ff.makeUnaryPredicate(Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.SUBSETEQ, S, T, null), null),
				S, Predicate.NOTSUBSETEQ, T);

		// not(a <= b) == a > b
		assertUnaryPredicate("¬ a ≤ b == a > b", ff.makeRelationalPredicate(
				Predicate.GT, E, F, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.LE, E, F, null));

		// not(a >= b) == a < b
		assertUnaryPredicate("¬ a ≥ b == a < b", ff.makeRelationalPredicate(
				Predicate.LT, E, F, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.GE, E, F, null));

    	// not(a > b) == a <= b
		assertUnaryPredicate("¬ a > b == a ≤ b", ff.makeRelationalPredicate(
				Predicate.LE, E, F, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.GT, E, F, null));

	   	// not(a < b) == a >= b
		assertUnaryPredicate("¬ a < b == a ≥ b", ff.makeRelationalPredicate(
				Predicate.GE, E, F, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.LT, E, F, null));

	   	// not(E = FALSE) == E = TRUE
		assertUnaryPredicate("¬ E = FALSE == E = TRUE", ff.makeRelationalPredicate(
				Predicate.EQUAL, bE, Lib.TRUE, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.EQUAL, bE, Lib.FALSE, null));

	   	// not(E = TRUE) == E = FALSE
		assertUnaryPredicate("¬ E = TRUE == E = FALSE", ff.makeRelationalPredicate(
				Predicate.EQUAL, bE, Lib.FALSE, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.EQUAL, bE, Lib.TRUE, null));

	   	// not(FALSE = E) == TRUE = E
		assertUnaryPredicate("¬ FALSE = E == TRUE = E", ff.makeRelationalPredicate(
				Predicate.EQUAL, Lib.TRUE, bE, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.EQUAL, Lib.FALSE, bE, null));

	   	// not(TRUE = E) == FALSE = E
		assertUnaryPredicate("¬ TRUE = E == FALSE = E", ff.makeRelationalPredicate(
				Predicate.EQUAL, Lib.FALSE, bE, null), Predicate.NOT, ff
				.makeRelationalPredicate(Predicate.EQUAL, Lib.TRUE, bE, null));
	}

	protected void assertQuantificationPredicate(String message,
			Predicate expected, int tag, BoundIdentDecl[] boundIdentifiers,
			Predicate predicate) {
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
								tag, boundIdentifiers, predicate, null);
		assertEquals(message, expected, qPred.rewrite(r));
	}

	@Test
	public void testQuantification() {
		final Predicate P = ff.makeRelationalPredicate(EQUAL,
				ff.makeBoundIdentifier(0, null),
				ff.makeIntegerLiteral(new BigInteger("2"), null), null);
		final Predicate Q = ff.makeRelationalPredicate(EQUAL,
				ff.makeBoundIdentifier(0, null),
				ff.makeIntegerLiteral(new BigInteger("3"), null), null);
		final Predicate R = ff.makeRelationalPredicate(EQUAL,
				ff.makeBoundIdentifier(0, null),
				ff.makeIntegerLiteral(new BigInteger("4"), null), null);
		
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

	protected void assertRelationalPredicate(String message, Predicate expected,
			Expression left, int tag, Expression right) {
		RelationalPredicate rPred = ff.makeRelationalPredicate(
								tag, left, right, null);
		assertEquals(message, expected, rPred.rewrite(r));
	}

	@Test
	public void testEquality() {
		// E = E == true
		assertRelationalPredicate("E = E == ⊤", Lib.True, E, Predicate.EQUAL, E);

		// E /= E == false
		assertRelationalPredicate("E ≠ E == ⊥", Lib.False, E,
				Predicate.NOTEQUAL, E);

		// E |-> F = G |-> H == E = G & F = H
		Predicate pred1 = ff.makeRelationalPredicate(Expression.EQUAL, E, number1,
				null);
		Predicate pred2 = ff.makeRelationalPredicate(Expression.EQUAL, F, number2,
				null);
		AssociativePredicate expected = ff.makeAssociativePredicate(
				Predicate.LAND, new Predicate[] { pred1, pred2 }, null);
		BinaryExpression left = ff.makeBinaryExpression(Expression.MAPSTO, E,
				F, null);
		BinaryExpression right = ff.makeBinaryExpression(Expression.MAPSTO, number1,
				number2, null);
		assertRelationalPredicate("E ↦ F = G ↦ H == E = G ∧ F = H", expected,
				left, Predicate.EQUAL, right);
		
		// TRUE = FALSE  ==  false
		assertRelationalPredicate("TRUE = FALSE == ⊥", Lib.False, Lib.TRUE,
				Predicate.EQUAL, Lib.FALSE);

		// FALSE = TRUE  ==  false
		assertRelationalPredicate("FALSE = TRUE == ⊥", Lib.False, Lib.FALSE,
				Predicate.EQUAL, Lib.TRUE);
	}

	protected void assertAssociativeExpression(String message,
			Expression expected, int tag, Expression... expressions) {
		AssociativeExpression expression = ff.makeAssociativeExpression(tag,
				expressions, null);
		assertEquals(message, expected, expression.rewrite(r));
	}

	protected void assertBinaryExpression(String message, Expression expected,
			Expression left, int tag, Expression right) {
		BinaryExpression expression = ff.makeBinaryExpression(tag, left, right,
				null);
		
		assertEquals(message, expected, expression.rewrite(r));
	}

	protected void assertUnaryExpression(String message, Expression expected,
			int tag, Expression expression) {
		UnaryExpression uExp = ff.makeUnaryExpression(tag,
								expression, null);
		assertEquals(message, expected, uExp.rewrite(r));
	}

	@Test
	public void testSetTheory() {
		// S /\ ... /\ {} /\ ... /\ T == {}
		Expression expected = ff.makeAssociativeExpression(Expression.BINTER,
				new Expression[] { S, T, U }, null);
		assertAssociativeExpression("S ∩ ∅ == ∅", emptySet,
				Expression.BINTER, S, emptySet);
		assertAssociativeExpression("∅ ∩ S == ∅", emptySet,
				Expression.BINTER, emptySet, S);
		assertAssociativeExpression("S ∩ T ∩ U == S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, U);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ U == ∅", emptySet,
				Expression.BINTER, S, T, emptySet, U);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ U == ∅", emptySet,
				Expression.BINTER, emptySet, S, T, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ ∅ == ∅", emptySet,
				Expression.BINTER, S, T, U, emptySet);
		assertAssociativeExpression("S ∩ T ∩ ∅ ∩ U ∩ ∅ == ∅", emptySet,
				Expression.BINTER, S, T, emptySet, U, emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ U ∩ ∅ == ∅", emptySet,
				Expression.BINTER, emptySet, S, T, U, emptySet);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ U == ∅", emptySet,
				Expression.BINTER, emptySet, S, T, emptySet, U);
		assertAssociativeExpression("∅ ∩ S ∩ T ∩ ∅ ∩ U ∩ ∅ == ∅", emptySet,
				Expression.BINTER, emptySet, S, T, emptySet, U,
				emptySet);

		// S /\ ... /\ T /\ ... /\ T /\ ... /\ U == S /\ ... /\ T /\ ... /\ ...
		// /\ U
		assertAssociativeExpression("S ∩ S = S", S, Expression.BINTER, S, S);
		assertAssociativeExpression("S ∩ S ∩ T ∩ U = S ∩ T ∩ U", expected,
				Expression.BINTER, S, S, T, U);
		assertAssociativeExpression("S ∩ T ∩ S ∩ U = S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, S, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ S = S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, U, S);
		assertAssociativeExpression("S ∩ T ∩ U ∩ T = S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, U, T);
		assertAssociativeExpression("S ∩ T ∩ U ∩ U = S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, U, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ T ∩ U = S ∩ T ∩ U", expected,
				Expression.BINTER, S, T, U, T, U);

		// S \/ ... \/ {} \/ ... \/ T == S ... \/ ... \/ T
		expected = ff.makeAssociativeExpression(Expression.BUNION,
				new Expression[] { S, T, U }, null);
		assertAssociativeExpression("S ∪ ∅ == S", S, Expression.BUNION, S,
				emptySet);
		assertAssociativeExpression("∅ ∪ S == S", S, Expression.BUNION,
				emptySet, S);
		assertAssociativeExpression("S ∪ T ∪ U == S ∪ T ∪ U", expected,
				Expression.BUNION, S, T, U);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ U == S ∪ T ∪ U", expected,
				Expression.BUNION, S, T, emptySet, U);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ U == S ∪ T ∪ U", expected,
				Expression.BUNION, emptySet, S, T, U);
		assertAssociativeExpression("S ∪ T ∪ U ∪ ∅ == S ∪ T ∪ U", expected,
				Expression.BUNION, S, T, U, emptySet);
		assertAssociativeExpression("S ∪ T ∪ ∅ ∪ U ∪ ∅ == S ∪ T ∪ U", expected,
				Expression.BUNION, S, T, emptySet, U, emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ U ∪ ∅ == S ∪ T ∪ U", expected,
				Expression.BUNION, emptySet, S, T, U, emptySet);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ U == S ∪ T ∪ U", expected,
				Expression.BUNION, emptySet, S, T, emptySet, U);
		assertAssociativeExpression("∅ ∪ S ∪ T ∪ ∅ ∪ U ∪ ∅ == S ∪ T ∪ U",
				expected, Expression.BUNION, emptySet, S, T, emptySet,
				U, emptySet);

		// S \/ ... \/ T \/ ... \/ T \/ ... \/ U == S \/ ... \/ T \/ ... \/ ...
		// \/ U
		assertAssociativeExpression("S ∩ S = S", S, Expression.BUNION, S, S);
		assertAssociativeExpression("S ∩ S ∩ T ∩ U = S ∩ T ∩ U", expected,
				Expression.BUNION, S, S, T, U);
		assertAssociativeExpression("S ∩ T ∩ S ∩ U = S ∩ T ∩ U", expected,
				Expression.BUNION, S, T, S, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ S = S ∩ T ∩ U", expected,
				Expression.BUNION, S, T, U, S);
		assertAssociativeExpression("S ∩ T ∩ U ∩ T = S ∩ T ∩ U", expected,
				Expression.BUNION, S, T, U, T);
		assertAssociativeExpression("S ∩ T ∩ U ∩ U = S ∩ T ∩ U", expected,
				Expression.BUNION, S, T, U, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ T ∩ U = S ∩ T ∩ U", expected,
				Expression.BUNION, S, T, U, T, U);

		// {} <: S == true
		assertRelationalPredicate("∅ ⊆ S == ⊤", Lib.True, emptySet,
				Expression.SUBSETEQ, S);

		// S <: S == true
		assertRelationalPredicate("S ⊆ S == ⊤", Lib.True, S,
				Expression.SUBSETEQ, S);

		// E : {} == false
		assertRelationalPredicate("E ∈ ∅ == ⊥", Lib.False, E,
				Expression.IN, emptySet);

		// A : {A} == true
		assertRelationalPredicate("A ∈ {A} == ⊤", Lib.True, E,
				Expression.IN, ff.makeSetExtension(E, null));

		// B : {A, ..., B, ..., C} == true
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True, ff
				.makeIntegerLiteral(new BigInteger("1"), null), Expression.IN,
				U);
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True, ff
				.makeIntegerLiteral(new BigInteger("2"), null), Expression.IN,
				U);
		assertRelationalPredicate("B ∈ {A, ..., B, ..., C} == ⊤", Lib.True, ff
				.makeIntegerLiteral(new BigInteger("3"), null), Expression.IN,
				U);

		// {A, ..., B, ..., B, ..., C} == {A, ..., B, ..., C}
		assertSetExtension("{E, F, E} == {E, F}", ff.makeSetExtension(
				new Expression[] { E, F }, null), E, F, E);
		assertSetExtension("{E, F, F, E} == {E, F}", ff.makeSetExtension(
				new Expression[] { E, F }, null), E, F, F, E);
		assertSetExtension("{E, F, F} == {E, F}", ff.makeSetExtension(
				new Expression[] { E, F }, null), E, F, F);
		assertSetExtension("{E, E, F} == {E, F}", ff.makeSetExtension(
				new Expression[] { E, F}, null), E, E, F);
		assertSetExtension("{0, 1, 0} == {0, 1}", ff.makeSetExtension(
				new Expression[] { number0, number1 }, null),
				number0, number1, number0);
		assertSetExtension("{0, 1, 1, 0} == {0, 1}", ff.makeSetExtension(
				new Expression[] { number0, number1 }, null),
				number0, number1, number1, number0);
		assertSetExtension("{0, 1, 1} == {0, 1}", ff.makeSetExtension(
				new Expression[] { number0, number1 }, null),
				number0, number1, number1);
		assertSetExtension("{0, 0, 1} == {0, 1}", ff.makeSetExtension(
				new Expression[] { number0, number1 }, null),
				number0, number0, number1);
		assertSetExtension("{0, E, 1, 1, 0} == {0, E, 1}", ff.makeSetExtension(
				new Expression[] { number0, E, number1 },
				null), number0, E, number1, number1, number0);
		assertSetExtension("{0, E, 1, F, 1, 0} == {0, E, 1, F}", ff
				.makeSetExtension(new Expression[] { number0, E, number1, F },
						null), number0, E, number1, F, number1, number0);

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
		assertBinaryExpression("S ∖ S == ∅",
				emptySet, S, Expression.SETMINUS, S);
		assertBinaryExpression("T ∖ T == ∅",
				emptySet, T, Expression.SETMINUS, T);
		assertBinaryExpression("U ∖ U == ∅",
				emptySet, U, Expression.SETMINUS, U);

		// {} \ S == {}
		assertBinaryExpression("∅ ∖ S == ∅",
				emptySet, emptySet, Expression.SETMINUS, S);
		assertBinaryExpression("∅ ∖ T == ∅",
				emptySet, emptySet, Expression.SETMINUS, T);
		assertBinaryExpression("∅ ∖ U == ∅",
				emptySet, emptySet, Expression.SETMINUS, U);

		// S \ {} == S
		assertBinaryExpression("S ∖ ∅ == S",
				S, S, Expression.SETMINUS, emptySet);
		assertBinaryExpression("T ∖ ∅ == T",
				T, T, Expression.SETMINUS, emptySet);
		assertBinaryExpression("U ∖ ∅ == U",
				U, U, Expression.SETMINUS, emptySet);

		Expression m1 = ff.makeBinaryExpression(Expression.MAPSTO, number1,
				number2, null);
		Expression m2 = ff.makeBinaryExpression(Expression.MAPSTO, number2,
				number3, null);
		Expression m3 = ff.makeBinaryExpression(Expression.MAPSTO, number1,
				number1, null);
		Expression m4 = ff.makeBinaryExpression(Expression.MAPSTO, number2,
				number2, null);

		// r~~ == r
		Expression f = ff.makeSetExtension(new Expression[] { m1 }, null);
		assertUnaryExpression("f∼∼ = f", f, Expression.CONVERSE, ff
				.makeUnaryExpression(Expression.CONVERSE, f, null));
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		assertUnaryExpression("f∼∼ = f", f, Expression.CONVERSE, ff
				.makeUnaryExpression(Expression.CONVERSE, f, null));
		f = ff.makeSetExtension(new Expression[] { m1, m2, m3 }, null);
		assertUnaryExpression("f∼∼ = f", f, Expression.CONVERSE, ff
				.makeUnaryExpression(Expression.CONVERSE, f, null));

		// dom({x |-> a, ..., y |-> b}) == {x, ..., y}
		f = ff.makeSetExtension(new Expression[] { m1 }, null);
		assertUnaryExpression("", S, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		assertUnaryExpression("", T, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m3 }, null);
		assertUnaryExpression("", S, Expression.KDOM, f);
		f = ff.makeSetExtension(new Expression[] { m1, m2, m3, m4 }, null);
		assertUnaryExpression("", T, Expression.KDOM, f);

		// ran({x |-> a, ..., y |-> b}) == {a, ..., b}
		f = ff.makeSetExtension(new Expression[] { m3 }, null);
		assertUnaryExpression("", S, Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m3, m1 }, null);
		assertUnaryExpression("", T, Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m1, m4 }, null);
		assertUnaryExpression("", ff.makeSetExtension(
				new Expression[] { number2 }, null), Expression.KRAN, f);
		f = ff.makeSetExtension(new Expression[] { m3, m1, m2, m4 }, null);
		assertUnaryExpression("", U, Expression.KRAN, f);

		// (f <+ {E |-> F})(E) = F
		f = ff.makeSetExtension(new Expression[] { m1, m4 }, null);

		Expression g = ff.makeAssociativeExpression(Expression.OVR,
				new Expression[] { f, ff.makeSetExtension(m2, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", number3, g,
				Expression.FUNIMAGE, number2);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, number3, null), g, Expression.FUNIMAGE,
				number3);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m3, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", number1, g,
				Expression.FUNIMAGE, number1);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, number2, null), g, Expression.FUNIMAGE,
				number2);

		f = ff.makeSetExtension(new Expression[] { m1, m3 }, null);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m4, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", number2, g,
				Expression.FUNIMAGE, number2);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, number3, null), g, Expression.FUNIMAGE,
				number3);

		g = ff.makeAssociativeExpression(Expression.OVR, new Expression[] { f,
				ff.makeSetExtension(m1, null) }, null);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", number2, g,
				Expression.FUNIMAGE, number1);
		assertBinaryExpression("{f  {E ↦ F})(E) = F", ff.makeBinaryExpression(
				Expression.FUNIMAGE, g, number2, null), g, Expression.FUNIMAGE,
				number2);

		// E : {F} == E = F (if F is a single expression)
		assertRelationalPredicate(
				"E ∈ {F} == E = F   if F is a single expression", ff
						.makeRelationalPredicate(Predicate.EQUAL, E,
								F, null), E, Predicate.IN, ff
						.makeSetExtension(F, null));

		// {E} = {F} == E = F if E, F is a single expression
		assertRelationalPredicate(
				"{E} = {F} == E = F   if E, F is a single expression", ff
						.makeRelationalPredicate(Predicate.EQUAL, E,
								F, null),
				ff.makeSetExtension(E, null), Predicate.EQUAL, ff
						.makeSetExtension(F, null));
		
		// {x |-> a, ..., y |-> b}~  ==  {a |-> x, ..., b |-> y}
		Expression number0ToE = ff.makeBinaryExpression(Expression.MAPSTO, number0, E, null);
		Expression eToNumber0 = ff.makeBinaryExpression(Expression.MAPSTO, E, number0, null);
		Expression number0ToF = ff.makeBinaryExpression(Expression.MAPSTO, number0, F, null);
		Expression fToNumber0 = ff.makeBinaryExpression(Expression.MAPSTO, F, number0, null);
		Expression number0ToNumber1 = ff.makeBinaryExpression(
				Expression.MAPSTO, number0, number1, null);
		Expression number1ToNumber0 = ff.makeBinaryExpression(
				Expression.MAPSTO, number1, number0, null);

		assertUnaryExpression("{0 ↦ E}∼ == {E ↦ 0}", ff.makeSetExtension(
				new Expression[] { eToNumber0 }, null), Expression.CONVERSE, ff
				.makeSetExtension(new Expression[] { number0ToE }, null));
		assertUnaryExpression("{0 ↦ E, 0 ↦ F}∼ == {E ↦ 0, F ↦ 0}", ff
				.makeSetExtension(new Expression[] { eToNumber0, fToNumber0 },
						null), Expression.CONVERSE, ff.makeSetExtension(
				new Expression[] { number0ToE, number0ToF }, null));
		assertUnaryExpression(
				"{0 ↦ E, 0 ↦ F, 0 ↦ 1}∼ == {E ↦ 0, F ↦ 0, 1 ↦ 0}", ff
						.makeSetExtension(new Expression[] { eToNumber0,
								fToNumber0, number1ToNumber0 }, null),
				Expression.CONVERSE, ff.makeSetExtension(new Expression[] {
						number0ToE, number0ToF, number0ToNumber1 }, null));
		
		// Typ = {} == false (where Typ is a type expression) is NOT done here
		assertRelationalPredicate("ℤ = ∅ == ℤ = ∅", ff.makeRelationalPredicate(
				Predicate.EQUAL, integer, emptySet, null), integer,
				Expression.EQUAL, emptySet);
		assertRelationalPredicate("ℙ(ℤ) = ∅ == ℙ(ℤ) = ∅", ff.makeRelationalPredicate(
				Predicate.EQUAL, powInteger, emptySet, null), powInteger,
				Expression.EQUAL, emptySet);

		// {} = Typ == false (where Typ is a type expression) is NOT done here
		assertRelationalPredicate("∅ = ℤ == ∅ = ℤ", ff.makeRelationalPredicate(
				Predicate.EQUAL, emptySet, integer, null), emptySet,
				Expression.EQUAL, integer);
		assertRelationalPredicate("∅ = ℙ(ℤ) == ∅ = ℙ(ℤ)", ff.makeRelationalPredicate(
				Predicate.EQUAL, emptySet, powInteger, null), emptySet,
				Expression.EQUAL, powInteger);

		// E : Typ == true (where Typ is a type expression) is NOT done here
		assertRelationalPredicate("E ∈ ℤ == E ∈ ℤ", ff.makeRelationalPredicate(
				Predicate.IN, E, integer, null), E, Expression.IN,
				integer);
		assertRelationalPredicate("F ∈ ℤ == F ∈ ℤ", ff.makeRelationalPredicate(
				Predicate.IN, F, integer, null), F, Expression.IN,
				integer);
		assertRelationalPredicate("S ∈ ℙ(ℤ) == S ∈ ℙ(ℤ)", ff.makeRelationalPredicate(
				Predicate.IN, S, powInteger, null), S, Expression.IN,
				powInteger);
		assertRelationalPredicate("T ∈ ℙ(ℤ) == T ∈ ℙ(ℤ)", ff.makeRelationalPredicate(
				Predicate.IN, T, powInteger, null), T, Expression.IN,
				powInteger);
		assertRelationalPredicate("U ∈ ℙ(ℤ) == U ∈ ℙ(ℤ)", ff.makeRelationalPredicate(
				Predicate.IN, U, powInteger, null), U, Expression.IN,
				powInteger);
		
		// f(f~(E)) == E
		Expression gConverse = ff.makeUnaryExpression(Expression.CONVERSE, g, null);
		assertBinaryExpression("g(g∼(E)) = E", number2, g,
				Expression.FUNIMAGE, ff.makeBinaryExpression(
						Expression.FUNIMAGE, gConverse, number2, null));

		// f(f~(E)) == E
		assertBinaryExpression("g∼(g(E)) = E", number3, gConverse,
				Expression.FUNIMAGE, ff.makeBinaryExpression(
						Expression.FUNIMAGE, g, number3, null));

		// {x |-> a, ..., y |-> b}({a |-> x, ..., b |-> y}(E)) = E
		f = ff.makeSetExtension(new Expression[] { m1, m2 }, null);
		Expression fConverse = ff.makeUnaryExpression(Expression.CONVERSE, f, null);
		assertBinaryExpression("f(f∼(E)) = E", number2, f,
				Expression.FUNIMAGE, ff.makeBinaryExpression(
						Expression.FUNIMAGE, fConverse, number2, null));

		// p;...;{};...;q == {}
		Expression exp1 = Lib.parseExpression("{1 ↦ 2}");
		exp1.typeCheck(ff.makeTypeEnvironment());
		Expression exp2 = Lib.parseExpression("{2 ↦ 3}");
		exp2.typeCheck(ff.makeTypeEnvironment());
		Expression emptySet = ff.makeEmptySet(exp1.getType(), null);
		assertAssociativeExpression("p; ... ;∅; ... ;q  = ∅", ff.makeEmptySet(
				exp1.getType(), null), Expression.BCOMP, exp1, exp2, emptySet);
		assertAssociativeExpression("p; ... ;∅; ... ;q  = ∅", ff.makeEmptySet(
				exp1.getType(), null), Expression.BCOMP, emptySet, exp1, exp2);
		assertAssociativeExpression("p; ... ;∅; ... ;q  = ∅", ff.makeEmptySet(
				exp1.getType(), null), Expression.BCOMP, exp1, emptySet, exp2);
		
		// U \ (U \ S) == S
		Expression uMinusS = ff.makeBinaryExpression(Expression.SETMINUS,
				integer, S, null);
		Expression uMinusT = ff.makeBinaryExpression(Expression.SETMINUS,
				integer, T, null);
		Expression uMinusU = ff.makeBinaryExpression(Expression.SETMINUS,
				integer, U, null);
		assertBinaryExpression("ℤ ∖ (ℤ ∖ S) == S", S, integer,
				Expression.SETMINUS, uMinusS);
		assertBinaryExpression("ℤ ∖ (ℤ ∖ T) == T", T, integer,
				Expression.SETMINUS, uMinusT);
		assertBinaryExpression("ℤ ∖ (ℤ ∖ U) == U", U, integer,
				Expression.SETMINUS, uMinusU);

		// S \ U == {}
		emptySet = ff.makeEmptySet(S.getType(), null);
		assertBinaryExpression("S ∖ ℤ == ∅", emptySet, S, Expression.SETMINUS,
				integer);
		assertBinaryExpression("T ∖ ℤ == ∅", emptySet, T, Expression.SETMINUS,
				integer);
		assertBinaryExpression("U ∖ ℤ == ∅", emptySet, U, Expression.SETMINUS,
				integer);
		
		// S \/ ... \/ U \/ ... \/ T == U
		assertAssociativeExpression("S ∪ T ∪ ℤ ∪ U == ℤ", integer,
				Expression.BUNION, S, T, integer, U);
		assertAssociativeExpression("ℤ ∪ S ∪ T ∪ U == ℤ", integer,
				Expression.BUNION, integer, S, T, U);
		assertAssociativeExpression("S ∪ T ∪ U ∪ ℤ == ℤ", integer,
				Expression.BUNION, S, T, U, integer);

		// S /\ ... /\ U /\ ... /\ T == S /\ ... /\ ... /\ T
		Expression resultExp = ff.makeAssociativeExpression(Expression.BINTER,
				new Expression[] { S, T, U }, null);
		assertAssociativeExpression("S ∩ T ∩ ℤ ∩ U == S ∩ T ∩ U", resultExp,
				Expression.BINTER, S, T, integer, U);
		assertAssociativeExpression("ℤ ∩ S ∩ T ∩ U == S ∩ T ∩ U", resultExp,
				Expression.BINTER, integer, S, T, U);
		assertAssociativeExpression("S ∩ T ∩ U ∩ ℤ == S ∩ T ∩ U", resultExp,
				Expression.BINTER, S, T, U, integer);
	}

	private void assertSetExtension(String message, Expression expected,
			Expression... expressions) {
		SetExtension setExtension = ff.makeSetExtension(expressions, null);
		assertEquals(expected, setExtension.rewrite(r));
	}

	@Test
	public void testArithmetic() {
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
		assertBinaryExpression("0 − E == −E", uNumberMinus1, number0,
				Expression.MINUS, number1);

		// -(-E) == E
		assertUnaryExpression("−(−E) = E", E, Expression.UNMINUS,
				ff.makeUnaryExpression(Expression.UNMINUS, E, null));

		assertUnaryExpression("−(−i) = i", number1, Expression.UNMINUS,
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

		// E / E == 1
		assertBinaryExpression("2 ÷ 2 = 1", number1, number2, Expression.DIV,
				number2);
		assertBinaryExpression("E ÷ E = 1", number1, E, Expression.DIV,
				E);

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

		// (X * ... * E * ... * Y)/E == X * ... * Y
		assertBinaryExpression("(E * 2) / E = 2", number2, ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						E, number2 }, null), Expression.DIV, E);
		assertBinaryExpression("(2 * E) / E = 2", number2, ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						number2, E }, null), Expression.DIV, E);		
		assertBinaryExpression("(E * 2) / 2 = E", E, ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						E, number2 }, null), Expression.DIV, number2);
		assertBinaryExpression("(2 * E) / 2 = E", E, ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						number2, E }, null), Expression.DIV, number2);		
		assertBinaryExpression("(E * 2 * E) / E = 2 * E", ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						number2, E }, null), ff.makeAssociativeExpression(
				Expression.MUL, new Expression[] { E, number2, E }, null),
				Expression.DIV, E);
		assertBinaryExpression("(E * 2 * E) / 2 = E * E", ff
				.makeAssociativeExpression(Expression.MUL, new Expression[] {
						E, E }, null), ff.makeAssociativeExpression(
				Expression.MUL, new Expression[] { E, number2, E }, null),
				Expression.DIV, number2);

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
		
		// -(i) == (-i) where i is a literal
		assertUnaryExpression("−(1) == (−1)", numberMinus1, Expression.UNMINUS, number1);

		// -(-i) == i where i is a literal
		assertUnaryExpression("−(−1) == 1", number1, Expression.UNMINUS, numberMinus1);
		
		// i = j == true   or   i = j == false  (by computation)
		assertRelationalPredicate("1 = 1 == ⊤", Lib.True, number1, Predicate.EQUAL, number1);
		assertRelationalPredicate("1 = 2 == ⊥", Lib.False, number1, Predicate.EQUAL, number2);
		assertRelationalPredicate("1 = −1 == ⊥", Lib.False, number1, Predicate.EQUAL, numberMinus1);
		assertRelationalPredicate("−1 = −1 == ⊤", Lib.True, numberMinus1, Predicate.EQUAL, numberMinus1);
		assertRelationalPredicate("−1 = −2 == ⊥", Lib.False, numberMinus1, Predicate.EQUAL, numberMinus2);
		assertRelationalPredicate("−1 = 1 == ⊥", Lib.False, numberMinus1, Predicate.EQUAL, number1);

		// i <= j == true   or   i <= j == false  (by computation)
		assertRelationalPredicate("1 ≤ 1 == ⊤", Lib.True, number1, Predicate.LE, number1);
		assertRelationalPredicate("1 ≤ 2 == ⊤", Lib.True, number1, Predicate.LE, number2);
		assertRelationalPredicate("1 ≤ −1 == ⊥", Lib.False, number1, Predicate.LE, numberMinus1);
		assertRelationalPredicate("−1 ≤ −1 == ⊤", Lib.True, numberMinus1, Predicate.LE, numberMinus1);
		assertRelationalPredicate("−1 ≤ −2 == ⊥", Lib.False, numberMinus1, Predicate.LE, numberMinus2);
		assertRelationalPredicate("−1 ≤ 1 == ⊤", Lib.True, numberMinus1, Predicate.LE, number1);

		// i < j == true   or   i < j == false  (by computation)
		assertRelationalPredicate("1 < 1 == ⊥", Lib.False, number1, Predicate.LT, number1);
		assertRelationalPredicate("1 < 2 == ⊤", Lib.True, number1, Predicate.LT, number2);
		assertRelationalPredicate("1 < −1 == ⊥", Lib.False, number1, Predicate.LT, numberMinus1);
		assertRelationalPredicate("−1 < −1 == ⊥", Lib.False, numberMinus1, Predicate.LT, numberMinus1);
		assertRelationalPredicate("−1 < −2 == ⊥", Lib.False, numberMinus1, Predicate.LT, numberMinus2);
		assertRelationalPredicate("−1 < 1 == ⊤", Lib.True, numberMinus1, Predicate.LT, number1);

		// i >= j == true   or   i >= j == false  (by computation)
		assertRelationalPredicate("1 ≥ 1 == ⊤", Lib.True, number1, Predicate.GE, number1);
		assertRelationalPredicate("1 ≥ 2 == ⊥", Lib.False, number1, Predicate.GE, number2);
		assertRelationalPredicate("1 ≥ −1 == ⊤", Lib.True, number1, Predicate.GE, numberMinus1);
		assertRelationalPredicate("−1 ≥ −1 == ⊤", Lib.True, numberMinus1, Predicate.GE, numberMinus1);
		assertRelationalPredicate("−1 ≥ −2 == ⊤", Lib.True, numberMinus1, Predicate.GE, numberMinus2);
		assertRelationalPredicate("−1 ≥ 1 == ⊥", Lib.False, numberMinus1, Predicate.GE, number1);

		// i > j == true   or   i > j == false  (by computation)
		assertRelationalPredicate("1 > 1 == ⊥", Lib.False, number1, Predicate.GT, number1);
		assertRelationalPredicate("1 > 2 == ⊥", Lib.False, number1, Predicate.GT, number2);
		assertRelationalPredicate("1 > −1 == ⊤", Lib.True, number1, Predicate.GT, numberMinus1);
		assertRelationalPredicate("−1 > −1 == ⊥", Lib.False, numberMinus1, Predicate.GT, numberMinus1);
		assertRelationalPredicate("−1 > −2 == ⊤", Lib.True, numberMinus1, Predicate.GT, numberMinus2);
		assertRelationalPredicate("−1 > 1 == ⊥", Lib.False, numberMinus1, Predicate.GT, number1);
		
		// E <= E = true
		assertRelationalPredicate("E ≤ E == ⊤", Lib.True, E, Predicate.LE, E);
		assertRelationalPredicate("F ≤ F == ⊤", Lib.True, F, Predicate.LE, F);

		// E >= E = true
		assertRelationalPredicate("E ≥ E == ⊤", Lib.True, E, Predicate.GE, E);
		assertRelationalPredicate("F ≥ F == ⊤", Lib.True, F, Predicate.GE, F);

		// E < E = false
		assertRelationalPredicate("E < E == ⊥", Lib.False, E, Predicate.LT, E);
		assertRelationalPredicate("F < F == ⊥", Lib.False, F, Predicate.LT, F);

		// E > E = false
		assertRelationalPredicate("E > E == ⊥", Lib.False, E, Predicate.GT, E);
		assertRelationalPredicate("F > F == ⊥", Lib.False, F, Predicate.GT, F);
	}

	@Test
	public void testFinite() {
		// finite({}) == true
		assertSimplePredicate("finite(∅) = ⊤", Lib.True, Predicate.KFINITE, ff
				.makeEmptySet(ff.makePowerSetType(ff.makeGivenType("SET")),
						null));
		
		// finite({a, ..., b}) == true
		assertSimplePredicate("finite(S) = ⊤", Lib.True, Predicate.KFINITE, S);
		assertSimplePredicate("finite(T) = ⊤", Lib.True, Predicate.KFINITE, T);
		assertSimplePredicate("finite(U) = ⊤", Lib.True, Predicate.KFINITE, U);
		
		// finite(S \/ ... \/ T) == finite(S) \/ ... \/ finite(T)
		Expression setA = Lib.parseExpression("{x ∣ x > 0}");
		Expression setB = Lib.parseExpression("{y ∣ y < 0}");
		Expression setC = Lib.parseExpression("{z ∣ z = 0}");
		setA.typeCheck(ff.makeTypeEnvironment());
		setB.typeCheck(ff.makeTypeEnvironment());
		setC.typeCheck(ff.makeTypeEnvironment());
		SimplePredicate finiteA = ff.makeSimplePredicate(Predicate.KFINITE,
				setA, null);
		SimplePredicate finiteB = ff.makeSimplePredicate(Predicate.KFINITE,
				setB, null);
		SimplePredicate finiteC = ff.makeSimplePredicate(Predicate.KFINITE,
				setC, null);
		Predicate expected = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { finiteA, finiteB }, null);
		Expression aExp = ff.makeAssociativeExpression(Expression.BUNION,
				new Expression[] { setA, setB }, null);
		assertSimplePredicate("finite(A ∪ B) = finite(A) ∧ finite(B)",
				expected, Predicate.KFINITE, aExp);
		expected = ff.makeAssociativePredicate(Predicate.LAND,
				new Predicate[] { finiteA, finiteB, finiteC }, null);
		aExp = ff.makeAssociativeExpression(Expression.BUNION, new Expression[] {
				setA, setB, setC }, null);
		assertSimplePredicate(
				"finite(A ∪ B ∪ C) = finite(A) ∧ finite(B) ∧ finite(C)",
				expected, Predicate.KFINITE, aExp);
		
		// finite(POW(S)) == finite(S)
		assertSimplePredicate("finite(ℙ(S)) == finite(S)", ff
				.makeSimplePredicate(Predicate.KFINITE, integer, null),
				Predicate.KFINITE, powInteger);
		
		// finite(S ** T) == S = {} or T = {} or (finite(S) & finite(T))
		expected = Lib.parsePredicate(setA + "= ∅ ∨ " + setB + "= ∅ ∨ (finite("
				+ setA + ") ∧ finite(" + setB + "))");
		expected.typeCheck(ff.makeTypeEnvironment());
		assertSimplePredicate(
				"finite(A × B) == A = ∅ ∨ B = ∅ ∨ (finite(A) ∧ finite(B))",
				expected, Predicate.KFINITE, ff.makeBinaryExpression(
						Expression.CPROD, setA, setB, null));
		
		// finite(r~) == finite(r)
		Expression r = Lib.parseExpression("{x ↦ y ∣ x > 0 ∧ y < 2}");
		r.typeCheck(ff.makeTypeEnvironment());
		assertSimplePredicate("finite(r∼) == finite(r)", ff
				.makeSimplePredicate(Predicate.KFINITE, r, null),
				Predicate.KFINITE, ff.makeUnaryExpression(Expression.CONVERSE,
						r, null));
		
		// finite(a..b) == true
		assertSimplePredicate("finite(1‥2) == ⊤", Lib.True,
				Predicate.KFINITE, Lib.parseExpression("1‥2"));
	}

	private void assertSimplePredicate(String message, Predicate expected,
			int tag, Expression expression) {
		SimplePredicate sPred = ff.makeSimplePredicate(tag, expression, null);
		assertEquals(message, expected, sPred.rewrite(r));
	}

	@Test
	public void testCardinality() {
		// card({}) == 0
		assertUnaryExpression("card(∅) == 0", number0, Expression.KCARD, ff
				.makeEmptySet(S.getType(), null));
		
		
		// card({E}) == 1
		assertUnaryExpression("card({E}) == 1", number1, Expression.KCARD, S);
		
		// card(POW(S)) == 2^card(S)
		Expression powA = Lib.parseExpression("ℙ({x ∣ x > 0})");
		Expression expected = Lib.parseExpression(" 2^(card({x ∣ x > 0}))");
		powA.typeCheck(ff.makeTypeEnvironment());
		expected.typeCheck(ff.makeTypeEnvironment());
		assertUnaryExpression("card(ℙ(A)) == 2^(card(A))", expected,
				Expression.KCARD, powA);
		
		// card(S ** T) == card(S) * card(T)
		Expression aCrossB = Lib.parseExpression("{x ∣ x > 0} × {y ∣ y < 0}");
		expected = Lib.parseExpression("card({x ∣ x > 0}) ∗ card({y ∣ y < 0})");
		aCrossB.typeCheck(ff.makeTypeEnvironment());
		expected.typeCheck(ff.makeTypeEnvironment());
		assertUnaryExpression("card(A × B) == card(A) ∗ card(B)", expected,
				Expression.KCARD, aCrossB);
		
		// card(S \ T) == card(S) - card(S /\ T)
		Expression input = Lib.parseExpression("{x ∣ x > 0} ∖ {y ∣ y < 0}");
		expected = Lib
				.parseExpression("card({x ∣ x > 0}) − card({x ∣ x > 0} ∩ {y ∣ y < 0})");
		input.typeCheck(ff.makeTypeEnvironment());
		expected.typeCheck(ff.makeTypeEnvironment());
		assertUnaryExpression("card(A ∖ B) == card(A) − card(A ∩ B)", expected,
				Expression.KCARD, input);
		
		// card(S) = 0  ==  S = {}
		input = Lib.parseExpression("card({x ∣ x > 0})");
		Predicate expectedPred = Lib.parsePredicate("{x ∣ x > 0} = ∅");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) = 0  ==  S = ∅", expectedPred,
				input, Predicate.EQUAL, number0);

		// 0 = card(S)  ==  S = {}
		assertRelationalPredicate("0 = card(S)  ==  S = ∅", expectedPred,
				number0, Predicate.EQUAL, input);

		// not(card(S) = 0)  ==  not(S = {})
		Predicate inputPred = Lib.parsePredicate("card({x ∣ x > 0}) = 0");
		expectedPred = Lib.parsePredicate("¬({x ∣ x > 0} = ∅)");
		inputPred.typeCheck(ff.makeTypeEnvironment());
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertUnaryPredicate("card(S) ≠ 0  ==  ¬(S = ∅)", expectedPred,
				Predicate.NOT, inputPred);

		// not(0 = card(S))  ==  not(S = {})
		inputPred = Lib.parsePredicate("0 = card({x ∣ x > 0})");
		inputPred.typeCheck(ff.makeTypeEnvironment());
		assertUnaryPredicate("0 ≠ card(S)  ==  ¬(S = ∅)", expectedPred,
				Predicate.NOT, inputPred);

		// card(S) > 0  ==  not(S = {})
		input = Lib.parseExpression("card({x ∣ x > 0})");
		expectedPred = Lib.parsePredicate("¬({x ∣ x > 0} = ∅)");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) > 0  ==  ¬(S = ∅)", expectedPred,
				input, Predicate.GT, number0);

		// not(0 = card(S))  ==  not(S = {})
		assertRelationalPredicate("0 < card(S)  ==  ¬(S = ∅)", expectedPred,
				number0, Predicate.LT, input);

		// card(S) = 1 == #x.S = {x}
		input = Lib.parseExpression("card({x ∣ x > 0})");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred = Lib.parsePredicate("∃y·{x ∣ x > 0} = {y}");
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) = 1  ==  ∃x·S = {x}", expectedPred,
				input, Predicate.EQUAL, number1);
		
		input = Lib.parseExpression("card({x ↦ (y ↦ z) ∣ x > 0 ∧ y ∈ BOOL ∧ z < 0})");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred = Lib.parsePredicate("∃x1,x2,x3·{x ↦ (y ↦ z) ∣ x > 0 ∧ y ∈ BOOL ∧ z < 0} = {x1 ↦ (x2 ↦ x3)}");
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) = 1  ==  ∃x·S = {x}", expectedPred,
				input, Predicate.EQUAL, number1);

		// 1 = card(S) == #x.S = {x}
		input = Lib.parseExpression("card({x ∣ x > 0})");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred = Lib.parsePredicate("∃y·{x ∣ x > 0} = {y}");
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) = 1  ==  ∃x·S = {x}", expectedPred,
				number1, Predicate.EQUAL, input);
		
		input = Lib.parseExpression("card({x ↦ (y ↦ z) ∣ x > 0 ∧ y ∈ BOOL ∧ z < 0})");
		input.typeCheck(ff.makeTypeEnvironment());
		expectedPred = Lib.parsePredicate("∃x1,x2,x3·{x ↦ (y ↦ z) ∣ x > 0 ∧ y ∈ BOOL ∧ z < 0} = {x1 ↦ (x2 ↦ x3)}");
		expectedPred.typeCheck(ff.makeTypeEnvironment());
		assertRelationalPredicate("card(S) = 1  ==  ∃x·S = {x}", expectedPred,
				number1, Predicate.EQUAL, input);

	}
}
