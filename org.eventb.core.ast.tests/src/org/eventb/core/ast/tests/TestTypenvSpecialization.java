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

import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * Unit tests for specialization of type environments. For each test, we specify
 * a source type environment, then a specialization (type substitution and
 * identifier substitution), followed by the expected type environment after
 * specialization.
 * 
 * @author Thomas Muller
 * @author Laurent Voisin
 */
public class TestTypenvSpecialization extends AbstractTests {

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

	/**
	 * Ensures that an empty specialization does not change a type environment.
	 */
	public void testEmptySpecialization() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)"),//
				"", "",//
				mTypeEnvironment("S", "ℙ(S)"));
	}

	/**
	 * Ensures that a given type which is specialized disappears from the type
	 * environment.
	 */
	public void testGivenDisappears() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)"),//
				"S := ℤ", "",//
				mTypeEnvironment());
	}

	/**
	 * Ensures that a given type which is specialized reappears if it occurs in
	 * the right-hand side of some type substitutions.
	 */
	public void testGivenReappears() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)"),//
				"S := ℤ || T := S", "",//
				mTypeEnvironment("S", "ℙ(S)"));
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)"),//
				"S := S", "",//
				mTypeEnvironment("S", "ℙ(S)"));
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)"),//
				"S := ℙ(S×T)", "",//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)"));
	}

	/**
	 * Ensures that a given type which is specialized does not reappear if it
	 * occurs in the right-hand side of some type substitutions which is never
	 * applied.
	 */
	public void testGivenReappearsNot() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)"),//
				"S := ℤ || T := S", "",//
				mTypeEnvironment());
	}

	/**
	 * Ensures that a given type which is not specialized is retained in the
	 * type environment.
	 */
	public void testGivenUnchanged() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)"),//
				"S := ℤ", "",//
				mTypeEnvironment("T", "ℙ(T)"));
	}

	/**
	 * Ensures that an identifier which is not substituted has its type changed,
	 * if needed.
	 */
	public void testIdentNotSubstituted() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "a", "S"),//
				"S := T", "",//
				mTypeEnvironment("T", "ℙ(T)", "a", "T"));
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "b", "S×T"),//
				"S := T", "",//
				mTypeEnvironment("T", "ℙ(T)", "b", "T×T"));
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "U", "ℙ(U)", "c", "U"),//
				"S := T", "",//
				mTypeEnvironment("T", "ℙ(T)", "U", "ℙ(U)", "c", "U"));
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "S×T"),//
				"S := T || T := S×T", "",//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "T×(S×T)"));
	}

	/**
	 * Ensures that an identifier can be substituted even when its type does not
	 * change.
	 */
	public void testIdentSubstitutedNoTypeChange() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "a", "S"),//
				"", "a := b",//
				mTypeEnvironment("S", "ℙ(S)", "b", "S"));
	}

	/**
	 * Ensures that an identifier can be substituted.
	 */
	public void testIdentSubstituted() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "a", "S×T"),//
				"S := T", "a := b",//
				mTypeEnvironment("T", "ℙ(T)", "b", "T×T"));
	}

	/**
	 * Ensures that types can be swapped.
	 */
	public void testSwapTypes() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "S×T"),//
				"S := T || T := S", "",//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "T×S"));
	}

	/**
	 * Ensures that identifiers can be swapped.
	 */
	public void testSwapIdents() {
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "a", "S", "b", "S"),//
				"", "a := b || b := a",//
				mTypeEnvironment("S", "ℙ(S)", "a", "S", "b", "S"));
	}

	/**
	 * Ensures that types and identifiers can be swapped at the same time.
	 */
	public void testSwapBoth() {
		/*
		 * We need to build the specialization by hand, because it is too
		 * difficult to build it correctly from strings.
		 */
		final ISpecialization spe = ff.makeSpecialization();
		final FreeIdentifier aS = mFreeIdentifier("a", S);
		final FreeIdentifier bT = mFreeIdentifier("b", T);
		spe.put(S, T);
		spe.put(T, S);
		spe.put(aS, bT);
		spe.put(bT, aS);
		assertSpecialization(//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "S", "b", "T"),//
				spe,//
				mTypeEnvironment("S", "ℙ(S)", "T", "ℙ(T)", "a", "S", "b", "T"));
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			String typeSpec, String identSpec, ITypeEnvironment expected) {
		final ISpecialization spe = mSpecialization(typenv, typeSpec, identSpec);
		assertSpecialization(typenv, spe, expected);
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			ISpecialization spe, ITypeEnvironment expected) {
		final ITypeEnvironment actual = typenv.specialize(spe);
		assertEquals(expected, actual);
	}

}
