/*******************************************************************************
 * Copyright (c) 2010, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - added tests related to predicate variables
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.junit.Test;

/**
 * Unit tests about specialization creation. These tests ensure that all
 * precondition of specialization methods are indeed implemented.
 * 
 * @author Laurent Voisin
 * @author htson - added tests related to predicate variables
 */
public class TestSpecialization extends AbstractTests {

	private static final Type Z = ff.makeIntegerType();
	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

	private static final FreeIdentifier aS = mFreeIdentifier("a", S);
	private static final FreeIdentifier aT = mFreeIdentifier("a", T);
	private static final FreeIdentifier bS = mFreeIdentifier("b", S);
	private static final FreeIdentifier bT = mFreeIdentifier("b", T);

	private static final Expression one = mIntegerLiteral(1);
	private static final Expression two = mIntegerLiteral(2);
	private static final Expression expTrue = mBoolExpression(mLiteralPredicate(BTRUE));
	private static final FreeIdentifier untyped = mFreeIdentifier("untyped");

	private static final PredicateVariable P = ff.makePredicateVariable("$P", null);
	private static final PredicateVariable Q = ff.makePredicateVariable("$Q", null);
	private static final Predicate untypedPred = ff.makeRelationalPredicate(Predicate.EQUAL, untyped, one, null);
	
	private final ISpecialization spec = ff.makeSpecialization();

	/**
	 * Ensures that a null given type is rejected.
	 */
	@Test 
	public void testNullGivenType() {
		try {
			spec.put(null, Z);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a null type value is rejected.
	 */
	@Test 
	public void testNullTypeValue() {
		try {
			spec.put(S, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a type value from another factory is rejected.
	 */
	@Test 
	public void testWrongFactoryTypeValue() {
		try {
			spec.put(S, LIST_FAC.makeIntegerType());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a type substitution which is overridden is rejected.
	 */
	@Test 
	public void testOverriddenType() {
		spec.put(S, Z);
		try {
			spec.put(S, T);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a inserting a type substitution identical to one already
	 * registered is accepted.
	 */
	@Test 
	public void testOverridenSameType() {
		spec.put(S, Z);
		spec.put(S, Z);
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a null identifier is rejected.
	 */
	@Test 
	public void testNullIdentifier() {
		try {
			spec.put(null, one);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an untyped identifier is rejected.
	 */
	@Test 
	public void testUntypedIdentifier() {
		try {
			spec.put(untyped, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier that denotes a given type is rejected, if not
	 * already registered as a type substitution.
	 */
	@Test 
	public void testGivenTypeIdentifier() {
		try {
			spec.put(S.toExpression(), Z.toExpression());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier that denotes a given type is accepted, if it
	 * has already been registered as a type substitution.
	 */
	@Test 
	public void testGivenTypeIdentifierAlready() {
		spec.put(S, Z);
		spec.put(S.toExpression(), Z.toExpression());
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a null expression is rejected.
	 */
	@Test 
	public void testNullExpression() {
		try {
			spec.put(aS, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an expression from another factory is rejected.
	 */
	@Test 
	public void testWrongFactoryExpression() {
		spec.put(S, Z);
		try {
			spec.put(aS, LIST_FAC.makeIntegerLiteral(ZERO, null));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that an untyped expression is rejected.
	 */
	@Test 
	public void testUntypedExpression() {
		try {
			spec.put(aS, untyped);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier substitution which changes type when no type
	 * substitution has been registered is rejected. Moreover, there is no
	 * side-effect performed during the check.
	 */
	@Test 
	public void testIncompatibleTypeNoTypeSubstitution() {
		try {
			spec.put(aS, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier substitution which is not compatible with the
	 * type substitution is rejected.
	 */
	@Test 
	public void testIncompatibleTypeWithTypeSubstitution() {
		spec.put(S, Z);
		try {
			spec.put(aS, expTrue);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that the type of the expression is checked within complex
	 * replacement types.
	 */
	@Test 
	public void testIncompatibleComplexType() {
		spec.put(S, ff.makePowerSetType(Z));
		try {
			spec.put(aS, ff.makeSetExtension(expTrue, null));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("S=ℙ(S)", "S := ℙ(ℤ)", "");
	}

	/**
	 * Ensures that an identifier substitution for which the replacement
	 * expression is of the same type as the given identifier is accepted.
	 * Moreover, the given sets occurring in the type shall also be added.
	 */
	@Test 
	public void testSameTypeIdentSubstitution() {
		spec.put(aS, bS);
		assertSpecialization("a=S", "a := b", "b=S");
	}

	/**
	 * Ensures that registering two substitution for the same identifier but
	 * with different expressions is rejected.
	 */
	@Test 
	public void testOverridenExpression() {
		spec.put(S, Z);
		spec.put(aS, one);
		try {
			spec.put(aS, two);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("a=S", "S := ℤ || a := 1", "");
	}

	/**
	 * Ensures that registering twice the same identifier substitution is
	 * accepted.
	 */
	@Test 
	public void testOverridenSameExpression() {
		spec.put(S, Z);
		spec.put(aS, one);
		spec.put(aS, one);
		assertSpecialization("a=S", "S := ℤ || a := 1", "");
	}

	/**
	 * Ensures that a type substitution which is not compatible with the
	 * identifier substitutions is rejected.
	 */
	@Test 
	public void testIncompatibleTypeSubstitution() {
		spec.put(aS, bS);
		try {
			spec.put(S, Z);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("a=S", "a := b", "b=S");
	}

	/**
	 * Ensures that an identifier substitution which contains an ill-formed
	 * expression is accepted.
	 */
	@Test
	public void testIllFormedExpression() {
		spec.put(aS, ff.makeBoundIdentifier(0, null, S));
		// Cannot check the specialization with the current framework
	}

	/**
	 * Ensures that types can be swapped in a specialization.
	 */
	@Test 
	public void testTypeSwap() {
		spec.put(S, T);
		spec.put(T, S);
		assertSpecialization("S=ℙ(S); T=ℙ(T)", "S := T || T := S", "");
	}

	/**
	 * Ensures that identifiers can be swapped in a specialization.
	 */
	@Test 
	public void testIdentSwap() {
		spec.put(S, T);
		spec.put(aS, bT);
		spec.put(bS, aT);
		assertSpecialization("a=S; b=S", //
				"S := T || a := b || b := a", //
				"a=T; b=T");
	}

	/**
	 * Ensures that both types and identifiers can be swapped in a
	 * specialization.
	 */
	@Test 
	public void testBothSwap() {
		spec.put(S, T);
		spec.put(T, S);
		spec.put(aS, bT);
		spec.put(bT, aS);
		assertSpecialization("a=S; b=T", //
				"S := T || T := S || a := b || b := a", //
				"a=S; b=T");
	}

	/**
	 * Ensures that both types and identifiers can be swapped in a
	 * specialization, entering substitutions alternatively.
	 */
	@Test 
	public void testBothSwapMixed() {
		spec.put(S, T);
		spec.put(aS, bT);
		spec.put(T, S);
		spec.put(bT, aS);
		assertSpecialization("a=S; b=T", //
				"S := T || T := S || a := b || b := a", //
				"a=S; b=T");
	}

	/**
	 * Ensures that {@link ISpecialization#getFactory()} returns the right
	 * factory.
	 */
	@Test
	public void testGetFactory() {
		assertSame(ff, spec.getFactory());
		assertSame(LIST_FAC, LIST_FAC.makeSpecialization().getFactory());
		assertEmptySpecialization();
	}

	/**
	 * Ensures that one can retrieve a substitution.
	 */
	@Test
	public void testGetForTypeWithSubstitution() {
		spec.put(S, T);
		assertEquals(T, spec.get(S));
		assertSpecialization("S=ℙ(S)", "S := T", "T=ℙ(T)");
	}

	/**
	 * Ensures that one gets null if there is no substitution and that no
	 * substitution is created as a side-effect.
	 */
	@Test
	public void testGetForTypeWithoutSubstitution() {
		assertNull(spec.get(S));
		assertNull(spec.get(S));
		assertEmptySpecialization();
	}

	/**
	 * Ensures that one can retrieve a substitution.
	 */
	@Test
	public void testGetForIdentWithSubstitution() {
		spec.put(aS, bS);
		assertEquals(bS, spec.get(aS));
		assertSpecialization("a=S", "a := b", "b=S");
	}

	/**
	 * Ensures that one can retrieve a substitution for an identifier
	 * corresponding to a substituted type.
	 */
	@Test
	public void testGetForIdentWithTypeSubstitution() {
		spec.put(S, T);
		assertEquals(T.toExpression(), spec.get(S.toExpression()));
		assertSpecialization("S=ℙ(S)", "S := T", "T=ℙ(T)");
	}

	/**
	 * Ensures that one gets null if there is no substitution and that no
	 * substitution is created as a side-effect.
	 */
	@Test
	public void testGetForIdentWithoutSubstitution() {
		assertNull(spec.get(aS));
		assertNull(spec.get(aS));
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a null given type is rejected.
	 * 
	 * @author htson
	 */
	@Test
	public void testCanPut_NullGivenType() {
		try {
			spec.canPut(null, Z);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a null type value is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_NullTypeValue() {
		try {
			spec.canPut(S, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a type value from another factory is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_WrongFactoryTypeValue() {
		try {
			spec.canPut(S, LIST_FAC.makeIntegerType());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a type substitution which is overridden is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_OverriddenType() {
		spec.put(S, Z);
		boolean ok = spec.canPut(S, T);
		assertFalse("Should reject overriding type substitution", ok);
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a inserting a type substitution identical to one already
	 * registered is accepted.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_OverridenSameType() {
		spec.put(S, Z);
		boolean ok = spec.canPut(S, Z);
		assertTrue(
				"Should accept overriding type substitution with the same identical substitution",
				ok);
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a type substitution which is not compatible with the
	 * identifier substitutions is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_IncompatibleTypeSubstitution() {
		spec.put(aS, bS);
		boolean ok = spec.canPut(S, Z);
		assertFalse("Should reject incompatible type substitution", ok);
		assertSpecialization("a=S", "a := b", "b=S");
	}

	/**
	 * Ensures that types can be swapped in a specialization.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_TypeSwap() {
		spec.put(S, T);
		boolean ok = spec.canPut(T, S);
		assertTrue("Should accept instantiations that swapping types", ok);
		assertSpecialization("S=ℙ(S)", "S := T", "T=ℙ(T)");
	}

	/**
	 * Ensures that a null identifier is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_NullIdentifier() {
		try {
			spec.canPut(null, one);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an untyped identifier is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_UntypedIdentifier() {
		try {
			spec.canPut(untyped, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier that denotes a given type is rejected, if not
	 * already registered as a type substitution.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_GivenTypeIdentifier() {
		boolean ok = spec.canPut(S.toExpression(), Z.toExpression());
		assertFalse(
				"Should reject instantiation for an identifier denoting a given type if it is NOT yet registered",
				ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier that denotes a given type is accepted, if it
	 * has already been registered as a type substitution.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_GivenTypeIdentifierAlready() {
		spec.put(S, Z);
		boolean ok = spec.canPut(S.toExpression(), Z.toExpression());
		assertTrue("Should accept instantiation for an identifier denoting a given type if it is registered", ok);
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a null expression is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_NullExpression() {
		try {
			spec.canPut(aS, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an expression from another factory is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_WrongFactoryExpression() {
		spec.put(S, Z);
		try {
			spec.canPut(aS, LIST_FAC.makeIntegerLiteral(ZERO, null));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that an untyped expression is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_UntypedExpression() {
		try {
			spec.canPut(aS, untyped);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier substitution which changes type when no type
	 * substitution has been registered is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_IncompatibleTypeNoTypeSubstitution() {
		boolean ok = spec.canPut(aS, one);
		assertFalse("Should reject the proposed free identifier instantiation",
				ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an identifier substitution which is not compatible with the
	 * type substitution is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_IncompatibleTypeWithTypeSubstitution() {
		spec.put(S, Z);
		boolean ok = spec.canPut(aS, expTrue);
		assertFalse("Should reject the proposed free identifier instantiation",
				ok);
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}


	/**
	 * Ensures that the type of the expression is checked within complex
	 * replacement types.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_IncompatibleComplexType() {
		spec.put(S, ff.makePowerSetType(Z));
		boolean ok = spec.canPut(aS, ff.makeSetExtension(expTrue, null));
		assertFalse(
				"Should reject free identifier substitution with incompatible complex type",
				ok);
		assertSpecialization("S=ℙ(S)", "S := ℙ(ℤ)", "");
	}

	/**
	 * Ensures that an identifier substitution for which the replacement
	 * expression is of same type as the given identifier is accepted.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_SameTypeIdentSubstitution() {
		boolean ok = spec.canPut(aS, bS);
		assertTrue(
				"Should accept free identifier substitution with the same type",
				ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that registering two substitution for the same identifier but
	 * with different expressions is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_OverridenExpression() {
		spec.put(S, Z);
		spec.put(aS, one);
		boolean ok = spec.canPut(aS, two);
		assertFalse(
				"Should reject substitution for the same identifier with a different expression value",
				ok);
		assertSpecialization("a=S", "S := ℤ || a := 1", "");
	}

	/**
	 * Ensures that registering twice the same identifier substitution is
	 * accepted.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_OverridenSameExpression() {
		spec.put(S, Z);
		spec.put(aS, one);
		boolean ok = spec.canPut(aS, one);
		assertTrue(
				"Should accept substitution for the same identifier with the same expression value",
				ok);
		assertSpecialization("a=S", "S := ℤ || a := 1", "");
	}

	/**
	 * Ensures that an identifier substitution which contains an ill-formed
	 * expression is accepted.
	 * 
	 * @author htson
	 */
	@Test
	public void testCanPut_IllFormedExpression() {
		boolean ok = spec.canPut(aS, ff.makeBoundIdentifier(0, null, S));
		assertTrue(
				"Should accept identifier substitution with an ill-formed expression",
				ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that identifiers can be swapped in a specialization.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_IdentSwap() {
		spec.put(S, T);
		spec.put(aS, bT);
		boolean ok = spec.canPut(bS, aT);
		assertTrue("Should accept substitution that swapping identifiers", ok);
		assertSpecialization("a=S", "S := T || a := b", "b=T");
	}

	/**
	 * Ensures that both types and identifiers can be swapped in a
	 * specialization.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_BothSwap() {
		spec.put(S, T);
		spec.put(T, S);
		spec.put(aS, bT);
		boolean ok = spec.canPut(bT, aS);
		assertTrue("Should accept substitution that swapping identifiers and their types", ok);
		assertSpecialization("a=S; T=ℙ(T)", //
				"S := T || T := S || a := b", //
				"b=T");
	}

	/**
	 * Ensures that both types and identifiers can be swapped in a
	 * specialization, entering substitutions alternatively.
	 * 
	 * @author htson
	 */
	@Test 
	public void testCanPut_BothSwapMixed() {
		spec.put(S, T);
		spec.put(aS, bT);
		spec.put(T, S);
		boolean ok = spec.canPut(bT, aS);
		assertTrue("Should accept substitution that swapping identifiers and their types", ok);
		assertSpecialization("a=S; T=ℙ(T)", //
				"S := T || T := S || a := b", //
				"b=T");
	}

	/**
	 * Ensures that if the canPut test fails, then no substitution has been
	 * added as a side-effect.
	 */
	@Test 
	public void testCanPutFailure_NoSideEffect() {
		boolean ok = spec.canPut(aS, one);
		assertFalse("Should have failed", ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that if the canPut test fails, then no substitution has been
	 * added as a side-effect.
	 */
	@Test 
	public void testCanPutFailure_NoSideEffectComplexType() {
		final FreeIdentifier aST = mFreeIdentifier("a", REL(S, POW(T)));
		boolean ok = spec.canPut(aST, one);
		assertFalse("Should have failed", ok);
		assertEmptySpecialization();
	}


	/**
	 * Ensures that if the canPut test succeeds, then no substitution has been
	 * added as a side-effect.
	 */
	@Test 
	public void testCanPutSuccess_NoSideEffect() {
		boolean ok = spec.canPut(aS, bS);
		assertTrue("Should have succeeded", ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that if the canPut test succeeds, then no substitution has been
	 * added as a side-effect.
	 */
	@Test 
	public void testCanPutSuccess_NoSideEffectComplexType() {
		final FreeIdentifier aST = mFreeIdentifier("a", REL(S, POW(T)));
		final FreeIdentifier bST = mFreeIdentifier("b", REL(S, POW(T)));
		boolean ok = spec.canPut(aST, bST);
		assertTrue("Should have succeeded", ok);
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a null predicate variable is rejected.
	 * 
	 * @author htson
	 */
	@Test
	public void testPut_NullPredicateVariable() {
		try {
			spec.put(null, Q);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}		
		assertEmptySpecialization();
	}

	/**
	 * Ensures that a null predicate value is rejected.
	 * 
	 * @author htson
	 */
	@Test
	public void testPut_NullPredicateValue() {
		try {
			spec.put(P, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}		
		assertEmptySpecialization();
	}

	/**
	 * Ensures that an untyped predicate is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPut_UntypedPredicate() {
		try {
			spec.put(P, untypedPred);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}
	
	/**
	 * Ensures that a typed-predicate from another factory is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPut_WrongFactoryPredicateValue() {
		try {
			spec.put(P, LIST_FAC.makeLiteralPredicate(BTRUE, null));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
		assertEmptySpecialization();
	}

	/**
	 * Ensures that registering two substitution for the same predicate variable
	 * but with different predicates is rejected.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPut_OverridenPredicate() {
		spec.put(P, Q);
		boolean ok = spec.put(P,
				ff.makeRelationalPredicate(Predicate.EQUAL, aT, bT, null));
		assertFalse(
				"Should reject substitution for the same predicate variable with a different predicate value",
				ok);
		assertSpecialization("", "$P := $Q", "");
	}

	/**
	 * Ensures that registering twice the same predicate variable substitution
	 * is accepted.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPut_OverridenSamePredicate() {
		spec.put(P, Q);
		boolean ok = spec.put(P, Q);
		assertTrue(
				"Should accept substitution for the same predicate variable with the same predicate value",
				ok);
		assertSpecialization("", "$P := $Q", "");
	}

	/**
	 * Ensures that a predicate variable substitution which contains an ill-formed
	 * predicate is accepted.
	 * 
	 * @author htson
	 */
	@Test
	public void testPut_IllFormedPredicate() {
		BoundIdentifier boundIdent = ff.makeBoundIdentifier(0, null, Z);
		Predicate value = ff.makeRelationalPredicate(Predicate.EQUAL,
				boundIdent, one, null);

		boolean ok = spec.put(
				P,
				value);
		assertTrue(
				"Should accept predicate variable substitution with an ill-formed predicate",
				ok);
		// Cannot check the specialization with the current framework
	}

	/**
	 * Ensures that identifiers can be swapped in a specialization.
	 * 
	 * @author htson
	 */
	@Test 
	public void testPut_PredVarSwap() {
		spec.put(P, Q);
		boolean ok = spec.put(Q, P);
		assertTrue("Should accept substitution that swapping predicate variables", ok);
		assertSpecialization("", "$P := $Q || $Q := $P", "");
	}

	/**
	 * Ensures that adding a predicate variable substitution to a specialization
	 * adds the destination identifiers in the destination type environment.
	 */
	@Test 
	public void testPut_PredVarDstTypenv() {
		final Predicate value = parsePredicate("a = 1",
				mTypeEnvironment("a=ℤ", ff));
		assertTrue(spec.put(P, value));
		assertSpecialization("", "$P := a = 1", "a=ℤ");
	}

	/**
	 * Ensures that one can retrieve a predicate varialbe substitution.
	 * 
	 * @author htson
	 */
	@Test
	public void testGet_PredicateVariableWithSubstitution() {
		spec.put(P, Q);
		assertEquals("Incorrect subsititution for $P", Q, spec.get(P));
		assertSpecialization("", "$P := $Q", "");
	}


	/**
	 * Ensures that one gets null if there is no substitution and that no
	 * substitution is created as a side-effect.
	 * 
	 * @author htson
	 */
	@Test
	public void testGet_PredicateVariableWithoutSubstitution() {
		assertNull("There should be no substitution for $P", spec.get(P));
		assertNull("There should be still no substitution for $P", spec.get(P));
		assertEmptySpecialization();
	}
	
	/**
	 * Ensures that types, free identifiers, and predicate variable are
	 * correctly put into the specialization.
	 * 
	 * @author htson
	 */
	@Test
	public void testGets() {
		spec.put(S, T);
		assertSpecialization("S=ℙ(S)", "S := T", "T=ℙ(T)");
		
		spec.put(aS, bT);
		assertSpecialization("a=S", "S := T || a := b", "b=T");
		
		spec.put(T, S);
		assertSpecialization("a=S; T=ℙ(T)", //
				"S := T || a := b || T := S", //
				"b=T");

		spec.put(P, Q);
		assertSpecialization("a=S; T=ℙ(T)", //
				"S := T || a := b || T := S || $P := $Q", //
				"b=T");

		spec.put(bT, aS);
		assertSpecialization("a=S; b=T", //
				"S := T || a := b || T := S || $P := $Q || b := a", //
				"b=T; a=S");

		spec.put(Q, P);
		assertSpecialization("a=S; b=T", //
				"S := T || a := b || T := S || $P := $Q || b := a || $Q := $P", //
				"b=T; a=S");
	}

	private <T extends Object> void assertSet(T[] actuals, T...expected) {
		final Set<T> exp = new HashSet<T>(Arrays.asList(expected));
		final Set<T> act = new HashSet<T>(Arrays.asList(actuals));
		assertEquals("Incorrect sets", exp, act);
	}

	// Verifies that the specialization is empty
	private void assertEmptySpecialization() {
		assertSet(spec.getTypes());
		assertSet(spec.getFreeIdentifiers());
		assertSet(spec.getPredicateVariables());
	}

	// Verifies that the specialization contains the expected substitutions
	private void assertSpecialization(String srcTypenvImage, String specImage,
			String dstTypenvImage) {
		assertSpecialization(spec, srcTypenvImage, specImage, dstTypenvImage);
	}

	// Verifies that a specialization contains the expected substitutions
	private void assertSpecialization(ISpecialization spe,
			String srcTypenvImage, String specImage, String dstTypenvImage) {
		SpecializationChecker.verify(spe, srcTypenvImage, specImage,
				dstTypenvImage);
	}

}
