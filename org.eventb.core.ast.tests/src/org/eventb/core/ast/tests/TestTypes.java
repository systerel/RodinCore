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
 *     Systerel - translation to another factory
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.ff_extns;
import static org.eventb.core.ast.tests.FastFactory.mDatatypeFactory;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
 * Unit tests for Event-B type construction, conversion back and forth to
 * strings and expressions, and translation to another factory.
 * 
 * @author Laurent Voisin
 */
public class TestTypes extends AbstractTests {

	private static final String ENUM_SPEC = "Enum ::= e";

	/**
	 * Factory used to build types. Contains the classical List datatype and a
	 * simple enumerate with one value.
	 */
	private static final FormulaFactory tf = mDatatypeFactory(LIST_FAC,
			ENUM_SPEC);

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
		final Type list_S = tf.makeParametricType(EXT_LIST, ty_S);
		final Type list_List_S = tf.makeParametricType(EXT_LIST, list_S);

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
		checkType(tf.makeParametricType(enum_ext), "Enum");
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
	private void assertTypeExpression(Type type, String image) throws Exception {
		final Expression expected = parseExpression(image, typenv);
		final Expression actual = type.toExpression();
		assertEquals(expected, actual);
		assertEquals(expected, type.toExpression());

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

	/**
	 * Ensures that a type, which can be translated to another factory, is
	 * properly translated.
	 */
	@Test
	public void testTypeTranslation() {
		assertTypeTranslation("BOOL");
		assertTypeTranslation("ℤ");
		assertTypeTranslation("S");
		assertTypeTranslation("ℙ(S)");
		assertTypeTranslation("S×T");
		assertTypeTranslation("ℙ(S×T)");
		assertTypeTranslation("S×T×U");
		assertTypeTranslation("S×(T×U)");
		assertTypeTranslation("Enum");
		assertTypeTranslation("List(S)");
		assertTypeTranslation("List(List(S))");
	}

	// Translating from tf to ff_extns
	private void assertTypeTranslation(String image) {
		final Type type = parseType(image, tf);

		// Translation to same factory
		assertTrue(type.isTranslatable(tf));
		assertSame(type, type.translate(tf));

		// Translation to compatible factory
		final FormulaFactory trg = mDatatypeFactory(ff_extns, ENUM_SPEC);
		assertTrue(type.isTranslatable(trg));
		final Type translated = type.translate(trg);
		assertSame(trg, translated.getFactory());
		assertEquals(type, translated);
	}

	/**
	 * Ensures that a type, which cannot be translated to another factory, is
	 * properly identified as such and that attempting to translate it raises an
	 * exception.
	 */
	@Test
	public void testIsTranslatable() {
		// Given type "prime" is a reserved keyword in target factory
		assertNotTypeTranslation("prime", ff_extns);
		assertNotTypeTranslation("ℙ(prime)", ff_extns);
		assertNotTypeTranslation("prime×T", ff_extns);
		assertNotTypeTranslation("S×prime", ff_extns);
		assertNotTypeTranslation("List(prime)", ff_extns);

		// Missing extension in target factory
		assertNotTypeTranslation("Enum", ff);
		assertNotTypeTranslation("List(S)", ff);
	}

	// Translation to trg (incompatible factory)
	private void assertNotTypeTranslation(String image, FormulaFactory trg) {
		final Type type = parseType(image, tf);

		assertFalse(type.isTranslatable(trg));
		try {
			type.translate(trg);
			fail("Type " + type + " shall not translate to " + trg);
		} catch (IllegalArgumentException exc) {
			// pass
		}
	}

}
