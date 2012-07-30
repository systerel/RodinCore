/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover.tests;

import java.util.Arrays;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverReasoner;

/**
 * A simple test reasoner
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("deprecation")
public class TestReasoner extends XProverReasoner {

	static final String SUCCESS_MSG = "Success";
	
	// Used to ensure that cleanup was called.
	boolean cleanedUp = false;
	
	// Configuration of this reasoner (defines the way a prover call behaves) 
	boolean reportNeededHyps = true;
	boolean reportNeededGoal = true;
	long sleepDelay = 0;
	boolean throwException = false;
	Thread threadToInterrupt = null;
	IProofMonitor pmToCancel = null;

	private class ProverCall extends XProverCall {

		private boolean valid;

		protected ProverCall(Iterable<Predicate> hypotheses, Predicate goal,
				IProofMonitor pm) {
			super(hypotheses, goal, pm);
			cleanedUp = false;
		}

		@Override
		public void cleanup() {
			// Report call
			cleanedUp = true;
		}

		@Override
		public String displayMessage() {
			return SUCCESS_MSG;
		}

		@Override
		public boolean isValid() {
			return valid;
		}

		@Override
		public boolean isGoalNeeded() {
			if (reportNeededGoal) {
				return super.isGoalNeeded();
			}
			return false;
		}

		@Override
		public Set<Predicate> neededHypotheses() {
			if (reportNeededHyps) {
				return ProverLib.collectPreds(Arrays.asList(goal));
			}
			return super.neededHypotheses();
		}

		@Override
		public void run() {
			
			if (throwException) {
				throw new IllegalStateException();
			}
			
			if (threadToInterrupt != null) {
				threadToInterrupt.interrupt();
			}
			
			if (pmToCancel != null) {
				pmToCancel.setCanceled(true);
			}
			
			if (sleepDelay != 0) {
				try {
					Thread.sleep(sleepDelay);
				} catch (InterruptedException e) {
					return;
				}
			}
			
			for (Predicate hyp: hypotheses) {
				if (isCancelled()) {
					return;
				}
				if (hyp.equals(goal)) {
					valid = true;
					return;
				}
			}
		}
		
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		return new ProverCall(hypotheses, goal, pm);
	}

	public String getReasonerID() {
		return "org.eventb.xprover.core.tests.test";
	}

}
