/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.loader;

import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
import org.junit.Test;

public class TestOrderer extends AbstractPPTest {

	private static ITypeEnvironmentBuilder typenv = mTypeEnvironment(
			"x0=A; x1=B; a=S; e=BOOL; f=A↔B; n=ℤ; N=ℙ(ℤ); S=ℙ(S); P=ℙ(B); Q=ℙ(A);" //
			+" R=ℙ(A); U=ℙ(U); M=B×A↔A; SS=ℙ(S); T=ℤ↔ℤ; TT=ℤ×ℤ↔ℤ", ff);

	public static void doTest(String... images) {
		final AbstractContext context = new AbstractContext();
		INormalizedFormula formula = null;
		for (String image : images) {
			final Predicate pred = Util.parsePredicate(image, typenv);
			context.load(pred, false);
			final INormalizedFormula newFormula = context.getLastResult();
			if (formula == null) {
				formula = newFormula;
				continue;
			}
			assertEquals(formula, newFormula);
		}
	}

    @Test
	public void testOrdering() {
		doTest("a ∈ S ∨ d ∈ U", "d ∈ U ∨ a ∈ S");
		doTest("a ∈ S ∨ ¬(a ∈ S)", "¬(a ∈ S) ∨ a ∈ S");
		doTest("a = b ∨ a ∈ S", "a ∈ S ∨ a = b");
		doTest("a = b ∨ n = 1", "n = 1 ∨ a = b");
		doTest("a = b ∨ ¬(a = b)", "¬(a = b) ∨ a = b");
		doTest("a = b ∨ ¬(a ∈ S)", "¬(a ∈ S) ∨ a = b");
		doTest("¬(a = b) ∨ a ∈ S", "a ∈ S ∨ ¬(a = b)");
		doTest("n < 1 ∨ a = b", "a = b ∨ n < 1");
		doTest("¬(a ∈ S ∨ d ∈ U) ∨ a = b", "a = b ∨ ¬(a ∈ S ∨ d ∈ U)");
		doTest("¬(a ∈ S ∨ d ∈ U) ∨ b ∈ S", "b ∈ S ∨ ¬(a ∈ S ∨ d ∈ U)");
		doTest("(∀x·x ∈ S) ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ (∀x·x ∈ S)");
		doTest("a ∈ S ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a ∈ S");
		doTest("a = b ∨ (∀x·x ∈ U)", "(∀x·x ∈ U) ∨ a = b");
	}

}
