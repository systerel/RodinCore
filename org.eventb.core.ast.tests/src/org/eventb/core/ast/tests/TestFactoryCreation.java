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
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.DatatypeParser.parse;
import static org.eventb.core.ast.tests.FastFactory.mDatatypeFactory;
import static org.eventb.core.ast.tests.TestGenParser.EXT_PRIME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.junit.Test;

/**
 * Unit tests about factory creation.
 * 
 * @author Laurent Voisin
 */
public class TestFactoryCreation extends AbstractTests {

	private static final IExpressionExtension COND = FormulaFactory.getCond();

	/**
	 * Ensures that a factory without extension is the same as the default
	 * factory.
	 */
	@Test
	public void simpleFactory() {
		assertSame(ff, FormulaFactory.getInstance());
	}

	/**
	 * Ensures that two factories with the same extension are the same.
	 */
	@Test
	public void sameOneExtension() {
		final FormulaFactory ff1 = FormulaFactory.getInstance(EXT_PRIME);
		final FormulaFactory ff2 = FormulaFactory.getInstance(EXT_PRIME);
		assertSame(ff1, ff2);
	}

	/**
	 * Ensures that two factories with different extensions are different.
	 */
	@Test
	public void differentOneExtension() {
		final FormulaFactory ff1 = FormulaFactory.getInstance(EXT_PRIME);
		final FormulaFactory ff2 = FormulaFactory.getInstance(COND);
		assertFalse(ff1.equals(ff2));
		assertFalse(ff2.equals(ff1));
	}

	/**
	 * Ensures that two factories with the same datatype are the same.
	 */
	@Test
	public void oneDatatype() {
		final FormulaFactory ff1 = mDatatypeFactory(ff, "Foo ::= foo");
		final FormulaFactory ff2 = mDatatypeFactory(ff, "Foo ::= foo");
		assertSame(ff1, ff2);
	}

	/**
	 * Ensures that two factories with the same extensions are the same, even if
	 * the extensions are listed in a different order.
	 */
	@Test
	public void twoExtensions() {
		final FormulaFactory ff1 = FormulaFactory.getInstance(EXT_PRIME, COND);
		final FormulaFactory ff2 = FormulaFactory.getInstance(COND, EXT_PRIME);
		assertSame(ff1, ff2);
	}

	/**
	 * Ensures that a factory cannot be created with an incomplete set of
	 * extensions from a datatype.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void incompleteDatatype() {
		final IDatatype dt = parse(ff, "Foo ::= foo");
		final IFormulaExtension extn = dt.getExtensions().iterator().next();
		FormulaFactory.getInstance(extn);
	}

	/**
	 * Ensures that a factory cannot be created with a datatype without its
	 * direct dependencies.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void datatypeMissingDirectDependencies() {
		final FormulaFactory dtFF = mDatatypeFactory(ff, "Foo ::= foo");
		final IDatatype dt = parse(dtFF, "Bar ::= bar[Foo]");
		FormulaFactory.getInstance(dt.getExtensions());
	}

	/**
	 * Ensures that a factory cannot be created with a datatype without its
	 * direct dependencies.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void datatypeMissingIndirectDependencies() {
		final FormulaFactory dtFF = mDatatypeFactory(ff, //
				"Foo ::= foo", //
				"Bar ::= bar[Foo]");
		final IDatatype dt = parse(dtFF, "Baz ::= baz[Bar]");
		FormulaFactory.getInstance(dt.getExtensions());
	}

}
