/*******************************************************************************
 * Copyright (c) 2017 Systerel and others.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.junit.Test;

/**
 * Unit tests about specialization cloning. These tests ensure that a clone is
 * indeed disconnected from the original specialization.
 * 
 * @author Laurent Voisin
 */
public abstract class TestSpecializationClone extends AbstractTests {

	private static final GivenType S = ff.makeGivenType("S");
	private static final GivenType T = ff.makeGivenType("T");

	private static final FreeIdentifier aS = mFreeIdentifier("a", S);
	private static final FreeIdentifier bS = mFreeIdentifier("b", S);
	private static final FreeIdentifier bT = mFreeIdentifier("b", T);

	private static final PredicateVariable P = ff.makePredicateVariable("$P",
			null);
	private static final PredicateVariable Q = ff.makePredicateVariable("$Q",
			null);

	private final String srcTypenvImage;
	private final String specImage;
	private final String dstTypenvImage;

	private final ISpecialization spec;
	private final ISpecialization clone;

	/*
	 * Fixture where the original specialization is empty.
	 */
	public static class EmptySpecialization extends TestSpecializationClone {
		public EmptySpecialization() {
			super("", "", "");
		}
	}

	/*
	 * Fixture where the original specialization contains substitutions that do
	 * not interfere with the tests.
	 */
	public static class NonEmptySpecialization extends TestSpecializationClone {
		public NonEmptySpecialization() {
			super("x=U", "U := V || x := y || $Y := $Z", "y=V");
		}
	}

	protected TestSpecializationClone(String srcTypenvImage, String specImage,
			String dstTypenvImage) {
		this.srcTypenvImage = srcTypenvImage;
		this.dstTypenvImage = dstTypenvImage;
		this.specImage = specImage;
		final ITypeEnvironment te = mTypeEnvironment(srcTypenvImage, ff);
		this.spec = mSpecialization(te, specImage);
		this.clone = spec.clone();
	}

	/**
	 * Ensures that adding a type substitution to a clone does not change the
	 * original specialization.
	 */
	@Test
	public void clonePutType() {
		clone.put(S, T);
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("S=ℙ(S)", "S := T", "T=ℙ(T)");
	}

	/**
	 * Ensures that adding an identifier substitution to a clone does not change
	 * the original specialization.
	 */
	@Test
	public void clonePutIdentSameType() {
		clone.put(aS, bS);
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("a=S", "S := S || a := b", "b=S");
	}

	/**
	 * Ensures that adding an identifier substitution to a clone does not change
	 * the original specialization.
	 */
	@Test
	public void clonePutIdentDifferentType() {
		clone.put(S, T);
		clone.put(aS, bT);
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("a=S", "S := T || a := b", "b=T");
	}

	/**
	 * Ensures that adding a predicate substitution to a clone does not change
	 * the original specialization.
	 */
	@Test
	public void clonePutPred() {
		clone.put(P, Q);
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("", "$P := $Q", "");
	}

	/**
	 * Ensures that the side-effects performed while specializing a type only
	 * modify the clone, and not the original specialization.
	 * 
	 * This corresponds to bug 757.
	 */
	@Test
	public void cloneSpecializeType() {
		assertSame(S, S.specialize(clone));
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("S=ℙ(S)", "S := S", "S=ℙ(S)");
	}

	/**
	 * Ensures that the side-effects performed while specializing a type
	 * environment only modify the clone, and not the original specialization.
	 */
	@Test
	public void cloneSpecializeTypenv() {
		final ITypeEnvironment typenv = mTypeEnvironment("a=S", ff);
		assertEquals(typenv, typenv.specialize(clone));
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("a=S", "S := S || a := a", "a=S");
	}

	/**
	 * Ensures that the side-effects performed while specializing a formula only
	 * modify the clone, and not the original specialization.
	 */
	@Test
	public void cloneSpecializeFormula() {
		final ITypeEnvironment typenv = mTypeEnvironment("a=S", ff);
		final Predicate pred = parsePredicate("a=a ∧ $P", typenv);
		assertSame(pred, pred.specialize(clone));
		assertOriginalSpecializationDidNotChange();
		assertCloneSpecialization("a=S", "S := S || a := a || $P := $P", "a=S");
	}

	// Checks that the original specialization did not change.
	private void assertOriginalSpecializationDidNotChange() {
		SpecializationChecker.verify(spec, srcTypenvImage, specImage,
				dstTypenvImage);
	}

	// Checks that the cloned specialization did change.
	private void assertCloneSpecialization(String srcComplement,
			String specComplement, String dstComplement) {
		SpecializationChecker.verify(clone,
				concat(srcTypenvImage, srcComplement, "; "),
				concat(specImage, specComplement, " || "),
				concat(dstTypenvImage, dstComplement, "; "));
	}

	private String concat(String prefix, String suffix, String sep) {
		if (prefix.length() == 0) {
			return suffix;
		}
		if (suffix.length() == 0) {
			return prefix;
		}
		return prefix + sep + suffix;
	}

}
