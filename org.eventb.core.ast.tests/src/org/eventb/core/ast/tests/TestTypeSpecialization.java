/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.eventb.core.ast.tests.FastFactory.mTypeSpecialization;
import static org.eventb.core.ast.tests.datatype.TestDatatypes.MOULT_FAC;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.junit.Test;

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
	@Test 
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
	@Test 
	public void testBooleanType() {
		assertSpecialization("BOOL", "S := T", "BOOL");
	}

	/**
	 * Ensures that the integer type is never specialized.
	 */
	@Test 
	public void testIntegerType() {
		assertSpecialization("ℤ", "S := T", "ℤ");
	}

	/**
	 * Ensures that a power set type can be specialized in various ways.
	 */
	@Test 
	public void testPowerSetType() {
		assertSpecialization("ℙ(S)", "", "ℙ(S)");
		assertSpecialization("ℙ(S)", "S := T", "ℙ(T)");
	}

	/**
	 * Ensures that a product type can be specialized in various ways.
	 */
	@Test 
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
	@Test 
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

	/**
	 * Ensures that specializing a type remembers the types that are not
	 * substituted.
	 */
	@Test 
	public void bug727() {
		final Type src = parseType("List(S)", LIST_FAC);
		final ISpecialization spe = LIST_FAC.makeSpecialization();
		assertSame(src, src.specialize(spe));
		final GivenType s = LIST_FAC.makeGivenType("S");
		final Type z = LIST_FAC.makeIntegerType();
		try {
			spe.put(s, z);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

	}

	private static void assertSpecialization(String typeImage,
			String typeSpecializationImage, String expectedImage) {
		assertSpecialization(typeImage, typeSpecializationImage, expectedImage,
				ff);
	}

	private static void assertSpecialization(String typeImage,
			String typeSpecImage, String expectedImage,
			FormulaFactory fac) {
		final Type type = parseType(typeImage, fac);
		final ITypeEnvironmentBuilder te = fac.makeTypeEnvironment();
		addGivenSets(te, type);
		final ISpecialization spe = mTypeSpecialization(te, typeSpecImage);
		final Type expected = parseType(expectedImage, fac);
		final Type actual = type.specialize(spe);
		assertEquals(expected, actual);
		// If the specialization did not change the type, it should be the
		// same object
		if (expected.equals(type)) {
			assertSame(type, actual);
		}
	}

	private static void addGivenSets(ITypeEnvironmentBuilder te, Type type) {
		for (final GivenType gt : type.getGivenTypes()) {
			te.addGivenSet(gt.getName());
		}
	}

}
