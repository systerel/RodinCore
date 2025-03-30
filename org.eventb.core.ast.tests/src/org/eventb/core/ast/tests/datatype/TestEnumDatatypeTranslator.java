/*******************************************************************************
 * Copyright (c) 2012, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.datatype;

import org.eventb.core.ast.tests.AbstractTranslatorTests;
import org.junit.Test;

/**
 * Tests for the enumeration datatype translation.<br>
 * These tests check that the enumeration datatype constructors are translated
 * into fresh free identifiers of a new type which corresponds to the datatype
 * constructor, and that the partition predicate associated to the translation
 * is valid.
 * 
 * @author Thomas Muller
 */
public class TestEnumDatatypeTranslator extends AbstractTranslatorTests {

	/**
	 * Case where there is one enumerated element.
	 */
	@Test 
	public void testOneEnumTypeConstructorTranlator() {
		final TestTranslationSupport s = mSupport("basicEnum ::= one");
		s.setExpectedTypeEnvironment("basicEnum=ℙ(basicEnum); one=basicEnum");
		s.assertExprTranslation("basicEnum", "basicEnum");
		s.assertAxioms("partition(basicEnum, {one})");
	}

	/**
	 * Case where there are three enumerated elements.
	 */
	@Test 
	public void testEnumConstructorTranslator() {
		final TestTranslationSupport s = mSupport("basicEnum ::= one ||"
				+ " two || three");
		s.setExpectedTypeEnvironment("basicEnum=ℙ(basicEnum); one=basicEnum;"
				+ " two=basicEnum; three=basicEnum");
		s.assertExprTranslation("one", "one");
		s.assertAxioms("partition(basicEnum, {one}, {two}, {three})");
	}

}
