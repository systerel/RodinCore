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
	public void testOneEnumTypeConstructorTranlator() {
		final TestTranslationSupport s = mSupport("basicEnum ::= one");
		s.setExpectedTypeEnvironment("basicEnum=ℙ(basicEnum); one=basicEnum");
		s.assertExprTranslation("basicEnum", "basicEnum");
		s.assertAxioms("partition(basicEnum, {one})");
	}

	/**
	 * Case where there are three enumerated elements.
	 */
	public void testEnumConstructorTranslator() {
		final TestTranslationSupport s = mSupport("basicEnum ::= one ||"
				+ " two || three");
		s.setExpectedTypeEnvironment("basicEnum=ℙ(basicEnum); one=basicEnum;"
				+ " two=basicEnum; three=basicEnum");
		s.assertExprTranslation("one", "one");
		s.assertAxioms("partition(basicEnum, {one}, {two}, {three})");
	}

	/**
	 * Robustness test case : the enumerated datatype has a useless type
	 * parameter which is instantiated twice.
	 */
	public void testEnumWithTypeParamsTranlator() {
		final TestTranslationSupport s = mSupport("basicEnum[T] ::= one || two");
		s.setExpectedTypeEnvironment("basicEnum=ℙ(basicEnum);"
				+ " basicEnum0=ℙ(basicEnum0)");
		s.assertExprTranslation("basicEnum(ℤ)", "basicEnum");
		s.assertExprTranslation("basicEnum(1‥3)", "basicEnum");
		s.assertExprTranslation("basicEnum(BOOL)", "basicEnum0");
		s.assertExprTranslation("basicEnum({TRUE})", "basicEnum0");
		s.assertAxioms("partition(basicEnum, {one}, {two})",
				"partition(basicEnum0, {one0}, {two0})");
	}

}