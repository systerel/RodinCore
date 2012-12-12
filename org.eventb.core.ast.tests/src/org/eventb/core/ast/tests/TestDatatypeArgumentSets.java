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

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.eventb.core.ast.tests.InjectedDatatypeExtension.injectExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.junit.Test;

/**
 * Acceptance tests for method
 * {@link IDatatype#getArgumentSets(IExpressionExtension, ExtendedExpression, FormulaFactory)}
 * 
 * @author Laurent Voisin
 */
public class TestDatatypeArgumentSets extends AbstractTests {

	// Specifications of datatypes common to all tests
	private static final String[] DATATYPE_SPECS = {
			"Param[S]      ::= param; param1[List(S)]",//
			"Pow[S]        ::= pow; pow1[ℙ(S)]",//
			"Product[S, T] ::= prod; prod1[S × T]",//
			"Simple        ::= simple; simple1[BOOL]",//
	};

	// Factory including LIST and all the datatypes specified above
	private static final FormulaFactory dtFF;
	static {
		final Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		extensions.addAll(LIST_FAC.getExtensions());
		for (final String ext : DATATYPE_SPECS) {
			final IDatatypeExtension dtExt = injectExtension(ext, LIST_FAC);
			final IDatatype dt = LIST_FAC.makeDatatype(dtExt);
			extensions.addAll(dt.getExtensions());
		}
		dtFF = FormulaFactory.getInstance(extensions);
	}

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
	 * Ensures that passing an extension which is not a constructor raises an
	 * error.
	 */
	@Test 
	public void testNotConstructor() {
		try {
			assertArgumentSets("List(1‥2)", "head");
			fail("Incompatible constructor");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that passing a type constructor instead of a value constructor
	 * raises an error.
	 */
	@Test 
	public void testNotValueConstructor() {
		try {
			assertArgumentSets("List(1‥2)", "List");
			fail("Incompatible constructor");
		} catch (IllegalArgumentException e) {
			// pass
		}
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
		final IExpressionExtension cons = extensionFromSymbol("cons");
		final IDatatype dt = (IDatatype) cons.getOrigin();
		final ExtendedExpression set = parseExtendedExpression("Pow(1‥2)");
		try {
			dt.getArgumentSets(cons, set, dtFF);
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
		final IExpressionExtension cons = extensionFromSymbol("cons");
		final IDatatype dt = (IDatatype) cons.getOrigin();
		final ExtendedExpression set = parseExtendedExpression("cons(1‥2, nil)");
		try {
			dt.getArgumentSets(cons, set, dtFF);
			fail("Incompatible type constructor in set");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	private void assertArgumentSets(String setImage, String constructorSymbol,
			String... expectedImages) {
		final IExpressionExtension cons = extensionFromSymbol(constructorSymbol);
		final ExtendedExpression set = parseExtendedExpression(setImage);
		final IDatatype dt = (IDatatype) set.getExtension().getOrigin();
		final Expression[] expected = parseExpressions(expectedImages);
		final List<Expression> actual = dt.getArgumentSets(cons, set, dtFF);
		assertEquals(asList(expected), actual);
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

	private ExtendedExpression parseExtendedExpression(String exprImage) {
		return (ExtendedExpression) parseExpression(exprImage, dtFF);
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
