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

/**
 * Unit tests for factory methods which are not fully tested elsewhere.
 * 
 * @author Laurent Voisin
 */
public class TestFormulaFactory extends AbstractTests {

	/**
	 * Ensures that method isValidIdentifierName() takes into account the
	 * version of the mathematical language supported by the formula factory
	 * instance.
	 */
	public void testValidIdentifierName() throws Exception {
		final String validName = "foo";
		assertTrue(ffV1.isValidIdentifierName(validName));
		assertTrue(ff.isValidIdentifierName(validName));
		assertTrue(LIST_FAC.isValidIdentifierName(validName));

		final String nameInV1Only = "partition";
		assertTrue(ffV1.isValidIdentifierName(nameInV1Only));
		assertFalse(ff.isValidIdentifierName(nameInV1Only));
		assertFalse(LIST_FAC.isValidIdentifierName(nameInV1Only));

		final String typeConstructorName = "List";
		assertTrue(ffV1.isValidIdentifierName(typeConstructorName));
		assertTrue(ff.isValidIdentifierName(typeConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(typeConstructorName));

		final String valueConstructorName = "cons";
		assertTrue(ffV1.isValidIdentifierName(valueConstructorName));
		assertTrue(ff.isValidIdentifierName(valueConstructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(valueConstructorName));

		final String destructorName = "head";
		assertTrue(ffV1.isValidIdentifierName(destructorName));
		assertTrue(ff.isValidIdentifierName(destructorName));
		assertFalse(LIST_FAC.isValidIdentifierName(destructorName));
	}

}
