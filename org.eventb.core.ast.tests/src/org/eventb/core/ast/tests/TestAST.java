/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KBOOL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.SETEXT;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.tests.Common.TagSupply;
import org.junit.Test;

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
public class TestAST extends AbstractTests {

	private FreeIdentifier id_x = ff.makeFreeIdentifier("x", null);
	private FreeIdentifier id_y = ff.makeFreeIdentifier("y", null);
	private FreeIdentifier id_z = ff.makeFreeIdentifier("z", null);

	private BoundIdentDecl bd_x = ff.makeBoundIdentDecl("x", null);
	private BoundIdentDecl bd_z = ff.makeBoundIdentDecl("z", null);

	private LiteralPredicate btrue = ff.makeLiteralPredicate(BTRUE, null);

	private IntegerLiteral two = ff.makeIntegerLiteral(Common.TWO, null);

	/**
	 * Tests if the constructed node's children returned by the accessors are
	 * the same that the one specified when constructing the node.
	 */
	@Test 
	public void testAST() {
		final TagSupply allTags = TagSupply.getAllTagSupply();

		List<Expression> expressions = Common.constructExpressions(allTags);
		List<Predicate> predicates = Common.constructPredicates(allTags);
		for (int tag : allTags.associativeExpressionTags) {
			for (Expression childExpr : expressions) {
				AssociativeExpression expr = ff.makeAssociativeExpression(
						tag, new Expression[] { childExpr, id_x }, null);
				assertEquals(expr.getChildren()[0], childExpr);
			}
		}
		for (int tag : allTags.associativePredicateTags) {
			for (Predicate childPred : predicates) {
				AssociativePredicate expr = ff.makeAssociativePredicate(
						tag, new Predicate[] { childPred, btrue }, null);
				assertEquals(expr.getChildren()[0], childPred);
			}
		}
		for (int tag : allTags.binaryExpressionTags) {
			for (Expression childExpr : expressions) {
				BinaryExpression expr = ff.makeBinaryExpression(tag,
						childExpr, id_x, null);
				assertEquals(expr.getLeft(), childExpr);
				BinaryExpression expr1 = ff.makeBinaryExpression(tag,
						id_x, childExpr, null);
				assertEquals(expr1.getRight(), childExpr);
			}
		}
		for (int tag : allTags.binaryPredicateTags) {
			for (Predicate childPred : predicates) {
				BinaryPredicate pred = ff.makeBinaryPredicate(tag,
						childPred, btrue, null);
				assertEquals(pred.getLeft(), childPred);
				BinaryPredicate pred2 = ff.makeBinaryPredicate(tag,
						btrue, childPred, null);
				assertEquals(pred2.getRight(), childPred);
			}
		}
		for (Predicate childPred : predicates) {
			BoolExpression expr = ff.makeBoolExpression(childPred, null);
			assertEquals(expr.getPredicate(), childPred);
		}
		for (int tag : allTags.multiplePredicateTags) {
			for (Expression childExpr : expressions) {
				MultiplePredicate pred = ff.makeMultiplePredicate(tag,
						new Expression[] { childExpr, id_x }, null);
				assertEquals(pred.getChildren()[0], childExpr);
			}
		}
		for (int tag : allTags.quantifiedExpressionTags) {
			for (Expression childExpr : expressions) {
				QuantifiedExpression expr = ff.makeQuantifiedExpression(
						tag, new BoundIdentDecl[] { bd_x }, btrue,
						childExpr, null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getExpression(), childExpr);
			}
			for (Predicate childPred : predicates) {
				QuantifiedExpression expr = ff.makeQuantifiedExpression(
						tag, new BoundIdentDecl[] { bd_x }, childPred,
						two, null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getPredicate(), childPred);
			}
			QuantifiedExpression expr = ff.makeQuantifiedExpression(tag,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, two, null,
					QuantifiedExpression.Form.Explicit);
			assertEquals(expr.getBoundIdentDecls()[0], bd_x);
			assertEquals(expr.getBoundIdentDecls()[1], bd_z);
		}
		for (int tag : allTags.quantifiedPredicateTags) {
			for (Predicate childPred : predicates) {
				QuantifiedPredicate expr = ff.makeQuantifiedPredicate(tag,
						new BoundIdentDecl[] { bd_x }, childPred, null);
				assertEquals(expr.getPredicate(), childPred);
			}
			QuantifiedPredicate expr = ff.makeQuantifiedPredicate(tag,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, null);
			assertEquals(expr.getBoundIdentDecls()[0], bd_x);
			assertEquals(expr.getBoundIdentDecls()[1], bd_z);
		}
		for (int tag : allTags.relationalPredicateTags) {
			for (Expression childExpr : expressions) {
				RelationalPredicate expr = ff.makeRelationalPredicate(tag,
						childExpr, id_z, null);
				assertEquals(expr.getLeft(), childExpr);
				RelationalPredicate expr2 = ff.makeRelationalPredicate(tag,
						id_z, childExpr, null);
				assertEquals(expr2.getRight(), childExpr);
			}
		}
		for (Expression childExpr : expressions) {
			SetExtension expr2 = ff.makeSetExtension(
					new Expression[] { childExpr }, null);
			assertEquals(expr2.getMembers()[0], childExpr);
		}
		for (Expression childExpr : expressions) {
			SimplePredicate expr3 = ff.makeSimplePredicate(Formula.KFINITE,
					childExpr, null);
			assertEquals(expr3.getExpression(), childExpr);
		}
		for (int tag : allTags.unaryExpressionTags) {
			for (Expression childExpr : expressions) {
				UnaryExpression expr = ff.makeUnaryExpression(tag, childExpr,
						null);
				assertEquals(expr.getChild(), childExpr);
			}
		}
		for (int tag : allTags.unaryPredicateTags) {
			for (Predicate childPred : predicates) {
				UnaryPredicate expr = ff.makeUnaryPredicate(tag, childPred,
						null);
				assertEquals(expr.getChild(), childPred);
			}
		}
	}
	
	
	/**
	 * Test that the tags returned by the accessor is the same as the one specified
	 * when constructing the node.
	 */
	@Test 
	public void testTags() {
		final TagSupply tagSupply = TagSupply.getAllTagSupply();
		final Expression[] eL = new Expression[] { id_y, id_x };
		final Predicate[] pL = new Predicate[] { btrue, btrue };
		final BoundIdentDecl[] bL = new BoundIdentDecl[] { bd_x };

		for (int tag : tagSupply.associativeExpressionTags) {
			assertTag(tag, ff.makeAssociativeExpression(tag, eL, null));
		}
		for (int tag : tagSupply.associativePredicateTags) {
			assertTag(tag, ff.makeAssociativePredicate(tag, pL, null));
		}
		for (int tag : tagSupply.binaryExpressionTags) {
			assertTag(tag, ff.makeBinaryExpression(tag, id_x, id_x, null));
		}
		for (int tag : tagSupply.binaryPredicateTags) {
			assertTag(tag, ff.makeBinaryPredicate(tag, btrue, btrue, null));
		}
		for (int tag : tagSupply.atomicExpressionTags) {
			assertTag(tag, ff.makeAtomicExpression(tag, null));
		}

		assertTag(KBOOL, ff.makeBoolExpression(btrue, null));

		assertTag(INTLIT, ff.makeIntegerLiteral(Common.ONE, null));

		for (int tag : tagSupply.literalPredicateTags) {
			assertTag(tag, ff.makeLiteralPredicate(tag, null));
		}
		for (int tag : tagSupply.multiplePredicateTags) {
			assertTag(tag, ff.makeMultiplePredicate(tag, eL, null));
		}
		for (int tag : tagSupply.quantifiedExpressionTags) {
			assertTag(tag, ff.makeQuantifiedExpression(tag, bL, btrue, two,
					null, QuantifiedExpression.Form.Explicit));
		}
		for (int tag : tagSupply.quantifiedPredicateTags) {
			assertTag(tag, ff.makeQuantifiedPredicate(tag, bL, btrue, null));
		}
		for (int tag : tagSupply.relationalPredicateTags) {
			assertTag(tag, ff.makeRelationalPredicate(tag, id_x, id_x, null));
		}

		assertTag(SETEXT, ff.makeSetExtension(eL, null));

		assertTag(KFINITE, ff.makeSimplePredicate(Formula.KFINITE, id_x, null));

		for (int tag : tagSupply.unaryExpressionTags) {
			assertTag(tag, ff.makeUnaryExpression(tag, id_x, null));
		}
		for (int tag : tagSupply.unaryPredicateTags) {
			assertTag(tag, ff.makeUnaryPredicate(tag, btrue, null));
		}
	}
	
	private void assertTag(int expected, Formula<?> formula) {
		assertEquals(expected, formula.getTag());
	}
}
