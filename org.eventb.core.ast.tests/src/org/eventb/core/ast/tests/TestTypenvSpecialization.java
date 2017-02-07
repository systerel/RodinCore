/*******************************************************************************
 * Copyright (c) 2012, 2017 Systerel and others.
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
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mSpecialization;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeSpecialization;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
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
	
	// Destination factory for specializations.
	private static final FormulaFactory DST_FAC = EXTS_FAC;

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
		final ISpecialization spe = DST_FAC.makeSpecialization();
		final FreeIdentifier aS = mFreeIdentifier("a", S);
		final FreeIdentifier bT = mFreeIdentifier("b", T);
		spe.put(S, T.translate(DST_FAC));
		spe.put(T, S.translate(DST_FAC));
		spe.put(aS, bT.translate(DST_FAC));
		spe.put(bT, aS.translate(DST_FAC));
		assertSpecialization(//
				"S=ℙ(S); T=ℙ(T); a=S; b=T",//
				spe,//
				"S=ℙ(S); T=ℙ(T); a=S; b=T");
	}

	/**
	 * Ensures that specializing a type environment prevents adding later a
	 * substitution on a type occurring in the environment.
	 */
	@Test
	public void typenvBlocksType() {
		final GivenType src = ff.makeGivenType("S");
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment();
		typenv.addGivenSet(src.getName());

		final ISpecialization spe = ff.makeSpecialization();
		typenv.specialize(spe);

		try {
			spe.put(src, INT_TYPE);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a type environment prevents adding later a
	 * substitution on an identifier occurring in the environment.
	 */
	@Test
	public void typenvBlocksIdent() {
		final FreeIdentifier src = mFreeIdentifier("a", INT_TYPE);
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment();
		typenv.add(src);

		final ISpecialization spe = ff.makeSpecialization();
		typenv.specialize(spe);

		try {
			spe.put(src, mIntegerLiteral());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := a", "a=ℤ");
	}

	/**
	 * Ensures that specializing a type environment prevents adding later a
	 * substitution on an identifier with the same name as one occurring in the
	 * environment but with a different type.
	 */
	@Test
	public void typenvBlocksIdentDifferentType() {
		final GivenType src = ff.makeGivenType("S");
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment();
		typenv.addGivenSet(src.getName());

		final ISpecialization spe = ff.makeSpecialization();
		typenv.specialize(spe);

		try {
			spe.put(mFreeIdentifier(src.getName(), INT_TYPE),
					mIntegerLiteral());
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that specializing a type environment containing a given type
	 * which has no substitution cannot conflict with an existing substitution.
	 */
	@Test
	public void typeBlocksTypenvSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("S", INT_TYPE), mIntegerLiteral(0));

		try {
			mTypeEnvironment("a=S", ff).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=ℤ", "S := 0", "");
	}

	/**
	 * Ensures that specializing a type environment containing an identifier
	 * which has no substitution cannot conflict with an existing substitution.
	 */
	@Test
	public void identBlocksTypenvSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mIntegerLiteral(0));

		try {
			mTypeEnvironment("a=S", ff).specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := 0", "");
	}

	/**
	 * Ensures that specializing a type environment containing a given type
	 * which gets substituted does not conflict with a substituted identifier.
	 */
	@Test
	public void typeDoesNotBlockTypenvSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(ff.makeGivenType("S"), ff.makeGivenType("T"));
		spe.put(mFreeIdentifier("b", INT_TYPE), mFreeIdentifier("S", INT_TYPE));

		assertEquals(mTypeEnvironment("a=T", ff),
				mTypeEnvironment("a=S", ff).specialize(spe));

		SpecializationChecker.verify(spe, //
				"b=ℤ; a=S", "S := T || b := S || a := a", "S=ℤ; a=T");
	}

	/**
	 * Ensures that specializing a type environment containing an identifier
	 * which gets substituted cannot conflict with an existing substitution.
	 */
	@Test
	public void identDoesNotBlockTypenvSpecialization() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", ff.makeGivenType("S")),
				mFreeIdentifier("b", ff.makeGivenType("S")));
		spe.put(mFreeIdentifier("b", INT_TYPE), mFreeIdentifier("a", INT_TYPE));

		assertEquals(mTypeEnvironment("b=S", ff),
				mTypeEnvironment("a=S", ff).specialize(spe));

		SpecializationChecker.verify(spe, //
				"a=S; b=ℤ", //
				"a := b || b := a || S := S", //
				"b=S; a=ℤ");
	}

	/**
	 * Ensures that specializing a type environment prevents adding later a type
	 * substitution using a given type in its replacement which conflicts with
	 * an identifier of the specialized type environment.
	 */
	@Test
	public void typenvBlocksDstType() {
		final GivenType src = ff.makeGivenType("S");
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("S=T", ff);

		final ISpecialization spe = ff.makeSpecialization();
		typenv.specialize(spe);

		try {
			spe.put(src, src);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "S=T", "S := S || T := T", "S=T");
	}

	/**
	 * Ensures that specializing a type environment prevents adding later an
	 * identifier substitution using an identifier in its replacement which
	 * conflicts with an identifier of the specialized type environment.
	 */
	@Test
	public void typenvBlocksDstIdent() {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment("a=S", ff);

		final ISpecialization spe = ff.makeSpecialization();
		typenv.specialize(spe);

		try {
			spe.put(mFreeIdentifier("b", INT_TYPE),
					mFreeIdentifier("a", INT_TYPE));
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=S", "S := S || a := a", "a=S");
	}

	/**
	 * Ensures that specializing a type environment containing a given type
	 * which has no substitution raises an exception if the name of the given
	 * type is already used with a different type in the right-hand side of some
	 * substitution.
	 */
	@Test
	public void dstTypeBlocksTypenv() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("S", INT_TYPE));

		final ITypeEnvironment src = mTypeEnvironment("b=S", ff);
		try {
			src.specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := S", "S=ℤ");
	}

	/**
	 * Ensures that specializing a type environment containing an identifier
	 * which has no substitution raises an exception if the identifier is
	 * already used with a different type in the right-hand side of some
	 * substitution.
	 */
	@Test
	public void dstIdentBlocksTypenv() {
		final ISpecialization spe = ff.makeSpecialization();
		spe.put(mFreeIdentifier("a", INT_TYPE), mFreeIdentifier("b", INT_TYPE));

		final ITypeEnvironment src = mTypeEnvironment("b=S", ff);
		try {
			src.specialize(spe);
			fail("Shall have raised an exception");
		} catch (IllegalArgumentException e) {
			// pass
		}

		SpecializationChecker.verify(spe, "a=ℤ", "a := b", "b=ℤ");
	}

	private static void assertSpecialization(String srcTypenvImage,
			String specImage, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		final ISpecialization spe = mSpecialization(typenv, specImage, DST_FAC);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertTypeSpecialization(String srcTypenvImage,
			String specImage, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		final ISpecialization spe = mTypeSpecialization(typenv, specImage, DST_FAC);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertSpecialization(String srcTypenvImage,
			ISpecialization spe, String expectedImage) {
		final ITypeEnvironment typenv = mTypeEnvironment(srcTypenvImage, ff);
		assertSpecialization(typenv, spe, expectedImage);
	}

	private static void assertSpecialization(ITypeEnvironment typenv,
			ISpecialization spe, String expectedImage) {
		final ITypeEnvironment expected = mTypeEnvironment(expectedImage, DST_FAC);
		final ITypeEnvironment actual = typenv.specialize(spe);
		assertEquals(expected, actual);
	}

}
