/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added cancellation tests
 *     Systerel - fixed assertions in isValid() and others
 *     Systerel - changed proof display to NewPP to avoid ambiguity
 *     Systerel - adapted to XProver v2 API
 *******************************************************************************/
package org.eventb.internal.pp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;

/**
 * Implementation of {@link XProverCall2} for PP.
 * 
 * @author François Terrier
 * 
 */
public class PPProverCall extends XProverCall2 implements IPPMonitor {

	private final int maxSteps;
	private PPResult result;

	public PPProverCall(XProverInput input, ISimpleSequent sequent,
			IProofMonitor pm) {
		super(sequent, pm);
		maxSteps = ((PPInput) input).getMaxSteps();
	}

	@Override
	public void cleanup() {
		// Run the garbage collector
		System.gc();
	}

	@Override
	public String displayMessage() {
		return "NewPP";
	}

	@Override
	public boolean isValid() {
		if (result == null) {
			// run() has not finished yet.
			return false;
		}
		return result.getResult() == Result.valid;
	}

	@Override
	public void run() {
		try {
			final PPProof prover = new PPProof(sequent, this);
			checkCancellation();
			prover.translate();
			checkCancellation();
			prover.load();
			checkCancellation();
			prover.prove(maxSteps);
			result = prover.getResult();
		} catch (CancellationException e) {
			result = new PPResult(PPResult.Result.cancel, null);
		}
	}
	
	private void checkCancellation() {
		if (isCancelled()) {
			throw new CancellationException();
		}
	}

	@Override
	public boolean isGoalNeeded() {
		if (!isValid()) {
			throw new IllegalStateException(
					"isGoalNeeded() called on invalid proof.");
		}
		return result.getTracer().isGoalNeeded();
	}

	@Override
	public Set<Predicate> neededHypotheses() {
		if (!isValid()) {
			throw new IllegalStateException(
					"neededHypotheses() called on invalid proof.");
		}
		return new HashSet<Predicate>(result.getTracer().getNeededHypotheses());
	}

	@Override
	public boolean isCanceled() {
		return isCancelled();
	}

}
