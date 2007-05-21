package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewriterImpl;
import org.junit.Before;
import org.junit.Test;

public class TypeRewriterTests {

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
	
	protected static final Expression E = Lib.parseExpression("x ∗ 2");

	protected static final Expression F = Lib.parseExpression("y ∗ 3");
	
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
		r = new TypeRewriterImpl();
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

	protected void assertRelationalPredicate(String message, Predicate expected,
			Expression left, int tag, Expression right) {
		RelationalPredicate rPred = ff.makeRelationalPredicate(
								tag, left, right, null);
		assertEquals(message, expected, rPred.rewrite(r));
	}

	@Test
	public void testTypeRewrites() {
		// Typ = {} == false (where Typ is a type expression)
		assertRelationalPredicate("ℤ = ∅ == ⊥", Lib.False, powInteger,
				Expression.EQUAL, emptySet);
		assertRelationalPredicate("ℙ(ℤ) = ∅ == ⊥", Lib.False, powInteger,
				Expression.EQUAL, emptySet);

		// {} = Typ == false (where Typ is a type expression)
		assertRelationalPredicate("∅ = ℤ == ⊥", Lib.False, emptySet,
				Expression.EQUAL, integer);
		assertRelationalPredicate("∅ = ℙ(ℤ) == ⊥", Lib.False, emptySet,
				Expression.EQUAL, powInteger);

		// E : Typ == true (where Typ is a type expression)
		assertRelationalPredicate("E ∈ ℤ == ⊤", Lib.True, E, Expression.IN,
				integer);
		assertRelationalPredicate("F ∈ ℤ == ⊤", Lib.True, F, Expression.IN,
				integer);
		assertRelationalPredicate("S ∈ ℙ(ℤ) == ⊤", Lib.True, S, Expression.IN,
				powInteger);
		assertRelationalPredicate("T ∈ ℙ(ℤ) == ⊤", Lib.True, T, Expression.IN,
				powInteger);
		assertRelationalPredicate("U ∈ ℙ(ℤ) == ⊤", Lib.True, U, Expression.IN,
				powInteger);
	}
}
