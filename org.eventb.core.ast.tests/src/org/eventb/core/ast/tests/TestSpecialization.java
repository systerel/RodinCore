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

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.tests.FastFactory.mBoolExpression;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;

import java.util.Collections;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Type;

/**
 * Unit tests for specialization of parametric formulas.
 * 
 * @author Laurent Voisin
 */
public class TestSpecialization extends AbstractTests {

	private static final Type Z = ff.makeIntegerType();
	private static final GivenType S = ff.makeGivenType("S");

	private static final FreeIdentifier a = mFreeIdentifier("a", S);
	private static final FreeIdentifier b = mFreeIdentifier("b", S);
	private static final Expression one = mIntegerLiteral(1);
	private static final Expression expTrue = mBoolExpression(mLiteralPredicate(BTRUE));
	private static final FreeIdentifier untyped = mFreeIdentifier("untyped");

	final ISpecialization spec = ff.makeSpecialization();

	/**
	 * Ensures that a null given type is rejected.
	 */
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
	public void testNullTypeValue() {
		try {
			spec.put(S, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that a type substitution which is overriden is rejected.
	 */
	public void testOverridenType() {
		try {
			spec.put(S, Z);
			spec.put(S, S);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that a inserting a type substitution identical to one already
	 * registered is accepted.
	 */
	public void testOverridenSameType() {
		try {
			spec.put(S, Z);
			spec.put(S, Z);
		} catch (IllegalArgumentException e) {
			fail("Shall not have raised an exception");
		}
	}

	/**
	 * Ensures that a null identifier is rejected.
	 */
	public void testNullIdentifier() {
		try {
			spec.put(S, Z);
			spec.put(null, one);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
	}

	/**
	 * Ensures that an untyped identifier is rejected.
	 */
	public void testUntypedIdentifier() {
		try {
			spec.put(S,Z);
			spec.put(untyped, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that a null expression is rejected.
	 */
	public void testNullExpression() {
		try {
			spec.put(S, Z);
			spec.put(a, null);
			fail("Shall have raised an exception");
		} catch (NullPointerException e) {
			// pass
		}
	}

	/**
	 * Ensures that an untyped expression is rejected.
	 */
	public void testUntypedExpression() {
		try {
			spec.put(S, Z);
			spec.put(a, untyped);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that an identifier substitution for which there is no given type
	 * substitution, is rejected.
	 */
	public void testSubstitutionMissingTypeSubstitution() {
		try {
			spec.put(a, one);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that an identifier substitution for which the replacement
	 * expression is of same type as the given identifier is accepted.
	 */
	public void testIdenticalTypeIdentSubstitution() {
		try {
			spec.put(a, b);
		} catch (IllegalArgumentException e) {
			fail("Shall not have raised an exception");
		}
	}
	
	/**
	 * Ensures that if a identity type substitution has been registered, an
	 * identifier substitution implying a different type substitution is
	 * rejected.
	 */
	public void testIdentSubstitutionWithUnmatchedTypes() {
		try {
			spec.put(S, S);
			spec.put(a, one);
			fail(("Shall have raised an exception"));
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that an expression put for the second time and incompatible with
	 * the first typed expression put, is rejected.
	 */
	public void testOverridenUncompatibleExpression() {
		try {
			spec.put(S, Z);
			spec.put(a, expTrue);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that the type of the expression is checked withing complex
	 * replacement types.
	 */
	public void testComplexOverridenUncompatibleExpression() {
		try {
			spec.put(S, ff.makePowerSetType(Z));
			spec.put(a, ff.makeSetExtension(expTrue, null)); 
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}
	
	/**
	 * Ensures that the identifier substitution that does not respect type substitution
	 * is rejected. 
	 */
	public void testIdentifierIsIncompatibleType() {
		try {
			spec.put(S, ff.makePowerSetType(Z));
			final FreeIdentifier sIdent = ff.makeFreeIdentifier("S", null);
			spec.put(sIdent, expTrue);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

	/**
	 * Ensures that an identifier substitution for which the given type
	 * substitution is present is accepted.
	 */
	public void testOKSimpleIdentSubstitution() {
		try {
			spec.put(S, Z);
			spec.put(a, one);	
			assertEquals(a.getGivenTypes(), Collections.singleton(S));
			assertEquals(one.getType(), Z);
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

}
