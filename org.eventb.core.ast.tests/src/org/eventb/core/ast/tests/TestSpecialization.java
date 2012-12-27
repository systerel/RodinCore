/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.fail;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Type;
import org.junit.Test;

/**
 * Unit tests about specialization creation. These tests ensure that all
 * precondition of specialization methods are indeed implemented.
 * 
 * @author Laurent Voisin
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
	}

	/**
	 * Ensures that a inserting a type substitution identical to one already
	 * registered is accepted.
	 */
	@Test 
	public void testOverridenSameType() {
		spec.put(S, Z);
		spec.put(S, Z);
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
	}

	/**
	 * Ensures that an identifier that denotes a given type is rejected, if not
	 * already registered as a type substitution.
	 */
	@Test 
	public void testGivenTypeIdentifier() {
		try {
			spec.put(S.toExpression(ff), Z.toExpression(ff));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that an identifier that denotes a given type is accepted, if it
	 * has already been registered as a type substitution.
	 */
	@Test 
	public void testGivenTypeIdentifierAlready() {
		spec.put(S, Z);
		spec.put(S.toExpression(ff), Z.toExpression(ff));
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
	}

	/**
	 * Ensures that an identifier substitution which changes type when no type
	 * substitution has been registered is rejected.
	 */
	@Test 
	public void testIncompatibleTypeNoTypeSubstitution() {
		try {
			spec.put(aS, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
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
	}

	/**
	 * Ensures that an identifier substitution for which the replacement
	 * expression is of same type as the given identifier is accepted.
	 */
	@Test 
	public void testSameTypeIdentSubstitution() {
		spec.put(aS, bS);
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
	}

	/**
	 * Ensures that an identifier substitution which contains an ill-formed expression 
	 * is rejected.
	 */
	@Test 
	public void testIllFormedExpression() {
		try {
			spec.put(aS, ff.makeBoundIdentifier(0, null, S));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that types can be swapped in a specialization.
	 */
	@Test 
	public void testTypeSwap() {
		spec.put(S, T);
		spec.put(T, S);
	}

	/**
	 * Ensures that identifiers can be swapped in a specialization.
	 */
	@Test 
	public void testIdentSwap() {
		spec.put(S, T);
		spec.put(aS, bT);
		spec.put(bS, aT);
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
	}

}
