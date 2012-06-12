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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
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
	private static final GivenType U = ff.makeGivenType("U");
	private static final GivenType V = ff.makeGivenType("V");
	
	private static final Type Z = ff.makeIntegerType();
	private static final PowerSetType POWER_Z = ff.makePowerSetType(Z);
	private static final ProductType PRODUCT_T_U = ff.makeProductType(T, U);

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
	 * Ensures that a given type to product type specialization succeeds.
	 */
	public void testGivenTypeToProductTypeSpecialization() {
		try {
			spec.put(S, PRODUCT_T_U);
			final Type specializedS = S.specialize(spec);
			assertEquals(PRODUCT_T_U, specializedS);
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
	
	/**
	 * Ensures that a given type appearing as left member of a product type is
	 * successfully spezialized.
	 */
	public void testProductTypeLeftSpecialization() {
		try {
			final ProductType pType = ff.makeProductType(S, T);
			spec.put(S, U);
			final Type specialType = pType.specialize(spec);
			assertEquals(ff.makeProductType(U, T), specialType);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}

	/**
	 * Ensures that a given type appearing as right member of a product type is
	 * successfully spezialized.
	 */
	public void testProductTypeRightSpecialization() {
		try {
			final ProductType pType = ff.makeProductType(T, S);
			spec.put(S, U);
			final Type specialType = pType.specialize(spec);
			assertEquals(ff.makeProductType(T, U), specialType);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that given types appearing as left and right members of a product
	 * type are successfully spezialized.
	 */
	public void testProductTypeLeftRightSpecialization() {
		try {
			final ProductType pType = ff.makeProductType(S, T);
			spec.put(S, U);
			spec.put(T, V);
			final Type specialType = pType.specialize(spec);
			assertEquals(ff.makeProductType(U, V), specialType);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that given types appearing as type parameters of a parametric
	 * type are successfully spezialized.
	 */
	public void testParametricTypeSpecialization() {
		try {
			final List<Type> types = new ArrayList<Type>();
			types.add(S);
			types.add(T);
			types.add(U);
			final Type paramType = LIST_FAC.makeParametricType(types,
					LIST_DT.getTypeConstructor());
			spec.put(S, T);
			spec.put(T, U);
			spec.put(U, V);
			final Type s = paramType.specialize(spec);
			assertTrue(s instanceof ParametricType);
			final ParametricType spType = (ParametricType) s;
			final Type[] typeParameters = spType.getTypeParameters();
			assertTrue(typeParameters.length == 3);
			assertEquals(T, typeParameters[0]);
			assertEquals(U, typeParameters[1]);
			assertEquals(V, typeParameters[2]);
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that a specialized given type S disappears when not used. 
	 */
	public void testTEWithASpecifiedUnusedGivenType() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("S");
			spec.put(S, T);
			final ITypeEnvironment sdTypeEnv = typeEnv
					.specialize(spec);
			assertNull(sdTypeEnv.getType("S"));
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that given type S is specialized when used indirectly by a free
	 * identifier of the type environment.
	 */
	public void testTEWithASpecifiedUsedGivenType() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("S");
			final FreeIdentifier a = ff.makeFreeIdentifier("a", null, S);
			typeEnv.add(a);
			spec.put(S, T);
			final ITypeEnvironment sdTypeEnv = typeEnv.specialize(spec);
			assertNull(sdTypeEnv.getType("S"));
			assertEquals(sdTypeEnv.getType("T"), ff.makePowerSetType(T));
			assertEquals(T, sdTypeEnv.getType("a"));
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that given type T is maintained when used indirectly by a free
	 * identifier that has been specialized.
	 */
	public void testTEWithASpecifiedIdentifier() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("T");
			final FreeIdentifier a = ff.makeFreeIdentifier("a", null, T);
			typeEnv.add(a);
			spec.put(a, ff.makeFreeIdentifier("b", null, T));
			final ITypeEnvironment sdTypeEnv = typeEnv.specialize(spec);
			assertEquals(ff.makePowerSetType(T), sdTypeEnv.getType("T"));
			assertEquals(T, sdTypeEnv.getType("b"));
			assertNull(sdTypeEnv.getType("a"));
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that given type S is specialized when used indirectly by a free
	 * identifier of the type environment, that an identifier involving S is
	 * Specialized in parallel and that S disappear as well as the occurrence of
	 * the free identifiers that have been renamed (and specialized).
	 * 
	 * Original typeEnv : S |-> POW(S); a |-> S ; c |-> S x INT
	 * 
	 * Specialization : S --> T ; a (oftype S) --> b (oftype T)
	 * 
	 * Specialized typeEnv : S disappeared ; a was renamed into b ; 
	 * 						 T |-> POW(T) ; b |-> T ; c |-> T x INT
	 */
	public void testTEWithSpecifiedGivenTypeAndIdent() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("S");
			final FreeIdentifier a = ff.makeFreeIdentifier("a", null, S);
			typeEnv.add(a);
			final FreeIdentifier c = ff.makeFreeIdentifier("c", null,
					ff.makeProductType(S, Z));
			typeEnv.add(c);
			spec.put(S, T);
			spec.put(a, ff.makeFreeIdentifier("b", null, T));
			final ITypeEnvironment sdTypeEnv = typeEnv.specialize(spec);
			assertNull(sdTypeEnv.getType("S"));
			assertEquals(ff.makePowerSetType(T), sdTypeEnv.getType("T"));
			assertNull(sdTypeEnv.getType("a"));
			assertEquals(T, sdTypeEnv.getType("b"));
			assertEquals(ff.makeProductType(T, Z), sdTypeEnv.getType("c"));
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Ensures that an identifier of given set can not be considered as a type
	 * and not a type at the same time.
	 * 
	 * Original typeEnv : S |-> POW(S)
	 * 
	 * 					  T |-> S x U
	 * 					  a |-> S x T
	 * 
	 * Specialization :   S --> T 
	 * 				      T --> S x T
	 * 
	 * Specialized typeEnv : S disappeared
	 * 						 T |-> POW(T)
	 * 						 a |-> T x (S x T)
	 */
	public void testTEWithAComplexSpecifiedGivenType() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("S");
			typeEnv.addGivenSet("T");
			final ProductType sxt = ff.makeProductType(S, T);
			final FreeIdentifier a = ff.makeFreeIdentifier("a", null, sxt);
			typeEnv.add(a);
			spec.put(S, T);
			spec.put(T, sxt);
			final ITypeEnvironment sdTypeEnv = typeEnv.specialize(spec);
			assertNull(sdTypeEnv.getType("S"));
			assertEquals(sdTypeEnv.getType("T"), ff.makePowerSetType(T));
			assertEquals(ff.makeProductType(T, sxt), sdTypeEnv.getType("a"));
		} catch (IllegalArgumentException e) {
			fail("Should not have raised an exception");
		}
	}
	
	/**
	 * Robustness non-regression test ensuring that an invalid type environment
	 * in which given types both as given types and free identifiers can not be
	 * specialized.
	 */
	public void testInvalidTESpecialization() {
		try {
			final ITypeEnvironment typeEnv = ff.makeTypeEnvironment();
			typeEnv.addGivenSet("S");
			final FreeIdentifier t = ff.makeFreeIdentifier("T", null, U);
			typeEnv.add(t);
			final ProductType sxt = ff.makeProductType(S, T);
			final FreeIdentifier a = ff.makeFreeIdentifier("a", null, sxt);
			typeEnv.add(a);
			spec.put(S, T); // consider T as a given type
			final ProductType sxu = ff.makeProductType(S, U);
			spec.put(T, sxu); // consider T as a free identifier
			typeEnv.specialize(spec);
			fail("Should have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

}
