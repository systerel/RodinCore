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

import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.TestGenParser.MOULT_FAC;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Unit tests for specialization of types. We check various combinations of
 * types and specialization to ensure that types are correctly substituted.
 * <p>
 * All tests are of the form of a triplet (original type, specialization, result
 * type).
 * </p>
 * 
 * @author Thomas Muller
 */
public class TestTypeSpecialization extends AbstractTests {

	/**
	 * Ensures that a given type can be specialized in various ways.
	 */
	public void testGivenType() {
		assertSpecialization("S", "", "S");
		assertSpecialization("S", "S := S", "S");
		assertSpecialization("S", "T := U", "S");
		assertSpecialization("S", "S := T", "T");
		assertSpecialization("S", "S := ℤ", "ℤ");
		assertSpecialization("S", "S := BOOL", "BOOL");
		assertSpecialization("S", "S := ℙ(S)", "ℙ(S)");
	}

	/**
	 * Ensures that the boolean type is never specialized.
	 */
	public void testBooleanType() {
		assertSpecialization("BOOL", "S := T", "BOOL");
	}

	/**
	 * Ensures that the integer type is never specialized.
	 */
	public void testIntegerType() {
		assertSpecialization("ℤ", "S := T", "ℤ");
	}

	/**
	 * Ensures that a power set type can be specialized in various ways.
	 */
	public void testPowerSetType() {
		assertSpecialization("ℙ(S)", "", "ℙ(S)");
		assertSpecialization("ℙ(S)", "S := T", "ℙ(T)");
	}

	/**
	 * Ensures that a product type can be specialized in various ways.
	 */
	public void testProductType() {
		assertSpecialization("S×T", "", "S×T");
		assertSpecialization("S×T", "S := U", "U×T");
		assertSpecialization("S×T", "T := V", "S×V");
		assertSpecialization("S×T", "S := U || T := V", "U×V");
		assertSpecialization("S×T", "S := T", "T×T");
		assertSpecialization("S×T", "S := T || T := S", "T×S");
	}

	/**
	 * Ensures that parametric types can be specialized in various ways.
	 */
	public void testParametricType() {
		assertSpecialization("List(S)", "", "List(S)", LIST_FAC);
		assertSpecialization("List(S)", "S := T", "List(T)", LIST_FAC);

		assertSpecialization("Moult(S,T)", "", "Moult(S,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := U", "Moult(U,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "T := V", "Moult(S,V)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := U || T := V", "Moult(U,V)",
				MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := T", "Moult(T,T)", MOULT_FAC);
		assertSpecialization("Moult(S,T)", "S := T || T := S", "Moult(T,S)",
				MOULT_FAC);
	}

	private static void assertSpecialization(String typeImage,
			String typeSpecializationImage, String expectedImage) {
		assertSpecialization(typeImage, typeSpecializationImage, expectedImage,
				ff);
	}

	private static void assertSpecialization(String typeImage,
			String typeSpecializationImage, String expectedImage,
			FormulaFactory fac) {
		final Type type = parseType(typeImage, fac);
		final ITypeEnvironment te = fac.makeTypeEnvironment();
		addGivenSets(te, type);
		final ISpecialization spe = mSpecialization(te, typeSpecializationImage);
		final Type expected = parseType(expectedImage, fac);
		final Type actual = type.specialize(spe);
		assertEquals(expected, actual);
		// If the specialization did not change the type, it should be the
		// same object
		if (expected.equals(type)) {
			assertSame(type, actual);
		}
	}

	private static void addGivenSets(ITypeEnvironment te, Type type) {
		for (final GivenType gt : type.getGivenTypes()) {
			te.addGivenSet(gt.getName());
		}
	}

}
