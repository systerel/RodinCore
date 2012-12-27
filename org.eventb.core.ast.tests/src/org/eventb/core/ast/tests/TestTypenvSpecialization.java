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

import static org.junit.Assert.assertEquals;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeSpecialization;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.junit.Test;

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
	@Test 
	public void testEmptySpecialization() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S)", ff),//
				"",//
				mTypeEnvironment("S=ℙ(S)", ff));
	}

	/**
	 * Ensures that a given type which is specialized disappears from the type
	 * environment.
	 */
	@Test 
	public void testGivenDisappears() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S)", ff),//
				"S := ℤ",//
				mTypeEnvironment());
	}

	/**
	 * Ensures that a given type which is specialized reappears if it occurs in
	 * the right-hand side of some type substitutions.
	 */
	@Test 
	public void testGivenReappears() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T)", ff),//
				"S := ℤ || T := S",//
				mTypeEnvironment("S=ℙ(S)", ff));
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S)", ff),//
				"S := S",//
				mTypeEnvironment("S=ℙ(S)", ff));
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S)", ff),//
				"S := ℙ(S×T)",//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T)", ff));
	}

	/**
	 * Ensures that a given type which is specialized does not reappear if it
	 * occurs in the right-hand side of some type substitutions which is never
	 * applied.
	 */
	@Test 
	public void testGivenReappearsNot() {
		assertTypeSpecialization(//
				mTypeEnvironment("S=ℙ(S)", ff),//
				"S := ℤ || T := S",//
				mTypeEnvironment());
	}

	/**
	 * Ensures that a given type which is not specialized is retained in the
	 * type environment.
	 */
	@Test 
	public void testGivenUnchanged() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T)", ff),//
				"S := ℤ",//
				mTypeEnvironment("T=ℙ(T)", ff));
	}

	/**
	 * Ensures that an identifier which is not substituted has its type changed,
	 * if needed.
	 */
	@Test 
	public void testIdentNotSubstituted() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); a=S", ff),//
				"S := T",//
				mTypeEnvironment("T=ℙ(T); a=T", ff));
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); b=S×T", ff),//
				"S := T",//
				mTypeEnvironment("T=ℙ(T); b=T×T", ff));
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); U=ℙ(U); c=U", ff),//
				"S := T",//
				mTypeEnvironment("T=ℙ(T); U=ℙ(U); c=U", ff));
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=S×T", ff),//
				"S := T || T := S×T",//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=T×(S×T)", ff));
	}

	/**
	 * Ensures that an identifier can be substituted even when its type does not
	 * change.
	 */
	@Test 
	public void testIdentSubstitutedNoTypeChange() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); a=S", ff),//
				"a := b",//
				mTypeEnvironment("S=ℙ(S); b=S", ff));
	}

	/**
	 * Ensures that an identifier can be substituted.
	 */
	@Test 
	public void testIdentSubstituted() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); a=S×T", ff),//
				"S := T || a := b",//
				mTypeEnvironment("T=ℙ(T); b=T×T", ff));
	}

	/**
	 * Ensures that types can be swapped.
	 */
	@Test 
	public void testSwapTypes() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=S×T", ff),//
				"S := T || T := S",//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=T×S", ff));
	}

	/**
	 * Ensures that identifiers can be swapped.
	 */
	@Test 
	public void testSwapIdents() {
		assertSpecialization(//
				mTypeEnvironment("S=ℙ(S); a=S; b=S", ff),//
				"a := b || b := a",//
				mTypeEnvironment("S=ℙ(S); a=S; b=S", ff));
	}

	/**
	 * Ensures that types and identifiers can be swapped at the same time.
	 */
	@Test 
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
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=S; b=T", ff),//
				spe,//
				mTypeEnvironment("S=ℙ(S); T=ℙ(T); a=S; b=T", ff));
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			String specImage, ITypeEnvironment expected) {
		final ISpecialization spe = mSpecialization(typenv, specImage);
		assertSpecialization(typenv, spe, expected);
	}

	private static void assertTypeSpecialization(ITypeEnvironment typenv,
			String typeSpecImage, ITypeEnvironment expected) {
		final ISpecialization spe = mTypeSpecialization(typenv, typeSpecImage);
		assertSpecialization(typenv, spe, expected);
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			ISpecialization spe, ITypeEnvironment expected) {
		final ITypeEnvironment actual = typenv.specialize(spe);
		assertEquals(expected, actual);
	}

}
