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
package org.eventb.core.seqprover.tests;

import static org.eventb.core.ast.LanguageVersion.V2;
import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.junit.Assert.*;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.internal.core.seqprover.proofBuilder.PredicateDecomposer;
import org.junit.Test;

/**
 * Unit tests for class PredicateDecomposer: decomposition in subgoals of
 * predicates.
 * 
 * @author Hugo De Sa Pereira Pinto
 */

public class PredicateDecomposerTests {

	/**
	 * The factory to use for parsing
	 */
	protected final FormulaFactory factory = FormulaFactory.getDefault();

	protected final DLib lib;

	/**
	 * Constructor.
	 * <p>
	 */
	public PredicateDecomposerTests() {
		this.lib = mDLib(factory);
	}

	protected Predicate makePredicate(String image, ITypeEnvironment typenv) {
		final Predicate pred = lib.parsePredicate(image);
		if (pred == null)
			fail("Predicate: \n\t" + image + "\n\tcannot be parsed");
		final ITypeCheckResult typeCheck = pred.typeCheck(typenv);
		if (typeCheck.hasProblem())
			fail("Input predicate: \n\t" + image + "\n\tcannot be type checked");
		typenv.addAll(typeCheck.getInferredEnvironment());
		return pred;
	}

	private ITypeEnvironment makeTypeEnvironment(String... env) {
		assertTrue(env.length % 2 == 0);
		final ITypeEnvironment typenv = factory.makeTypeEnvironment();
		for (int i = 0; i < env.length; i += 2) {
			final String name = env[i];
			final String typeString = env[i + 1];
			final IParseResult res = factory.parseType(typeString, V2);
			assertFalse(res.hasProblem());
			typenv.addName(name, res.getParsedType());
		}
		return typenv;
	}

	protected void predicateTest(ITypeEnvironment typenv, String inputImage,
			String... subgoals) {
		ITypeEnvironment env = typenv.clone();
		final Predicate input = makePredicate(inputImage, env);
		Predicate[] subgoalsPreds = new Predicate[subgoals.length];
		PredicateDecomposer decomposer = new PredicateDecomposer(env);
		Predicate[] result = (Predicate[]) decomposer.decompose(input).toArray(
				new Predicate[0]);
		for (int i = 0; i < subgoals.length; i++) {
			Predicate subGoalsPred = makePredicate(subgoals[i], env);
			subgoalsPreds[i] = subGoalsPred;
		}
		assertArrayEquals(subgoalsPreds, result);
	}

	@Test
	public void simpleTest() {
		final ITypeEnvironment env = makeTypeEnvironment();
		predicateTest(env, "x=0", "x=0");
		predicateTest(env, "∀x,y · x=0∧y=0", "y=0","x=0");
		predicateTest(env, "∀x· x=0∧(∀x· x=0)", "x=0 ");
		predicateTest(env, "∀x· x=0∧(∀x·(∀x·x=0)∧x=0)", "x=0");
	}
	
	/**
	 * Tests coming from model "routing_new" from Jean-Raymond Abrial.
	 */
	@Test
	public void testWithEnv() {
		final ITypeEnvironment env = makeTypeEnvironment(//
				"L", "ℙ(L)", //
				"N", "ℙ(N)", //
				"age", "L ↔ ℤ", //
				"l_net", "ℤ ↔ L",//
				"parity", "ℤ ↔ ℤ");
		predicateTest(env, "∀ n, l · n∈N ∧ l∈L ⇒ l∈dom(age) ∧ age∈L ⇸ ℤ",
				"l∈dom(age)","n∈N", "l∈L",  "age∈L ⇸ ℤ");
		predicateTest(env, "∀l·l∈L ⇒ n_net[N×{l}] ⊆ 0‥age(l)", "l∈L",
				" n_net[N×{l}] ⊆ 0‥age(l)");
		predicateTest(env,"x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ ∧"//
						+ "((parity(x)=0 ⇒ ln=l_net ∖ {n ↦ l})"//
						+ "⇒"//
						+ "x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ)",
						"x∈dom(parity)","parity(x)=0","parity∈ℤ ⇸ ℤ","ln=l_net ∖ {n ↦ l}");
		predicateTest(env,"x∈dom(parity) ∧ parity∈ℤ ⇸ ℤ","x∈dom(parity)","parity∈ℤ ⇸ ℤ");
	}
}
