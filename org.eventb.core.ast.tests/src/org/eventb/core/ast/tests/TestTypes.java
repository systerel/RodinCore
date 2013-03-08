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

import static org.eventb.core.ast.tests.FastFactory.NO_TYPES;
import static org.eventb.core.ast.tests.FastFactory.mDatatypeFactory;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.Test;

/**
 * Unit tests for Event-B type construction, and conversion back and forth to
 * strings and expressions.
 * 
 * @author Laurent Voisin
 */
public class TestTypes extends AbstractTests {

	/**
	 * Factory used to build types. Contains the classical List datatype and a
	 * simple enumerate with one value.
	 */
	private static final FormulaFactory tf = mDatatypeFactory(LIST_FAC,
			"Enum ::= e");

	private static final ITypeEnvironment typenv = mTypeEnvironment(
			"S=ℙ(S); T=ℙ(T); U=ℙ(U); x=ℙ(S); y=ℙ(T)", tf);

	/**
	 * Ensures that types can be constructed and converted back and forth to
	 * strings and expressions.
	 */
	@Test
	public void testTypes() throws Exception {
		final GivenType ty_S = tf.makeGivenType("S");
		final GivenType ty_T = tf.makeGivenType("T");
		final GivenType ty_U = tf.makeGivenType("U");
		final IExpressionExtension enum_ext = findExtension(tf, "Enum");
		final Type list_S = tf.makeParametricType(mList(ty_S), EXT_LIST);
		final Type list_List_S = tf.makeParametricType(mList(list_S), EXT_LIST);

		checkType(tf.makeBooleanType(), "BOOL");
		checkType(tf.makeIntegerType(), "ℤ");
		checkType(ty_S, "S");
		checkType(tf.makePowerSetType(ty_S), "ℙ(S)");
		checkType(tf.makeProductType(ty_S, ty_T), "S×T");
		checkType(tf.makeRelationalType(ty_S, ty_T), "ℙ(S×T)");
		checkType(tf.makeProductType(tf.makeProductType(ty_S, ty_T), ty_U),
				"S×T×U");
		checkType(tf.makeProductType(ty_S, tf.makeProductType(ty_T, ty_U)),
				"S×(T×U)");
		checkType(tf.makeParametricType(NO_TYPES, enum_ext), "Enum");
		checkType(list_S, "List(S)");
		checkType(list_List_S, "List(List(S))");
	}

	private IExpressionExtension findExtension(FormulaFactory fac, String symbol) {
		for (final IFormulaExtension extn : fac.getExtensions()) {
			if (extn.getSyntaxSymbol().equals(symbol)) {
				return (IExpressionExtension) extn;
			}
		}
		assert false;
		return null;
	}

	private void checkType(Type type, String image) throws Exception {
		assertTypeString(type, image);
		assertTypeExpression(type, image);
	}

	/**
	 * Checks type conversion to String and parsing back from string.
	 */
	private void assertTypeString(Type type, String image) {
		assertEquals(image, type.toString());
		final IParseResult result = tf.parseType(image);
		assertSuccess(image, result);
		assertEquals(type, result.getParsedType());
		assertNull(result.getParsedExpression());
	}

	/**
	 * Checks type conversion to Expression and getting back a type from the
	 * expression.
	 */
	@SuppressWarnings("deprecation")
	private void assertTypeExpression(Type type, String image) throws Exception {
		final Expression expected = parseExpression(image, typenv);
		final Expression actual = type.toExpression();
		assertEquals(expected, actual);
		assertEquals(expected, type.toExpression(type.getFactory()));

		assertTrue(actual.isATypeExpression());
		assertEquals(type, actual.toType());
		assertEquals(tf.makePowerSetType(type), actual.getType());
	}

	/**
	 * Ensures that the equality between types is not trivially wrong, by
	 * comparing types some of them.
	 */
	@Test
	public void testTypeInequality() {
		final Type[] types = mTypes(tf, "BOOL", "ℤ", "S", "ℙ(S)", "S×T",
				"ℙ(S×T)", "S×T×U", "S×(T×U)", "Enum", "List(S)",
				"List(List(S))");
		for (final Type left : types) {
			for (final Type right : types) {
				if (left != right) {
					assertFalse(left.equals(right));
				}
			}
		}
	}

	private Type[] mTypes(FormulaFactory fac, String... images) {
		final Type[] types = new Type[images.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = parseType(images[i], fac);
		}
		return types;
	}

	/**
	 * Ensures that an expression which does not look like a type cannot be
	 * parsed as type and is not advertised as a type.
	 */
	@Test
	public void testNotTypes() {
		assertNotType("ℕ");
		assertNotType("ℙ(ℕ)");
		assertNotType("ℙ1(ℤ)");
		assertNotType("S ⇸ T");
	}

	private void assertNotType(String image) {
		final IParseResult result = tf.parseType(image);
		assertFailure("parse should have failed", result);
		assertNull(result.getParsedExpression());
		assertNull(result.getParsedType());

		assertIsNotATypeExpression(image);
	}

	/**
	 * Ensures that an expression which is not a type is not advertised as a
	 * type.
	 */
	@Test
	public void testIsATypeExpression() {
		assertIsNotATypeExpression("x");
		assertIsNotATypeExpression("x ↦ y");
		assertIsNotATypeExpression("ℙ(x)");
		assertIsNotATypeExpression("x × T");
		assertIsNotATypeExpression("S × y");
	}

	private void assertIsNotATypeExpression(String image) {
		final Expression expr = parseExpression(image, typenv);
		assertFalse(expr.isATypeExpression());
	}

}
