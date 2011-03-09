/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.junit.Test;

/**
 * Unit tests for class XProverReasoner.
 * 
 * @author Laurent Voisin
 */
public class XProverReasonerTests extends XProverTests {
	
	/**
	 * Simple proof monitor.
	 */
	static class ProofMonitor implements IProofMonitor {

		private boolean canceled;
		
		public boolean isCanceled() {
			return canceled;
		}

		public void setCanceled(boolean value) {
			canceled = value;
		}

		public void setTask(String name) {
			// Ignore
		}
		
	}

	private static void assertCleanedUp(TestReasoner reasoner) {
		assertTrue("Prover was not cleaned up", reasoner.cleanedUp);
	}

	/**
	 * Ensures that an external prover can succeed.
	 */
	@Test
	public void simpleSuccess() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px), px);
		assertCleanedUp(reasoner);
	}

	/**
	 * Ensures that an external prover can fail.
	 */
	@Test
	public void simpleFailure() {
		final IProverSequent sequent = mSequent(mList(), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}

	/**
	 * Ensures that an external prover run in restricted mode doesn't see non
	 * selected hypotheses.
	 */
	@Test
	public void restrictedFailure() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(true, 0);
		final TestReasoner reasoner = new TestReasoner();
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover run in restricted mode sees
	 * selected hypotheses.
	 */
	@Test
	public void restrictedSuccess() {
		final IProverSequent sequent = mSequent(mList(px), mList(px), px);
		final IReasonerInput input = new XProverInput(true, 0);
		final TestReasoner reasoner = new TestReasoner();
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px), px);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover run in restricted mode sees
	 * only selected hypotheses.
	 */
	@Test
	public void restrictedSuccessAllHyp() {
		final IProverSequent sequent = mSequent(mList(px, py, pz), mList(px, py), px);
		final IReasonerInput input = new XProverInput(true, 0);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.reportNeededHyps = false;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px, py), px);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that the default implementation reports that all hypotheses have
	 * been used.
	 */
	@Test
	public void neededHypsDefault() {
		final IProverSequent sequent = mSequent(mList(px, py), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.reportNeededHyps = false;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px, py), px);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that the overriden implementation can be fine about needed
	 * hypotheses.
	 */
	@Test
	public void neededHypsOverriden() {
		final IProverSequent sequent = mSequent(mList(px, py), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.reportNeededHyps = true;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px), px);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that the default implementation reports that the goal has
	 * been used.
	 */
	@Test
	public void neededGoalDefault() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.reportNeededGoal = true;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px), px);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that the overriden implementation can be fine about needed
	 * hypotheses.
	 */
	@Test
	public void neededGoalOverriden() {
		final IProverSequent sequent = mSequent(mList(px, py), mList(), px);
		final IReasonerInput input = new XProverInput(false, 0);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.reportNeededGoal = false;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertSuccess(output, mList(px), null);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover fails if it reaches a timeout.
	 */
	@Test
	public void timeout() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 100);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.sleepDelay = 1000;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover fails if it throws an exception.
	 */
	@Test
	public void exception() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 100);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.throwException = true;
		final IReasonerOutput output = reasoner.apply(sequent, input, null);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover fails if this thread is interrupted.
	 */
	@Test
	public void interrupt() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 100);
		final TestReasoner reasoner = new TestReasoner();
		reasoner.threadToInterrupt = Thread.currentThread();
		try {
			final IReasonerOutput output = reasoner.apply(sequent, input, null);
			assertFailure(output, null);
		} finally {
			// Reset the interrupted status of this thread
			assertTrue(Thread.interrupted());
		}
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover fails if it is cancelled.
	 */
	@Test
	public void cancelBefore() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 100);
		final TestReasoner reasoner = new TestReasoner();
		final IProofMonitor pm = new ProofMonitor();
		pm.setCanceled(true);
		final IReasonerOutput output = reasoner.apply(sequent, input, pm);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}
	
	/**
	 * Ensures that an external prover fails if it is cancelled.
	 */
	@Test
	public void cancelDuringCall() {
		final IProverSequent sequent = mSequent(mList(px), mList(), px);
		final IReasonerInput input = new XProverInput(false, 100);
		final TestReasoner reasoner = new TestReasoner();
		final IProofMonitor pm = new ProofMonitor();
		reasoner.pmToCancel = pm;
		final IReasonerOutput output = reasoner.apply(sequent, input, pm);
		assertFailure(output, null);
		assertCleanedUp(reasoner);
	}

}
