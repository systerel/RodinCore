/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.datatype;

import static org.eventb.core.ast.tests.DatatypeParser.parse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.datatype.ITypeConstructorExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.tests.AbstractTests;
import org.eventb.core.ast.tests.FastFactory;
import org.junit.Test;

/**
 * Unit tests of interface {@link ISetInstantiation}.
 * 
 * @author Laurent Voisin
 */
public class TestSetInstantiation extends AbstractTests {

	private static final IDatatype OTHER_DT = parse(ff, "D ::= c");
	private static final FormulaFactory OTHER_FAC = OTHER_DT.getFactory();
	private static final Expression OTHER_SET = parseExpression("D", OTHER_FAC);

	private static final Expression INT_SET = parseExpression("1‥3", LIST_FAC);
	private static final Expression LIST_VALUE = parseExpression(
			"cons(1, nil)", LIST_FAC);

	// Tests about set instantiation creation

	@Test(expected = NullPointerException.class)
	public void getSetInstantiationNull() {
		LIST_DT.getSetInstantiation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSetInstantiationNotExtendedExpression() {
		LIST_DT.getSetInstantiation(INT_SET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSetInstantiationNotTypeConstructor() {
		LIST_DT.getSetInstantiation(LIST_VALUE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSetInstantiationOtherDatatype() {
		assertTrue(((ExtendedExpression) OTHER_SET).getExtension() instanceof ITypeConstructorExtension);
		LIST_DT.getSetInstantiation(OTHER_SET);
	}

	// Tests about the interface properly

	private static final Expression LIST_SET = parseExpression("List(1‥3)",
			LIST_FAC);
	private static final ISetInstantiation LIST_SET_INST = LIST_DT
			.getSetInstantiation(LIST_SET);

	@Test
	public void getOrigin() {
		assertSame(LIST_DT, LIST_SET_INST.getOrigin());
	}

	@Test
	public void getInstanceSet() {
		assertSame(LIST_SET, LIST_SET_INST.getInstanceSet());
	}

	// Tests about argument set instantiation

	private static final ISetInstantiation OTHER_INST = OTHER_DT
			.getSetInstantiation(OTHER_SET);

	@Test(expected = NullPointerException.class)
	public void argumentGetSetNull() {
		EXT_HEAD.getSet(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void argumentGetSetOtherDatatype() {
		EXT_HEAD.getSet(OTHER_INST);
	}

	@Test
	public void argumentGetSet() {
		assertEquals(INT_SET, EXT_HEAD.getSet(LIST_SET_INST));
		assertEquals(LIST_SET, EXT_TAIL.getSet(LIST_SET_INST));
	}

	// Integration tests

	// Factory including LIST and several datatypes common to all tests
	private static final FormulaFactory dtFF = FastFactory.mDatatypeFactory(
			LIST_FAC,//
			"Param[S]      ::= param[param1: List(S)]",//
			"Pow[S]        ::= pow[pow1: ℙ(S)]",//
			"Product[S, T] ::= prod[prod1: S × T]",//
			"Simple        ::= simple[simple1: BOOL]"//
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

	private void assertArgumentSets(String setImage, String constructorSymbol,
			String... expectedImages) {
		final IConstructorExtension cons = extensionFromSymbol(constructorSymbol);
		final Expression set = parseExpression(setImage, dtFF);
		final Expression[] expected = parseExpressions(expectedImages);
		final Expression[] actual = getArgumentSets(set, cons);
		assertArrayEquals(expected, actual);
	}

	private IConstructorExtension extensionFromSymbol(String symbol) {
		for (final IFormulaExtension ext : dtFF.getExtensions()) {
			if (symbol.equals(ext.getSyntaxSymbol())) {
				return (IConstructorExtension) ext;
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

	private Expression[] getArgumentSets(Expression set,
			IConstructorExtension cons) {
		final IDatatype datatype = cons.getOrigin();
		final ISetInstantiation inst = datatype.getSetInstantiation(set);
		final IConstructorArgument[] args = cons.getArguments();
		final Expression[] result = new Expression[args.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = args[i].getSet(inst);
		}
		return result;
	}

}
