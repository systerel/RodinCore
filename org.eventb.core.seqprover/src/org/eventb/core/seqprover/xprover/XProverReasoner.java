/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.internal.core.seqprover.Messages;
import org.eventb.internal.core.seqprover.Util;

/**
 * Abstract base class for running an external prover as a reasoner.
 * <p>
 * This class implements the {@link IReasoner} interface and provides a skeletal
 * implementation for running an external prover.
 * </p>
 * <p>
 * To implement a reasoner, clients should extend this class and provide two
 * methods:
 * <ul>
 * <li><code>getReasonerId</code> returns the id of the reasoner,</li>
 * <li><code>newProverCall</code> returns a new object encapsulating a run of
 * the external prover on a given sequent</li>
 * </ul>
 * </p>
 * <p>
 * Optionally, one can use an extended reasoner input (rather than the default
 * one). When this is the case, one has to also override the
 * <code>serializeInput</code> and <code>deserializeInput</code> methods. An
 * example of such an extension is described in the unit tests of this plugin
 * (see class
 * <code>org.eventb.core.seqprover.xprover.tests.ExtendedInputTests</code>).
 * </p>
 * 
 * @author François Terrier
 * @author Laurent Voisin
 * @since 1.0
 */
public abstract class XProverReasoner implements IReasoner {
	
	public static boolean DEBUG = false;
	
	/**
	 * Returns a new object encapsulating a run of the external prover with the
	 * given input.
	 * 
	 * @param input
	 *            the input to this reasoner (can contain additional parameters
	 *            specific to the external prover)
	 * @param hypotheses
	 *            hypotheses of the sequent to prove
	 * @param goal
	 *            goal of the sequent to prove
	 * @param pm
	 *            proof monitor (might be <code>null</code>)
	 * @return a new run of the external prover
	 */
	public abstract XProverCall newProverCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm);

	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		((XProverInput) rInput).serialize(writer);
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new XProverInput(reader);
	}
	
	public final IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final XProverInput input = (XProverInput) reasonerInput;
		if (input.hasError())
			return failure(input, input.getError());
		
		final Iterable<Predicate> hypotheses;
		if (input.restricted) {
			hypotheses = sequent.selectedHypIterable();
		} else {
			hypotheses = sequent.visibleHypIterable();
		}
		final Predicate goal = sequent.goal();
		
		final XProverCall call = newProverCall(input, hypotheses, goal, pm);
		final FutureTask<Object> task = new FutureTask<Object>(call, null);
		call.setTask(task);
		try {
			new Thread(task, "Prover call").start();
			if (input.timeOutDelay > 0) {
				task.get(input.timeOutDelay, TimeUnit.MILLISECONDS);
			} else {
				task.get();
			}
		} catch (InterruptedException e) {
			// Propagate the interruption
			Thread.currentThread().interrupt();
			return failure(input, Messages.xprover_interrupted);
		} catch (ExecutionException e) {
			Util.log(e.getCause(), e.getMessage());
			return failure(input, Messages.xprover_exception);
		} catch (TimeoutException e) {
			return failure(input, Messages.xprover_timeout);
		} finally {
			task.cancel(true);
			call.cleanup();
		}
		
		if (call.isValid()) {
			Set<Predicate> neededHyps = call.neededHypotheses();
			if (neededHyps == null) {
				neededHyps = ProverLib.collectPreds(hypotheses);
			}
			Predicate neededGoal = call.isGoalNeeded() ? goal : null;
			return ProverFactory.makeProofRule(this, input, neededGoal,
					neededHyps, null, call.displayMessage());
			
		} else {
			return failure(reasonerInput, Messages.xprover_failed);
		}
	}
	
	private final IReasonerOutput failure(IReasonerInput input, String message) {
		return ProverFactory.reasonerFailure(this, input, message);
	}

}
