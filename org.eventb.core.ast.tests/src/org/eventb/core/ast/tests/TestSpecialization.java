/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.*;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;

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
	private static final Expression one = mIntegerLiteral(1);
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
	 * Ensures that a null identifier is rejected.
	 */
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
	public void testUntypedIdentifier() {
		try {
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
			spec.put(a, untyped);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}
	}

}
