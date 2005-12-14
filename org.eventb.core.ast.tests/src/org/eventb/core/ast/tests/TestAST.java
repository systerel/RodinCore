package org.eventb.core.ast.tests;

import junit.framework.TestCase;

import static org.eventb.core.ast.Formula.*;
import static org.eventb.core.ast.tests.ITestHelper.*;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * Test the abstract syntax tree.
 * <p>
 * Test that an abstract syntax tree node is constructed correctly.
 * For all kinds of operators, tests that the constructed node and his
 * accessors are correct.
 * 
 * @author François Terrier
 *
 */
public class TestAST extends TestCase {
	
	private FormulaFactory ff = FormulaFactory.getDefault();

	private FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	private FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);

	private BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
	private BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z", null);
	
	private LiteralPredicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, null);
	
	private IntegerLiteral two = ff.makeIntegerLiteral(Common.TWO,null);
	
	/**
	 * Tests if the constructed node's children returned by the accessors are
	 * the same that the one specified when constructing the node.
	 */
	public void testAST() {
		Expression[] expressions = Common.constructExpressions();
		Predicate[] predicates = Common.constructPredicates();
		for (int i = 0; i < ASSOCIATIVE_EXPRESSION_LENGTH; i++) {
			for (int j = 0; j < expressions.length; j++) {
				AssociativeExpression expr = ff.makeAssociativeExpression(i+FIRST_ASSOCIATIVE_EXPRESSION,
						new Expression[]{expressions[j],id_x},null);
				assertEquals(expr.getChildren()[0], expressions[j]);
			}
		}
		for (int i = 0; i < ASSOCIATIVE_PREDICATE_LENGTH; i++) {
			for (int j = 0; j < predicates.length; j++) {
				AssociativePredicate expr = ff.makeAssociativePredicate(i + FIRST_ASSOCIATIVE_PREDICATE, 
						new Predicate[] { predicates[j], btrue }, null);
				assertEquals(expr.getChildren()[0], predicates[j]);
			}
		}
		for (int i = 0; i < BINARY_EXPRESSION_LENGTH; i++) {
			for (int j = 0; j < expressions.length; j++) {
				BinaryExpression expr = ff.makeBinaryExpression(i + FIRST_BINARY_EXPRESSION,
						expressions[j], id_x, null);
				assertEquals(expr.getLeft(),expressions[j]);
				BinaryExpression expr1 = ff.makeBinaryExpression(i + FIRST_BINARY_EXPRESSION,
						id_x, expressions[j], null);
				assertEquals(expr1.getRight(),expressions[j]);
			}
		}
		for (int i = 0; i < BINARY_PREDICATE_LENGTH; i++) {
			for (int j = 0; j < predicates.length; j++) {
				BinaryPredicate pred = ff.makeBinaryPredicate(i + FIRST_BINARY_PREDICATE,
						predicates[j], btrue, null);
				assertEquals(pred.getLeft(),predicates[j]);
				BinaryPredicate expr2 = ff.makeBinaryPredicate(i + FIRST_BINARY_PREDICATE,
						btrue, predicates[j], null);
				assertEquals(expr2.getRight(),predicates[j]);
			}
		}
		// BoolExpression
		for (int j = 0; j < predicates.length; j++) {
				BoolExpression expr = ff.makeBoolExpression(predicates[j], null);
				assertEquals(expr.getPredicate(), predicates[j]);
			}

		for (int i = 0; i < QUANTIFIED_EXPRESSION_LENGTH; i++) {
			for (int j = 0; j < expressions.length; j++) {
				QuantifiedExpression expr = ff.makeQuantifiedExpression(i + FIRST_QUANTIFIED_EXPRESSION,
						new BoundIdentDecl[] { bd_x }, btrue, expressions[j], null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getExpression(),expressions[j]);
			}
			for (int j = 0; j < predicates.length; j++) {
				QuantifiedExpression expr = ff.makeQuantifiedExpression(i + FIRST_QUANTIFIED_EXPRESSION, 
						new BoundIdentDecl[] { bd_x }, predicates[j], two, null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getPredicate(),predicates[j]);
			}
			QuantifiedExpression expr = ff.makeQuantifiedExpression(i + FIRST_QUANTIFIED_EXPRESSION,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, two, null, QuantifiedExpression.Form.Explicit);
			assertEquals(expr.getBoundIdentifiers()[0], bd_x);
			assertEquals(expr.getBoundIdentifiers()[1], bd_z);
		}
		for (int i = 0; i < QUANTIFIED_PREDICATE_LENGTH; i++) {
			for (int j = 0; j < predicates.length; j++) {
				QuantifiedPredicate expr = ff.makeQuantifiedPredicate(i + FIRST_QUANTIFIED_PREDICATE,
						new BoundIdentDecl[] { bd_x }, predicates[j], null);
				assertEquals(expr.getPredicate(), predicates[j]);
			}
			QuantifiedPredicate expr = ff.makeQuantifiedPredicate(i + FIRST_QUANTIFIED_PREDICATE,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, null);
			assertEquals(expr.getBoundIdentifiers()[0], bd_x);
			assertEquals(expr.getBoundIdentifiers()[1], bd_z);
		}
		for (int i = 0; i < RELATIONAL_PREDICATE_LENGTH; i++) {
			for (int j = 0; j < expressions.length; j++) {
				RelationalPredicate expr = ff.makeRelationalPredicate(i + FIRST_RELATIONAL_PREDICATE,
						expressions[j], id_z, null);
				assertEquals(expr.getLeft(),expressions[j]);
				RelationalPredicate expr2 = ff.makeRelationalPredicate(i + FIRST_RELATIONAL_PREDICATE,
						id_z, expressions[j], null);
				assertEquals(expr2.getRight(),expressions[j]);
			}
		}
		for (int i = 0; i < expressions.length; i++) {
			SetExtension expr2 = ff.makeSetExtension(new Expression[] { expressions[i] }, null);
			assertEquals(expr2.getMembers()[0], expressions[i]);
		}
		for (int i = 0; i < expressions.length; i++) {
			SimplePredicate expr3 = ff.makeSimplePredicate(Formula.KFINITE,
					expressions[i], null);
			assertEquals(expr3.getExpression(), expressions[i]);
		}
		for (int i = 0; i < UNARY_EXPRESSION_LENGTH; i++) {
			for (int j = 0; j < expressions.length; j++) {
				UnaryExpression expr = ff.makeUnaryExpression(i + FIRST_UNARY_EXPRESSION,
						expressions[j], null);
				assertEquals(expr.getChild(),expressions[j]);
			}
		}
		for (int i = 0; i < UNARY_PREDICATE_LENGTH; i++) {
			for (int j = 0; j < predicates.length; j++) {
				UnaryPredicate expr = ff.makeUnaryPredicate(i + FIRST_UNARY_PREDICATE,
						predicates[j], null);
				assertEquals(expr.getChild(),predicates[j]);
			}
		}
	}
	
	
	/**
	 * Test that the tags returned by the accessor is the same as the one specified
	 * when constructing the node.
	 */
	public void testTags() {
		for (int i = 0; i < ASSOCIATIVE_EXPRESSION_LENGTH; i++) {
			AssociativeExpression expr = ff.makeAssociativeExpression(i+FIRST_ASSOCIATIVE_EXPRESSION,
					new Expression[]{id_y,id_x},null);
			assertEquals(expr.getTag(), i+FIRST_ASSOCIATIVE_EXPRESSION);
		}
		for (int i = 0; i < ASSOCIATIVE_PREDICATE_LENGTH; i++) {
			AssociativePredicate expr = ff.makeAssociativePredicate(i+FIRST_ASSOCIATIVE_PREDICATE,
					new Predicate[]{btrue,btrue},null);
			assertEquals(expr.getTag(), i+FIRST_ASSOCIATIVE_PREDICATE);
		}
		for (int i = 0; i < BINARY_EXPRESSION_LENGTH; i++) {
			BinaryExpression expr = ff.makeBinaryExpression(i+FIRST_BINARY_EXPRESSION,
					id_x,id_x,null);
			assertEquals(expr.getTag(), i+FIRST_BINARY_EXPRESSION);
		}
		for (int i = 0; i < BINARY_PREDICATE_LENGTH; i++) {
			BinaryPredicate expr = ff.makeBinaryPredicate(i+FIRST_BINARY_PREDICATE,
					btrue,btrue,null);
			assertEquals(expr.getTag(), i+FIRST_BINARY_PREDICATE);
		}
		for (int i = 0; i < ATOMIC_EXPRESSION_LENGTH; i++) {
			AtomicExpression expr = ff.makeAtomicExpression(i+FIRST_ATOMIC_EXPRESSION, null);
			assertEquals(expr.getTag(), i+FIRST_ATOMIC_EXPRESSION);
		}
		// BoolExpression
		BoolExpression exprb = ff.makeBoolExpression(btrue, null);
		assertEquals(exprb.getTag(), Formula.KBOOL);
		IntegerLiteral expr1 = ff.makeIntegerLiteral(Common.ONE, null);
		assertEquals(expr1.getTag(),Formula.INTLIT);
		for (int i = 0; i < LITERAL_PREDICATE_LENGTH; i++) {
			LiteralPredicate expr = ff.makeLiteralPredicate(i+FIRST_LITERAL_PREDICATE, null);
			assertEquals(expr.getTag(), i+FIRST_LITERAL_PREDICATE);
		}
		for (int i = 0; i < QUANTIFIED_EXPRESSION_LENGTH; i++) {
			QuantifiedExpression expr = ff.makeQuantifiedExpression(i+FIRST_QUANTIFIED_EXPRESSION,
					new BoundIdentDecl[]{bd_x},btrue,two,null,QuantifiedExpression.Form.Explicit);
			assertEquals(expr.getTag(), i+FIRST_QUANTIFIED_EXPRESSION);
		}
		for (int i = 0; i < QUANTIFIED_PREDICATE_LENGTH; i++) {
			QuantifiedPredicate expr = ff.makeQuantifiedPredicate(i+FIRST_QUANTIFIED_PREDICATE,
					new BoundIdentDecl[]{bd_x},btrue,null);
			assertEquals(expr.getTag(), i+FIRST_QUANTIFIED_PREDICATE);
		}
		for (int i = 0; i < RELATIONAL_PREDICATE_LENGTH; i++) {
			RelationalPredicate expr = ff.makeRelationalPredicate(i+FIRST_RELATIONAL_PREDICATE,
					id_x,id_x,null);
			assertEquals(expr.getTag(), i+FIRST_RELATIONAL_PREDICATE);
		}
		SetExtension expr2 = ff.makeSetExtension(new Expression[]{id_x}, null);
		assertEquals(expr2.getTag(), Formula.SETEXT);
		
		SimplePredicate expr3 = ff.makeSimplePredicate(Formula.KFINITE,
				id_x,null);
		assertEquals(expr3.getTag(), Formula.KFINITE);
		
		for (int i = 0; i < UNARY_EXPRESSION_LENGTH; i++) {
			UnaryExpression expr = ff.makeUnaryExpression(i+FIRST_UNARY_EXPRESSION,
					id_x,null);
			assertEquals(expr.getTag(), i+FIRST_UNARY_EXPRESSION);
		}
		for (int i = 0; i < UNARY_PREDICATE_LENGTH; i++) {
			UnaryPredicate expr = ff.makeUnaryPredicate(i+FIRST_UNARY_PREDICATE,
					btrue,null);
			assertEquals(expr.getTag(), i+FIRST_UNARY_PREDICATE);
		}
	}
}
