/*******************************************************************************
 * Copyright (c) 2008, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation of RodinTests
 *     Systerel - created this class from RodinTests + some refactoring
 *     Systerel - mathematical language V2
 *     Systerel - used simple sequents
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.pp.TestSequent.makeSequent;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
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

		public TestItem(ISimpleSequent sSequent, boolean valid, int timeout) {
			this.sequent = new TestSequent(sSequent);
			this.valid = valid;
			this.timeout = timeout;
		}
		
		public TestItem(ISimpleSequent sSequent, boolean valid) {
			this(sSequent, valid, -1);
		}

		private PPResult prove() {
			final PPProof prover = new PPProof(sequent.getSimpleSequent(), null);
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
		final ISimpleSequent sequent = makeSequent(typenv, hypotheses, goal, ff);
		new TestItem(sequent, result).run();
	}

	protected static void doTest(List<String> typenv, Set<String> hypotheses,
			String goal, boolean result, int timeout) {
		final ISimpleSequent sequent = makeSequent(typenv, hypotheses, goal, ff);
		new TestItem(sequent, result, timeout).run();
	}

	protected static void doTest(ITypeEnvironment typenv,
			Set<String> hypotheses, String goal, boolean result) {
		final ISimpleSequent sequent = makeSequent(typenv, hypotheses, goal, ff);
		new TestItem(sequent, result).run();
	}

	protected static void doTest(ITypeEnvironment typenv,
			Set<String> hypotheses, String goal, boolean result, int timeout) {
		final ISimpleSequent sequent = makeSequent(typenv, hypotheses, goal, ff);
		new TestItem(sequent, result, timeout).run();
	}
}
