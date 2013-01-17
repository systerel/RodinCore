/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.KBOOL;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.SETEXT;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
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

	/**
	 * Tests if the constructed node's children returned by the accessors are
	 * the same that the one specified when constructing the node.
	 */
	@Test 
	public void testAST() {
		testAST(TagSupply.getV1TagSupply());
		testAST(TagSupply.getV2TagSupply());
	}

	public void testAST(TagSupply allTags) {
		final FormulaFactory fac = allTags.factory;

		final FreeIdentifier id_x = fac.makeFreeIdentifier("x", null);
		final FreeIdentifier id_z = fac.makeFreeIdentifier("z", null);

		final BoundIdentDecl bd_x = fac.makeBoundIdentDecl("x", null);
		final BoundIdentDecl bd_z = fac.makeBoundIdentDecl("z", null);

		final LiteralPredicate btrue = fac.makeLiteralPredicate(BTRUE, null);

		final IntegerLiteral two = fac.makeIntegerLiteral(Common.TWO, null);


		List<Expression> expressions = Common.constructExpressions(allTags);
		List<Predicate> predicates = Common.constructPredicates(allTags);
		for (int tag : allTags.associativeExpressionTags) {
			for (Expression childExpr : expressions) {
				AssociativeExpression expr = fac.makeAssociativeExpression(
						tag, new Expression[] { childExpr, id_x }, null);
				assertEquals(expr.getChildren()[0], childExpr);
			}
		}
		for (int tag : allTags.associativePredicateTags) {
			for (Predicate childPred : predicates) {
				AssociativePredicate expr = fac.makeAssociativePredicate(
						tag, new Predicate[] { childPred, btrue }, null);
				assertEquals(expr.getChildren()[0], childPred);
			}
		}
		for (int tag : allTags.binaryExpressionTags) {
			for (Expression childExpr : expressions) {
				BinaryExpression expr = fac.makeBinaryExpression(tag,
						childExpr, id_x, null);
				assertEquals(expr.getLeft(), childExpr);
				BinaryExpression expr1 = fac.makeBinaryExpression(tag,
						id_x, childExpr, null);
				assertEquals(expr1.getRight(), childExpr);
			}
		}
		for (int tag : allTags.binaryPredicateTags) {
			for (Predicate childPred : predicates) {
				BinaryPredicate pred = fac.makeBinaryPredicate(tag,
						childPred, btrue, null);
				assertEquals(pred.getLeft(), childPred);
				BinaryPredicate pred2 = fac.makeBinaryPredicate(tag, btrue,
						childPred, null);
				assertEquals(pred2.getRight(), childPred);
			}
		}
		for (Predicate childPred : predicates) {
			BoolExpression expr = fac.makeBoolExpression(childPred, null);
			assertEquals(expr.getPredicate(), childPred);
		}
		for (int tag : allTags.multiplePredicateTags) {
			for (Expression childExpr : expressions) {
				MultiplePredicate pred = fac.makeMultiplePredicate(tag,
						new Expression[] { childExpr, id_x }, null);
				assertEquals(pred.getChildren()[0], childExpr);
			}
		}
		for (int tag : allTags.quantifiedExpressionTags) {
			for (Expression childExpr : expressions) {
				QuantifiedExpression expr = fac.makeQuantifiedExpression(
						tag, new BoundIdentDecl[] { bd_x }, btrue, childExpr,
						null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getExpression(), childExpr);
			}
			for (Predicate childPred : predicates) {
				QuantifiedExpression expr = fac.makeQuantifiedExpression(
						tag, new BoundIdentDecl[] { bd_x }, childPred, two,
						null, QuantifiedExpression.Form.Explicit);
				assertEquals(expr.getPredicate(), childPred);
			}
			QuantifiedExpression expr = fac.makeQuantifiedExpression(tag,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, two, null,
					QuantifiedExpression.Form.Explicit);
			assertEquals(expr.getBoundIdentDecls()[0], bd_x);
			assertEquals(expr.getBoundIdentDecls()[1], bd_z);
		}
		for (int tag : allTags.quantifiedPredicateTags) {
			for (Predicate childPred : predicates) {
				QuantifiedPredicate expr = fac.makeQuantifiedPredicate(tag,
						new BoundIdentDecl[] { bd_x }, childPred, null);
				assertEquals(expr.getPredicate(), childPred);
			}
			QuantifiedPredicate expr = fac.makeQuantifiedPredicate(tag,
					new BoundIdentDecl[] { bd_x, bd_z }, btrue, null);
			assertEquals(expr.getBoundIdentDecls()[0], bd_x);
			assertEquals(expr.getBoundIdentDecls()[1], bd_z);
		}
		for (int tag : allTags.relationalPredicateTags) {
			for (Expression childExpr : expressions) {
				RelationalPredicate expr = fac.makeRelationalPredicate(tag,
						childExpr, id_z, null);
				assertEquals(expr.getLeft(), childExpr);
				RelationalPredicate expr2 = fac.makeRelationalPredicate(
						tag, id_z, childExpr, null);
				assertEquals(expr2.getRight(), childExpr);
			}
		}
		for (Expression childExpr : expressions) {
			SetExtension expr2 = fac.makeSetExtension(
					new Expression[] { childExpr }, null);
			assertEquals(expr2.getMembers()[0], childExpr);
		}
		for (Expression childExpr : expressions) {
			SimplePredicate expr3 = fac.makeSimplePredicate(
					Formula.KFINITE, childExpr, null);
			assertEquals(expr3.getExpression(), childExpr);
		}
		for (int tag : allTags.unaryExpressionTags) {
			for (Expression childExpr : expressions) {
				UnaryExpression expr = fac.makeUnaryExpression(tag,
						childExpr, null);
				assertEquals(expr.getChild(), childExpr);
			}
		}
		for (int tag : allTags.unaryPredicateTags) {
			for (Predicate childPred : predicates) {
				UnaryPredicate expr = fac.makeUnaryPredicate(tag,
						childPred, null);
				assertEquals(expr.getChild(), childPred);
			}
		}
	}
	
	
	/**
	 * Test that the tags returned by the accessor is the same as the one specified
	 * when constructing the node.
	 */
	@Test
	public void testTags(){
		testTags(TagSupply.getV1TagSupply());
		testTags(TagSupply.getV2TagSupply());
	}

	public void testTags(TagSupply tagSupply) {
		final FormulaFactory fac = tagSupply.factory;

		final FreeIdentifier id_x = fac.makeFreeIdentifier("x", null);
		final FreeIdentifier id_y = fac.makeFreeIdentifier("y", null);

		final BoundIdentDecl bd_x = fac.makeBoundIdentDecl("x", null);

		final LiteralPredicate btrue = fac.makeLiteralPredicate(BTRUE, null);

		final IntegerLiteral two = fac.makeIntegerLiteral(Common.TWO, null);

		final Expression[] eL = new Expression[] { id_y, id_x };
		final Predicate[] pL = new Predicate[] { btrue, btrue };
		final BoundIdentDecl[] bL = new BoundIdentDecl[] { bd_x };

		for (int tag : tagSupply.associativeExpressionTags) {
			assertTag(tag, fac.makeAssociativeExpression(tag, eL, null));
		}
		for (int tag : tagSupply.associativePredicateTags) {
			assertTag(tag, fac.makeAssociativePredicate(tag, pL, null));
		}
		for (int tag : tagSupply.binaryExpressionTags) {
			assertTag(tag, fac.makeBinaryExpression(tag, id_x, id_x, null));
		}
		for (int tag : tagSupply.binaryPredicateTags) {
			assertTag(tag, fac.makeBinaryPredicate(tag, btrue, btrue, null));
		}
		for (int tag : tagSupply.atomicExpressionTags) {
			assertTag(tag, fac.makeAtomicExpression(tag, null));
		}

		assertTag(KBOOL, fac.makeBoolExpression(btrue, null));

		assertTag(INTLIT, fac.makeIntegerLiteral(Common.ONE, null));

		for (int tag : tagSupply.literalPredicateTags) {
			assertTag(tag, fac.makeLiteralPredicate(tag, null));
		}
		for (int tag : tagSupply.multiplePredicateTags) {
			assertTag(tag, fac.makeMultiplePredicate(tag, eL, null));
		}
		for (int tag : tagSupply.quantifiedExpressionTags) {
			assertTag(tag, fac.makeQuantifiedExpression(tag, bL, btrue, two,
					null, QuantifiedExpression.Form.Explicit));
		}
		for (int tag : tagSupply.quantifiedPredicateTags) {
			assertTag(tag, fac.makeQuantifiedPredicate(tag, bL, btrue, null));
		}
		for (int tag : tagSupply.relationalPredicateTags) {
			assertTag(tag, fac.makeRelationalPredicate(tag, id_x, id_x, null));
		}

		assertTag(SETEXT, fac.makeSetExtension(eL, null));

		assertTag(KFINITE, fac.makeSimplePredicate(Formula.KFINITE, id_x, null));

		for (int tag : tagSupply.unaryExpressionTags) {
			assertTag(tag, fac.makeUnaryExpression(tag, id_x, null));
		}
		for (int tag : tagSupply.unaryPredicateTags) {
			assertTag(tag, fac.makeUnaryPredicate(tag, btrue, null));
		}
	}
	
	private void assertTag(int expected, Formula<?> formula) {
		assertEquals(expected, formula.getTag());
	}
}
