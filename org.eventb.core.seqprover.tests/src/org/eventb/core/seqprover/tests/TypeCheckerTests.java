/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.seqprover.TypeChecker;
import org.junit.Test;

/**
 * Unit tests for the type-checker class.
 * 
 * @author Laurent Voisin
 */
public class TypeCheckerTests {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final Type type_S = ff.makeGivenType("S");
	private static final Type type_T = ff.makeGivenType("T");

	private static void assertTypenv(TypeChecker checker,
			ITypeEnvironment expected, boolean shouldHaveChanged) {
		if (shouldHaveChanged) {
			assertTrue(checker.hasNewTypeEnvironment());
			assertEquals(expected, checker.getTypeEnvironment());
		} else {
			assertFalse(checker.hasNewTypeEnvironment());
			assertSame(expected, checker.getTypeEnvironment());
		}
	}

	/**
	 * Ensures that a formula compatible with the type environment is accepted.
	 */
	@Test
	public void simpleOK() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("x=S");
		final TypeChecker checker = new TypeChecker(typenv);
		checker.checkFormula(ff.makeFreeIdentifier("x", null, type_S));
		assertTypenv(checker, typenv, false);
		assertTrue(checker.areAddedIdentsFresh());
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a formula incompatible with the type environment is ejected
	 * (case of a free identifier which is not part of the environment).
	 */
	@Test
	public void unknownIdentInFormula() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("");
		final TypeChecker checker = new TypeChecker(typenv);
		checker.checkFormula(ff.makeFreeIdentifier("x", null, type_T));
		assertTypenv(checker, typenv, false);
		assertTrue(checker.areAddedIdentsFresh());
		assertTrue(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a formula incompatible with the type environment is ejected
	 * (case of a free identifier which has a different type as in the
	 * environment).
	 */
	@Test
	public void badTypeInFormula() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("x=S");
		final TypeChecker checker = new TypeChecker(typenv);
		checker.checkFormula(ff.makeFreeIdentifier("x", null, type_T));
		assertTypenv(checker, typenv, false);
		assertTrue(checker.areAddedIdentsFresh());
		assertTrue(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a fresh identifier can be added to the type environment, and
	 * is used for type compatibility checks afterward.
	 */
	@Test
	public void addFreshIdent() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("x=S");
		final TypeChecker checker = new TypeChecker(typenv);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("y", null, type_T),//
		};
		checker.addIdents(idents);
		final ITypeEnvironment newTypenv = typenv.clone();
		newTypenv.addAll(idents);
		assertTypenv(checker, newTypenv, true);
		assertTrue(checker.areAddedIdentsFresh());
		assertNull(typenv.getType("y"));

		checker.checkFormula(ff.makeFreeIdentifier("y", null, type_T));
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a non fresh identifier is detected, but that no type error
	 * is raised if its type is the same as the existing one.
	 */
	@Test
	public void addNonFreshIdentCompatible() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("x=S");
		final TypeChecker checker = new TypeChecker(typenv);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("x", null, type_S),//
		};
		checker.addIdents(idents);
		assertTypenv(checker, typenv, true);
		assertFalse(checker.areAddedIdentsFresh());
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a non fresh identifier with a different type is detected and
	 * produces a type-check error.
	 */
	@Test
	public void addNonFreshIdentIncompatible() throws Exception {
		final ITypeEnvironment typenv = genTypeEnv("x=S");
		final TypeChecker checker = new TypeChecker(typenv);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("x", null, type_T),//
		};
		checker.addIdents(idents);
		assertTypenv(checker, typenv, true);
		assertFalse(checker.areAddedIdentsFresh());
		assertTrue(checker.hasTypeCheckError());
	}

}
