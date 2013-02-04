/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *     Systerel - mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.LanguageVersion.LATEST;
import static org.eventb.core.ast.tests.FastFactory.NO_PREDICATE;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.junit.Test;


/**
 * Test event-B types.
 * 
 * @author Laurent Voisin
 */
public class TestTypes extends AbstractTests {
	
	private final FormulaFactory tf = LIST_FAC;

	private final GivenType ty_S = tf.makeGivenType("S");
	private final GivenType ty_T = tf.makeGivenType("T");
	private final GivenType ty_U = tf.makeGivenType("U");

	private final FreeIdentifier id_S = tf.makeFreeIdentifier("S", null,
			tf.makePowerSetType(ty_S));
	private final FreeIdentifier id_T = tf.makeFreeIdentifier("T", null,
			tf.makePowerSetType(ty_T));
	private final FreeIdentifier id_U = tf.makeFreeIdentifier("U", null,
			tf.makePowerSetType(ty_U));

	private final Type LIST_S = tf.makeParametricType(mList(ty_S), EXT_LIST);
	private final Expression LIST_S_EXP = tf.makeExtendedExpression(EXT_LIST,
			mList(id_S), NO_PREDICATE, null, tf.makePowerSetType(LIST_S));
	private final Type LIST_LIST_S = tf.makeParametricType(mList(LIST_S),
			EXT_LIST);

	private static class TestItem {
		Type type;
		Expression expr;
		String image;
		
		TestItem(Type type, Expression expr, String image) {
			this.type = type;
			this.expr = expr;
			this.image = image;
		}
	}
	
	TestItem[] items = new TestItem[] {
			new TestItem(
					tf.makeBooleanType(),
					tf.makeAtomicExpression(Formula.BOOL, null),
					"BOOL"
			),
			new TestItem(
					tf.makeIntegerType(),
					tf.makeAtomicExpression(Formula.INTEGER, null),
					"ℤ"
			),
			new TestItem(
					ty_S,
					id_S,
					"S"
			),
			new TestItem(
					tf.makePowerSetType(ty_S),
					tf.makeUnaryExpression(Formula.POW, id_S, null),
					"ℙ(S)"
			),
			new TestItem(
					tf.makeProductType(ty_S, ty_T),
					tf.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
					"S×T"
			),
			new TestItem(
					tf.makeRelationalType(ty_S, ty_T),
					tf.makeUnaryExpression(Formula.POW,
							tf.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
							null),
					"ℙ(S×T)"
			),
			new TestItem(
					tf.makeProductType(
							tf.makeProductType(ty_S, ty_T), 
							ty_U),
					tf.makeBinaryExpression(Formula.CPROD,
							tf.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
							id_U, null),
					"S×T×U"
			),
			new TestItem(
					tf.makeProductType(
							ty_S,
							tf.makeProductType(ty_T, ty_U)),
					tf.makeBinaryExpression(Formula.CPROD,
							id_S,
							tf.makeBinaryExpression(Formula.CPROD, id_T, id_U, null),
							null),
					"S×(T×U)"
			),
			new TestItem(
					LIST_S,
					LIST_S_EXP,
					"List(S)"
			),
			new TestItem(
					LIST_LIST_S,
					tf.makeExtendedExpression(EXT_LIST,
							mList(LIST_S_EXP), NO_PREDICATE,
							null, tf.makePowerSetType(LIST_LIST_S)),
					"List(List(S))"
			),
	};
	
	/**
	 * Checks that the type factory works correctly, as well as conversion to
	 * Expression and String.
	 */
	@Test 
	public void testTypeFactory() {
		for (TestItem item : items) {
			final Expression expr = item.type.toExpression();
			assertEquals(item.expr, expr);
			assertEquals(item.image, item.type.toString());
			final Type expectedExprType = tf.makePowerSetType(item.type);
			assertEquals(expectedExprType, expr.getType());
		}
	}
	
	/**
	 * Checks that the equality between types is not trivially wrong, by
	 * comparing types of the test items (they should all differ).
	 */
	@Test 
	public void testTypeInequality() {
		for (TestItem item1 : items) {
			for (TestItem item2 : items) {
				if (item1 != item2) {
					assertFalse(item1.equals(item2));
				}
			}
		}
	}
	
	@Test 
	public void testTypeParser() {
		for (TestItem item : items) {
			IParseResult result = tf.parseType(item.image, LATEST);
			assertSuccess(item.image, result);
			assertNull(result.getParsedExpression());
			assertEquals(item.type, result.getParsedType());
		}
		
		// test wrong type formulas
		String[] illFormed = new String[] {
				"ℕ", "ℙ(ℕ)", "ℙ1(ℤ)", "S ⇸ T"
		};
		for (String input: illFormed) {
			IParseResult result = tf.parseType(input, LATEST);
			assertFailure("parse should have failed", result);
			assertNull(result.getParsedExpression());
			assertNull(result.getParsedType());
		}
	}
	
	@Test 
	public void testIsATypeExpression() {
		ITypeEnvironmentBuilder typenv = tf.makeTypeEnvironment();
		typenv.addGivenSet("S");
		typenv.addGivenSet("T");
		typenv.addName("x", tf.makePowerSetType(ty_S));
		typenv.addName("y", tf.makePowerSetType(ty_T));
		
		String[] valid = new String[] {
				"S",
				"ℤ",
				"BOOL",
				"ℙ(S)",
				"S × T",
				"ℙ(S × T)",
				"S ↔ T",
		};

		for (String image: valid) {
			Expression expr = parseAndTypeCheck(image, typenv);
			assertTrue("Expression " + expr + " denotes a type", 
					expr.isATypeExpression());
		}
		
		// test wrong type formulas
		String[] illFormed = new String[] {
				"ℕ",
				"ℙ(ℕ)",
				"ℙ1(ℤ)",
				"S ⇸ T",
				"x",
				"x ↦ y",
				"ℙ(x)",
				"x × T",
				"S × y",
		};
		for (String image: illFormed) {
			Expression expr = parseAndTypeCheck(image, typenv);
			assertFalse("Expression " + expr + " doesn't denote a type", 
					expr.isATypeExpression());
		}
	}

	private Expression parseAndTypeCheck(String image, ITypeEnvironment typenv) {
		Expression expr = parseExpression(image);
		typeCheck(expr, typenv);
		return expr;
	}
	
}
