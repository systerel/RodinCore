/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
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

	private static final ISealedTypeEnvironment typenv_x_S = mTypeEnvironment("x=S")
			.makeSnapshot();

	private static void assertTypenv(TypeChecker checker,
			ISealedTypeEnvironment expected, boolean shouldHaveChanged) {
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
		final TypeChecker checker = new TypeChecker(typenv_x_S);
		checker.checkFormula(ff.makeFreeIdentifier("x", null, type_S));
		assertTypenv(checker, typenv_x_S, false);
		assertTrue(checker.areAddedIdentsFresh());
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a formula incompatible with the type environment is ejected
	 * (case of a free identifier which is not part of the environment).
	 */
	@Test
	public void unknownIdentInFormula() throws Exception {
		final ISealedTypeEnvironment typenv = mTypeEnvironment().makeSnapshot();
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
		final TypeChecker checker = new TypeChecker(typenv_x_S);
		checker.checkFormula(ff.makeFreeIdentifier("x", null, type_T));
		assertTypenv(checker, typenv_x_S, false);
		assertTrue(checker.areAddedIdentsFresh());
		assertTrue(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a fresh identifier can be added to the type environment, and
	 * is used for type compatibility checks afterward.
	 */
	@Test
	public void addFreshIdent() throws Exception {
		final TypeChecker checker = new TypeChecker(typenv_x_S);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("y", null, type_T),//
		};
		checker.addIdents(idents);
		final ITypeEnvironmentBuilder newTypenv = typenv_x_S.makeBuilder();
		newTypenv.addAll(idents);
		assertTypenv(checker, newTypenv.makeSnapshot(), true);
		assertTrue(checker.areAddedIdentsFresh());
		assertNull(typenv_x_S.getType("y"));

		checker.checkFormula(ff.makeFreeIdentifier("y", null, type_T));
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a non fresh identifier is detected, but that no type error
	 * is raised if its type is the same as the existing one.
	 */
	@Test
	public void addNonFreshIdentCompatible() throws Exception {
		final TypeChecker checker = new TypeChecker(typenv_x_S);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("x", null, type_S),//
		};
		checker.addIdents(idents);
		assertTypenv(checker, typenv_x_S, true);
		assertFalse(checker.areAddedIdentsFresh());
		assertFalse(checker.hasTypeCheckError());
	}

	/**
	 * Ensures that a non fresh identifier with a different type is detected and
	 * produces a type-check error.
	 */
	@Test
	public void addNonFreshIdentIncompatible() throws Exception {
		final TypeChecker checker = new TypeChecker(typenv_x_S);
		final FreeIdentifier[] idents = new FreeIdentifier[] { ff
				.makeFreeIdentifier("x", null, type_T),//
		};
		checker.addIdents(idents);
		assertTypenv(checker, typenv_x_S, true);
		assertFalse(checker.areAddedIdentsFresh());
		assertTrue(checker.hasTypeCheckError());
	}

}
