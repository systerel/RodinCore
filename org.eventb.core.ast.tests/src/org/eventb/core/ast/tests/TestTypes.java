/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;


/**
 * Test event-B types.
 * 
 * @author Laurent Voisin
 */
public class TestTypes extends AbstractTests {
	
	private FormulaFactory tf = FormulaFactory.getDefault();
	
	private GivenType ty_S = tf.makeGivenType("S");
	private GivenType ty_T = tf.makeGivenType("T");
	private GivenType ty_U = tf.makeGivenType("U");

	private FreeIdentifier id_S = ff.makeFreeIdentifier("S", null, 
			tf.makePowerSetType(ty_S));
	private FreeIdentifier id_T = ff.makeFreeIdentifier("T", null, 
			tf.makePowerSetType(ty_T));
	private FreeIdentifier id_U = ff.makeFreeIdentifier("U", null, 
			tf.makePowerSetType(ty_U));

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
					ff.makeAtomicExpression(Formula.BOOL, null),
					"BOOL"
			),
			new TestItem(
					tf.makeIntegerType(),
					ff.makeAtomicExpression(Formula.INTEGER, null),
					"ℤ"
			),
			new TestItem(
					ty_S,
					id_S,
					"S"
			),
			new TestItem(
					tf.makePowerSetType(ty_S),
					ff.makeUnaryExpression(Formula.POW, id_S, null),
					"ℙ(S)"
			),
			new TestItem(
					tf.makeProductType(ty_S, ty_T),
					ff.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
					"S×T"
			),
			new TestItem(
					tf.makeRelationalType(ty_S, ty_T),
					ff.makeUnaryExpression(Formula.POW,
							ff.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
							null),
					"ℙ(S×T)"
			),
			new TestItem(
					tf.makeProductType(
							tf.makeProductType(ty_S, ty_T), 
							ty_U),
					ff.makeBinaryExpression(Formula.CPROD,
							ff.makeBinaryExpression(Formula.CPROD, id_S, id_T, null),
							id_U, null),
					"S×T×U"
			),
			new TestItem(
					tf.makeProductType(
							ty_S,
							tf.makeProductType(ty_T, ty_U)),
					ff.makeBinaryExpression(Formula.CPROD,
							id_S,
							ff.makeBinaryExpression(Formula.CPROD, id_T, id_U, null),
							null),
					"S×(T×U)"
			),
	};
	
	/**
	 * Checks that the type factory works correctly, as well as conversion to
	 * Expression and String.
	 */
	public void testTypeFactory() {
		for (TestItem item : items) {
			final Expression expr = item.type.toExpression(ff);
			assertEquals(item.expr, expr);
			assertEquals(item.image, item.type.toString());
			final Type expectedExprType = ff.makePowerSetType(item.type);
			assertEquals(expectedExprType, expr.getType());
		}
	}
	
	/**
	 * Checks that the equality between types is not trivially wrong, by
	 * comparing types of the test items (they should all differ).
	 */
	public void testTypeInequality() {
		for (TestItem item1 : items) {
			for (TestItem item2 : items) {
				if (item1 != item2) {
					assertFalse(item1.equals(item2));
				}
			}
		}
	}
	
	public void testTypeParser() {
		for (TestItem item : items) {
			IParseResult result = ff.parseType(item.image);
			assertSuccess(item.image, result);
			assertNull(result.getParsedExpression());
			assertEquals(item.type, result.getParsedType());
		}
		
		// test wrong type formulas
		String[] illFormed = new String[] {
				"ℕ", "ℙ(ℕ)", "ℙ1(ℤ)", "S ⇸ T"
		};
		for (String input: illFormed) {
			IParseResult result = ff.parseType(input);
			assertFailure("parse should have failed", result);
			assertNull(result.getParsedExpression());
			assertNull(result.getParsedType());
		}
	}
	
	public void testIsATypeExpression() {
		ITypeEnvironment typenv = ff.makeTypeEnvironment();
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
