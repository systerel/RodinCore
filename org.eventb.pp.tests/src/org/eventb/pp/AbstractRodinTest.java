/*******************************************************************************
 * Copyright (c) 2008, 2010 ETH Zurich and others.
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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
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
		
		final TestSequent sequent;

		final boolean valid;

		final int timeout;

		public TestItem(ITypeEnvironment typeEnvironment,
				Set<String> hypotheses, String goal, boolean valid) {
			this.sequent = new TestSequent(typeEnvironment, hypotheses, goal);
			this.valid = valid;
			this.timeout = -1;
		}

		public TestItem(ITypeEnvironment typeEnvironment,
				Set<String> hypotheses, String goal, boolean valid, int timeout) {
			this.sequent = new TestSequent(typeEnvironment, hypotheses, goal);
			this.valid = valid;
			this.timeout = timeout;
		}

		public TestItem(List<String> typenvList, Set<String> hypotheses,
				String goal, boolean valid) {
			this.sequent = new TestSequent(typenvList, hypotheses, goal, ff);
			this.valid = valid;
			this.timeout = -1;
		}

		public TestItem(List<String> typenvList, Set<String> hypotheses,
				String goal, boolean valid, int timeout) {
			this.sequent = new TestSequent(typenvList, hypotheses, goal, ff);
			this.valid = valid;
			this.timeout = timeout;
		}

		private PPResult prove() {
			final PPProof prover = new PPProof(sequent.hypotheses(), sequent
					.goal(), null);
			prover.translate();
			prover.load();
			prover.prove(timeout);
			final PPResult result = prover.getResult();
			return result;
		}

		public void run() {
			final PPResult result = prove();
			assertEquals(valid, result.getResult() == Result.valid);
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
