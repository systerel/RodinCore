package org.eventb.core.ast.tests;

import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_ASSOCIATIVE_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_ATOMIC_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_BINARY_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_LITERAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_QUANTIFIED_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_RELATIONAL_PREDICATE;
import static org.eventb.core.ast.Formula.FIRST_UNARY_EXPRESSION;
import static org.eventb.core.ast.Formula.FIRST_UNARY_PREDICATE;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.ITestHelper.ASSOCIATIVE_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.ASSOCIATIVE_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.ATOMIC_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.BINARY_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.BINARY_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.LITERAL_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.QUANTIFIED_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.QUANTIFIED_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.RELATIONAL_PREDICATE_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.UNARY_EXPRESSION_LENGTH;
import static org.eventb.core.ast.tests.ITestHelper.UNARY_PREDICATE_LENGTH;

import java.math.BigInteger;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;

/**
 * Creates sets of all predicates or expressions to be used in unit tests.
 * 
 * @author franz
 */
public class Common {
	
	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public static final BigInteger ONE = BigInteger.ONE;
	public static final BigInteger TWO = BigInteger.valueOf(2);
	public static final BigInteger FIVE = BigInteger.valueOf(5);
	public static final BigInteger MINUS_FIVE = BigInteger.valueOf(-5);
	
	/**
	 * Creates an array of all possible expressions (except bound identifiers).
	 * 
	 * @return an array of all expressions
	 */
	public static Expression[] constructExpressions() {
		Expression[] expressions = new Expression[ASSOCIATIVE_EXPRESSION_LENGTH
		                                          + BINARY_EXPRESSION_LENGTH
		                                          + ATOMIC_EXPRESSION_LENGTH
		                                          + 1 // BoolExpression
		                                          + 1 // IntegerLiteral
		                                          + QUANTIFIED_EXPRESSION_LENGTH 
		                                          + 1 // SetExtension
		                                          + UNARY_EXPRESSION_LENGTH 
		                                          + 1 // FreeIdentifier
		                                          ];
		FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
		BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
		FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
		LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null);
		IntegerLiteral two = ff.makeIntegerLiteral(TWO, null);

		int l = 0;
		for (int i = 0; i < ASSOCIATIVE_EXPRESSION_LENGTH; i++) {
			expressions[l++] = ff.makeAssociativeExpression(
					i + FIRST_ASSOCIATIVE_EXPRESSION,
					mList(id_y, id_x), null);
		}
		for (int i = 0; i < BINARY_EXPRESSION_LENGTH; i++) {
			expressions[l++] = ff.makeBinaryExpression(
					i + FIRST_BINARY_EXPRESSION,
					id_x, id_x, null);
		}
		for (int i = 0; i < ATOMIC_EXPRESSION_LENGTH; i++) {
			expressions[l++] = ff.makeAtomicExpression(
					i + FIRST_ATOMIC_EXPRESSION, null);
		}
		expressions[l++] = ff.makeBoolExpression(btrue, null);
		expressions[l++] = ff.makeIntegerLiteral(ONE, null);
		for (int i = 0; i < QUANTIFIED_EXPRESSION_LENGTH; i++) {
			expressions[l++] = ff.makeQuantifiedExpression(
					i + FIRST_QUANTIFIED_EXPRESSION,
					mList(bd_x), btrue, two, null,
					QuantifiedExpression.Form.Explicit);
		}
		expressions[l++] = ff.makeSetExtension(mList(id_x),
				null);
		for (int i = 0; i < UNARY_EXPRESSION_LENGTH; i++) {
			expressions[l++] = ff.makeUnaryExpression(
					i + FIRST_UNARY_EXPRESSION,
					id_x, null);
		}
		//expressions[l++] = formulaFactory.makeBoundIdentifier(0,Formula.FREE_IDENT,null);
		expressions[l++] = id_x;
		return expressions;
	}
	
	/**
	 * Creates an array of all possible predicates (except bound identifiers).
	 * 
	 * @return an array of all predicates
	 */
	public static Predicate[] constructPredicates() {
		Predicate[] predicates = new Predicate[ASSOCIATIVE_PREDICATE_LENGTH
		 		                                   + BINARY_PREDICATE_LENGTH
		 		                                   + LITERAL_PREDICATE_LENGTH
		 		                                   + QUANTIFIED_PREDICATE_LENGTH
		 		                                   + RELATIONAL_PREDICATE_LENGTH + 1
		 		                                   + UNARY_PREDICATE_LENGTH];
		BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
		LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null); 
		IntegerLiteral two = ff.makeIntegerLiteral(TWO, null);

		int l = 0;
		for (int i = 0; i < BINARY_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeBinaryPredicate(
					i + FIRST_BINARY_PREDICATE,
					btrue, btrue, null);
		}
		for (int i = 0; i < LITERAL_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeLiteralPredicate(
					i + FIRST_LITERAL_PREDICATE, null);
		}
		predicates[l++] = ff.makeSimplePredicate(Formula.KFINITE,
				two, null);
		for (int i = 0; i < QUANTIFIED_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeQuantifiedPredicate(
					i + FIRST_QUANTIFIED_PREDICATE,
					mList(bd_x), btrue, null);
		}
		for (int i = 0; i < RELATIONAL_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeRelationalPredicate(
					i + FIRST_RELATIONAL_PREDICATE,
					two, two, null);
		}
		for (int i = 0; i < UNARY_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeUnaryPredicate(
					i + FIRST_UNARY_PREDICATE,
					btrue, null);
		}
		for (int i = 0; i < ASSOCIATIVE_PREDICATE_LENGTH; i++) {
			predicates[l++] = ff.makeAssociativePredicate(
					i + FIRST_ASSOCIATIVE_PREDICATE,
					mList(btrue, btrue), null);
		}
		return predicates;
	}
	
}
