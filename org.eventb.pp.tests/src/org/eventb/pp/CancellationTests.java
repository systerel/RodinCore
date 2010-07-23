/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.internal.pp.PPInput;
import org.eventb.internal.pp.PPProverCall;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that prover cancellation is correctly handled by PP.
 * 
 * @author Laurent Voisin
 */
public class CancellationTests {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	private static Type PS = ff.makePowerSetType(ff.makeGivenType("S"));

	/**
	 * Simple proof monitor.
	 */
	static class ProofMonitor implements IProofMonitor {

		private boolean canceled;

		@Override
		public boolean isCanceled() {
			return canceled;
		}

		@Override
		public void setCanceled(boolean value) {
			canceled = value;
		}

		@Override
		public void setTask(String name) {
			// Ignore
		}

	}

	private PPProverCall call;

	IProofMonitor monitor = new ProofMonitor();

	private Runnable canceller = new Runnable() {
		@Override
		public void run() {
			monitor.setCanceled(true);
		}
	};

	@Before
	public void setUp() {
		PPInput input = new PPInput(false, 1000, 1000);
		Iterable<Predicate> hyps = Collections.emptySet();
		call = new PPProverCall(input, hyps, makeGoal(), monitor);
	}

	private Predicate makeGoal() {
		return ff.makeRelationalPredicate(SUBSETEQ, //
				ff.makeFreeIdentifier("A", null, PS), //
				ff.makeAssociativeExpression(BUNION, new Expression[] {
						ff.makeFreeIdentifier("A", null, PS),
						ff.makeFreeIdentifier("B", null, PS), }, null), null);
	}

	/**
	 * Ensures that the lemma is proved if PP is not canceled.
	 */
	@Test
	public void notCanceled() throws Exception {
		call.run();
		assertTrue(call.isValid());
	}

	/**
	 * Ensures that the lemma is not proved if PP is canceled during the
	 * translation phase.
	 */
	@Test
	public void canceledDuringTranslate() throws Exception {
		try {
			PPProof.translateHook = canceller;
			call.run();
			assertFalse(call.isValid());
		} finally {
			PPProof.translateHook = null;
		}
	}

	/**
	 * Ensures that the lemma is not proved if PP is canceled during the
	 * loading phase.
	 */
	@Test
	public void canceledDuringLoad() throws Exception {
		try {
			PPProof.loadHook = canceller;
			call.run();
			assertFalse(call.isValid());
		} finally {
			PPProof.loadHook = null;
		}
	}

}
