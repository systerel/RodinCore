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

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Unit tests for specialization of types and type environments.
 * 
 * @author Thomas Muller
 */
public class TestTypeAndTypeEnvSpecialization extends AbstractTests {

	private static final BooleanType BOOL = ff.makeBooleanType();
	private static final GivenType S = ff.makeGivenType("S");
	private static final PowerSetType POWER_S = ff.makePowerSetType(S);

	private static final GivenType T = ff.makeGivenType("T");

	private static final Type Z = ff.makeIntegerType();
	private static final PowerSetType POWER_Z = ff.makePowerSetType(Z);

	// private static final FreeIdentifier a = mFreeIdentifier("a", S);
	// private static final FreeIdentifier b = mFreeIdentifier("b", S);
	// private static final Expression one = mIntegerLiteral(1);
	// private static final FreeIdentifier untyped = mFreeIdentifier("untyped");

	final ISpecialization spec = ff.makeSpecialization();

	/**
	 * Ensures that a given type specialized by itself remains unchanged.
	 */
	public void testGivenTypeIdSpecialization() {
		try {
			spec.put(S, S);
			final Type specializedS = S.specialize(spec);
			assertSame(S, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	/**
	 * Ensures that a given type specialized by an empty specialization remains
	 * unchanged.
	 */
	public void testGivenTypeNoSpecialization() {
		try {
			final Type specializedS = S.specialize(spec);
			assertSame(S, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	/**
	 * Ensures that a given type specialized by a specialization that doesn't
	 * concerns the given type remains unchanged.
	 */
	public void testGivenTypeNoMatchingSpecialization() {
		try {
			spec.put(T, S);
			final Type specializedS = S.specialize(spec);
			assertSame(S, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	/**
	 * Ensures that a simple given type specialization succeeds.
	 */
	public void testGivenTypeSpecialization() {
		try {
			spec.put(S, T);
			final Type specializedS = S.specialize(spec);
			assertEquals(T, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a given type specialization to integer type succeeds.
	 */
	public void testGivenTypeToIntegerTypeSpecialization() {
		try {
			spec.put(S, Z);
			final Type specializedS = S.specialize(spec);
			assertEquals(Z, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a given type specialization to integer type succeeds.
	 */
	public void testGivenTypeToBooleanTypeSpecialization() {
		try {
			spec.put(S, BOOL);
			final Type specializedS = S.specialize(spec);
			assertEquals(BOOL, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a given type to powerset type specialization succeeds.
	 */
	public void testGivenTypeToPowerSetTypeSpecialization() {
		try {
			spec.put(S, POWER_Z);
			final Type specializedS = S.specialize(spec);
			assertEquals(POWER_Z, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a powerset type to powerset type specialization succeeds.
	 */
	public void testPowerSetTypeSpecialization() {
		try {
			spec.put(S, T);
			final Type specializedS = POWER_S.specialize(spec);
			assertEquals(ff.makePowerSetType(T), specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	/**
	 * Ensures that a powerset type specialization to integer type succeeds.
	 */
	public void testPowerSetToIntegerTypeSpecialization() {
		try {
			spec.put(S, Z);
			final Type specializedS = POWER_S.specialize(spec);
			assertEquals(POWER_Z, specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a powerset type specialization to boolean type succeeds.
	 */
	public void testPowerSetToBooleanTypeSpecialization() {
		try {
			spec.put(S, BOOL);
			final Type specializedS = POWER_S.specialize(spec);
			assertEquals(ff.makePowerSetType(BOOL), specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a powerset type specialization to a complex powerset type
	 * succeeds.
	 */
	public void testPowerSetTypeToPowerSetSpecialization() {
		try {
			spec.put(S, POWER_Z);
			final Type specializedS = POWER_S.specialize(spec);
			assertEquals(ff.makePowerSetType(POWER_Z), specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a powerset type specialization to a product type
	 * succeeds.
	 */
	public void testPowerSetTypeToProductTypeSpecialization() {
		try {
			final ProductType prodType = ff.makeProductType(S, T);
			spec.put(S, prodType);
			final Type specializedS = POWER_S.specialize(spec);
			assertEquals(ff.makePowerSetType(prodType), specializedS);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	// FIXME TO BE COMPLETED...

}
