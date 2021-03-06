/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - comment
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genPreds;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.internal.core.seqprover.proofBuilder.PredicateDecomposer.decompose;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.proofBuilder.PredicateDecomposer;
import org.junit.Test;

/**
 * Unit tests for class PredicateDecomposer: decomposition in subgoals of
 * predicates.
 * 
 * @author Hugo De Sa Pereira Pinto
 */
public class PredicateDecomposerTests {

	@Test
	public void simpleTest() {
		predicateTest("x=ℤ", "x=0", "x=0");
		predicateTest("x=ℤ", "∀x· x=0", "x0=0");
		predicateTest("", "∀x,y · x=0∧y=0", "y=0", "x=0");
		predicateTest("", "∀x· x=0∧(∀x· x=0)", "x=0 ");
		predicateTest("", "∀x· x=0∧(∀x·(∀x·x=0)∧x=0)", "x=0");
	}

	/**
	 * Tests coming from model "routing_new" from Jean-Raymond Abrial.
	 */
	@Test
	public void testWithEnv() {
		final String env = "N=ℙ(N); age=L ↔ ℤ; l_net=ℤ ↔ L; parity=ℤ ↔ ℤ";
		predicateTest(env, "∀ n, l · n∈N ∧ l∈L ⇒ l∈dom(age) ∧ age∈L ⇸ ℤ",
				"l∈dom(age)", "n∈N", "l∈L", "age∈L ⇸ ℤ");
		predicateTest(env, "∀l·l∈L ⇒ n_net[N×{l}] ⊆ 0‥age(l)", "l∈L",
				" n_net[N×{l}] ⊆ 0‥age(l)");
		predicateTest(env, "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ ∧"//
				+ "((parity(x)=0 ⇒ ln=l_net ∖ {n ↦ l})"//
				+ "⇒"//
				+ "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ)", "x∈dom(parity)",
				"parity(x)=0", "parity∈ℤ ⇸ ℤ", "ln=l_net ∖ {n ↦ l}");
		predicateTest(env, "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ", "x∈dom(parity)",
				"parity∈ℤ ⇸ ℤ");
	}
	
	/**
	 * Utility test method for decomposing a predicate into sub-goals.
	 * 
	 * @param typenvImage
	 *            the string image of the type environment for type checking the
	 *            predicates
	 * @param inputImage
	 *            the string image of the input predicate
	 * @param subgoals
	 *            the string images of the expected sub-goals
	 * @see PredicateDecomposer#decompose(Predicate)
	 */
	private void predicateTest(String typenvImage, String inputImage,
			String... subgoals) {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(typenvImage);
		final Predicate input = genPred(typenv, inputImage);
		final ISealedTypeEnvironment stypenv = typenv.makeSnapshot();
		final Set<Predicate> expected = genPreds(typenv, subgoals);
		final Set<Predicate> actual = decompose(stypenv, input);
		assertEquals(expected, actual);
	}

}
