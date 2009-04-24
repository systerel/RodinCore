/*******************************************************************************
 * Copyright (c) 2008, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation of RodinTests
 *     Systerel - created this class from RodinTests + some refactoring
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.pp;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.pp.PPResult.Result;

/**
 * Common implementation for validation tests of the new Predicate Prover based
 * on lemmas coming form the Rodin platform.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractRodinTest extends AbstractPPTest {

	protected static class TestItem {

		private static final LanguageVersion LANGUAGE_VERSION = LanguageVersion.V2;

		private static Set<Predicate> parseHypotheses(Set<String> hypotheses) {
			final Set<Predicate> result = new LinkedHashSet<Predicate>();
			for (String string : hypotheses) {
				result.add(parsePredicate(string));
			}
			return result;
		}

		private static Predicate parsePredicate(String pred) {
			return ff.parsePredicate(pred, LANGUAGE_VERSION, null)
					.getParsedPredicate();
		}

		private static ITypeEnvironment parseTypeEnvironment(
				List<String> typenvList) {
			final ITypeEnvironment result = ff.makeTypeEnvironment();
			for (int i = 0; i < typenvList.size(); i = i + 2) {
				String name = typenvList.get(i);
				String type = typenvList.get(i + 1);
				result.addName(name, ff.parseType(type, LANGUAGE_VERSION)
						.getParsedType());
			}
			return result;
		}

		final Predicate goal;

		final Set<Predicate> hypotheses;

		final boolean valid;

		final int timeout;

		final ITypeEnvironment typenv;

		public TestItem(ITypeEnvironment typeEnvironment,
				Set<String> hypotheses, String goal, boolean valid) {
			this.typenv = typeEnvironment.clone();
			this.hypotheses = parseHypotheses(hypotheses);
			this.goal = parsePredicate(goal);
			this.valid = valid;
			this.timeout = -1;
		}

		public TestItem(ITypeEnvironment typeEnvironment,
				Set<String> hypotheses, String goal, boolean valid, int timeout) {
			this.typenv = typeEnvironment.clone();
			this.hypotheses = parseHypotheses(hypotheses);
			this.goal = parsePredicate(goal);
			this.valid = valid;
			this.timeout = timeout;
		}

		public TestItem(List<String> typenvList, Set<String> hypotheses,
				String goal, boolean valid) {
			this.typenv = parseTypeEnvironment(typenvList);
			this.hypotheses = parseHypotheses(hypotheses);
			this.goal = parsePredicate(goal);
			this.valid = valid;
			this.timeout = -1;
		}

		public TestItem(List<String> typenvList, Set<String> hypotheses,
				String goal, boolean valid, int timeout) {
			this.typenv = parseTypeEnvironment(typenvList);
			this.hypotheses = parseHypotheses(hypotheses);
			this.goal = parsePredicate(goal);
			this.valid = valid;
			this.timeout = timeout;
		}

		private PPResult prove() {
			final PPProof prover = new PPProof(hypotheses, goal, null);
			prover.translate();
			prover.load();
			prover.prove(timeout);
			final PPResult result = prover.getResult();
			return result;
		}

		public void run() {
			typeCheck();
			final PPResult result = prove();
			assertEquals(valid, result.getResult() == Result.valid);
		}

		private void typeCheck() {
			for (Predicate pred : hypotheses) {
				typeCheck(pred);
			}
			typeCheck(goal);
		}

		private void typeCheck(Predicate predicate) {
			final ITypeCheckResult result = predicate.typeCheck(typenv);
			assertTrue(predicate + " " + result.toString(), result.isSuccess());
			typenv.addAll(result.getInferredEnvironment());
		}

	}

	protected static void doTest(List<String> typenv, Set<String> hypotheses,
			String goal, boolean result) {
		new TestItem(typenv, hypotheses, goal, result).run();
	}

	protected static void doTest(List<String> typenv, Set<String> hypotheses,
			String goal, boolean result, int timeout) {
		new TestItem(typenv, hypotheses, goal, result, timeout).run();
	}

	protected static void doTest(ITypeEnvironment typenv,
			Set<String> hypotheses, String goal, boolean result) {
		new TestItem(typenv, hypotheses, goal, result).run();
	}

	protected static void doTest(ITypeEnvironment typenv,
			Set<String> hypotheses, String goal, boolean result, int timeout) {
		new TestItem(typenv, hypotheses, goal, result, timeout).run();
	}
}
