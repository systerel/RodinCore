/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.junit.Test;

/**
 * Acceptance tests for method
 * {@link IConstructorExtension#getArgumentSets(Expression)}
 * 
 * @author Laurent Voisin
 */
public class TestDatatypeArgumentSets extends AbstractTests {

	// Factory including LIST and several datatypes common to all tests
	private static final FormulaFactory dtFF = FastFactory.mDatatypeFactory(
			LIST_FAC,//
			"Param[S]      ::= param; param1[List(S)]",//
			"Pow[S]        ::= pow; pow1[ℙ(S)]",//
			"Product[S, T] ::= prod; prod1[S × T]",//
			"Simple        ::= simple; simple1[BOOL]"//
	);

	/**
	 * Ensures that a constructor argument of parametric type is instantiated
	 * correctly.
	 */
	@Test 
	public void testParametricTypeArgument() {
		assertArgumentSets("Param(1‥3)", "param", "List(1‥3)");
	}

	/**
	 * Ensures that a constructor argument of power set type is instantiated
	 * correctly.
	 */
	@Test 
	public void testPowerSetArgument() {
		assertArgumentSets("Pow(1‥3)", "pow", "ℙ(1‥3)");
	}

	/**
	 * Ensures that a constructor argument of product type is instantiated
	 * correctly.
	 */
	@Test 
	public void testProductTypeArgument() {
		assertArgumentSets("Product(1‥3, {TRUE})", "prod", "1‥3 × {TRUE}");
	}

	/**
	 * Ensures that a constructor argument of simple type is instantiated
	 * correctly.
	 */
	@Test 
	public void testSimpleTypeArgument() {
		assertArgumentSets("Simple", "simple", "BOOL");
	}

	/**
	 * Ensures that several constructor arguments are instantiated correctly.
	 */
	@Test 
	public void testSeveralArgument() {
		assertArgumentSets("List(1‥3)", "cons", "1‥3", "List(1‥3)");
	}

	/**
	 * Ensures that passing a constructor from another datatype raises an error.
	 */
	@Test 
	public void testConstructorWrongDatatype() {
		try {
			assertArgumentSets("List(1 ‥ 2)", "param");
			fail("Incompatible constructor");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that passing a set built with a type constructor from another
	 * datatype raises an error.
	 */
	@Test 
	public void testWrongTypeConstructor() {
		final IConstructorExtension cons = (IConstructorExtension) extensionFromSymbol("cons");
		final Expression set = parseExpression("Pow(1‥2)");
		try {
			cons.getArgumentSets(set);
			fail("Incompatible type constructor in set");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that passing a set not built with a type constructor raises an
	 * error.
	 */
	@Test 
	public void testNotTypeConstructor() {
		final IConstructorExtension cons = (IConstructorExtension) extensionFromSymbol("cons");
		final Expression set = parseExpression("cons(1‥2, nil)", dtFF);
		try {
			cons.getArgumentSets(set);
			fail("Incompatible type constructor in set");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	private void assertArgumentSets(String setImage, String constructorSymbol,
			String... expectedImages) {
		final IConstructorExtension cons = (IConstructorExtension) extensionFromSymbol(constructorSymbol);
		final Expression set = parseExpression(setImage, dtFF);
		final Expression[] expected = parseExpressions(expectedImages);
		final Expression[] actual = cons.getArgumentSets(set);
		assertArrayEquals(expected, actual);
	}

	private IExpressionExtension extensionFromSymbol(String symbol) {
		for (final IFormulaExtension ext : dtFF.getExtensions()) {
			if (symbol.equals(ext.getSyntaxSymbol())) {
				return (IExpressionExtension) ext;
			}
		}
		fail("Unknown extension symbol: " + symbol);
		return null;
	}

	private Expression[] parseExpressions(String[] images) {
		final int length = images.length;
		final Expression[] result = new Expression[length];
		for (int i = 0; i < length; i++) {
			result[i] = parseExpression(images[i], dtFF);
		}
		return result;
	}

}
