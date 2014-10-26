/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
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
import static org.eventb.core.ast.tests.FastFactory.mTypeSpecialization;
import static org.junit.Assert.assertEquals;

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
		assertSpecialization("S=ℙ(S)", "", "S=ℙ(S)");
	}

	/**
	 * Ensures that a given type which is specialized disappears from the type
	 * environment.
	 */
	@Test 
	public void testGivenDisappears() {
		assertSpecialization("S=ℙ(S)", "S := ℤ", "");
	}

	/**
	 * Ensures that a given type which is specialized reappears if it occurs in
	 * the right-hand side of some type substitutions.
	 */
	@Test 
	public void testGivenReappears() {
		assertSpecialization("S=ℙ(S); T=ℙ(T)", "S := ℤ || T := S", "S=ℙ(S)");
		assertSpecialization("S=ℙ(S)", "S := S", "S=ℙ(S)");
		assertSpecialization("S=ℙ(S)", "S := ℙ(S×T)", "S=ℙ(S); T=ℙ(T)");
	}

	/**
	 * Ensures that a given type which is specialized does not reappear if it
	 * occurs in the right-hand side of some type substitutions which is never
	 * applied.
	 */
	@Test 
	public void testGivenReappearsNot() {
		assertTypeSpecialization("S=ℙ(S)", "S := ℤ || T := S", "");
	}

	/**
	 * Ensures that a given type which is not specialized is retained in the
	 * type environment.
	 */
	@Test 
	public void testGivenUnchanged() {
		assertSpecialization("S=ℙ(S); T=ℙ(T)", "S := ℤ", "T=ℙ(T)");
	}

	/**
	 * Ensures that an identifier which is not substituted has its type changed,
	 * if needed.
	 */
	@Test 
	public void testIdentNotSubstituted() {
		assertSpecialization("S=ℙ(S); a=S", "S := T", "T=ℙ(T); a=T");
		assertSpecialization("S=ℙ(S); T=ℙ(T); b=S×T", "S := T", "T=ℙ(T); b=T×T");
		assertSpecialization(//
				"S=ℙ(S); U=ℙ(U); c=U",//
				"S := T",//
				"T=ℙ(T); U=ℙ(U); c=U");
		assertSpecialization(//
				"S=ℙ(S); T=ℙ(T); a=S×T",//
				"S := T || T := S×T",//
				"S=ℙ(S); T=ℙ(T); a=T×(S×T)");
	}

	/**
	 * Ensures that an identifier can be substituted even when its type does not
	 * change.
	 */
	@Test 
	public void testIdentSubstitutedNoTypeChange() {
		assertSpecialization("S=ℙ(S); a=S", "a := b", "S=ℙ(S); b=S");
	}

	/**
	 * Ensures that an identifier can be substituted.
	 */
	@Test 
	public void testIdentSubstituted() {
		assertSpecialization(//
				"S=ℙ(S); a=S×T",//
				"S := T || a := b",//
				"T=ℙ(T); b=T×T");
	}

	/**
	 * Ensures that types can be swapped.
	 */
	@Test 
	public void testSwapTypes() {
		assertSpecialization(//
				"S=ℙ(S); T=ℙ(T); a=S×T",//
				"S := T || T := S",//
				"S=ℙ(S); T=ℙ(T); a=T×S");
	}

	/**
	 * Ensures that identifiers can be swapped.
	 */
	@Test 
	public void testSwapIdents() {
		assertSpecialization(//
				"S=ℙ(S); a=S; b=S",//
				"a := b || b := a",//
				"S=ℙ(S); a=S; b=S");
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
				"S=ℙ(S); T=ℙ(T); a=S; b=T",//
				spe,//
				"S=ℙ(S); T=ℙ(T); a=S; b=T");
	}

	private static void assertSpecialization(String srcTypenvImage,
			String specImage, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		final ISpecialization spe = mSpecialization(typenv, specImage);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertTypeSpecialization(String srcTypenvImage,
			String specImage, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		final ISpecialization spe = mTypeSpecialization(typenv, specImage);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertSpecialization(String srcTypenvImage,
			ISpecialization spe, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			ISpecialization spe, String expectedImage) {
		final ITypeEnvironment expected = mTypeEnvironment(expectedImage, ff);
		final ITypeEnvironment actual = typenv.specialize(spe);
		assertEquals(expected, actual);
	}

}
