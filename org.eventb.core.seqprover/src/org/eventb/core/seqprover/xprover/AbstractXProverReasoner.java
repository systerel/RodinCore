/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted a super class to make new API
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eventb.core.ast.FormulaFactory;
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
 * Common abstract class for external prover reasoners.
 * <p>
 * This class is not intended to be subclassed directly. Instead, external
 * prover contributors should subclass one of its subclasses.
 * </p>
 * 
 * @author François Terrier
 * @author Laurent Voisin
 * @author Nicolas Beauger
 * @since 2.4
 */
public abstract class AbstractXProverReasoner implements IReasoner {

	public static boolean DEBUG = false;

	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		((XProverInput) rInput).serialize(writer);
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new XProverInput(reader);
	}

	/**
	 * Makes an external prover call from the given parameters.
	 * 
	 * @param input
	 *            the input to this reasoner (can contain additional parameters
	 *            specific to the external prover)
	 * @param hypotheses
	 *            hypotheses of the sequent to prove
	 * @param goal
	 *            goal of the sequent to prove
	 * @param factory
	 *            factory of the sequent to prove
	 * @param origin
	 *            origin of the sequent to prove
	 * @param pm
	 *            proof monitor (might be <code>null</code>)
	 * @return a new call of the external prover
	 */
	abstract AbstractXProverCall makeCall(IReasonerInput input,
			Iterable<Predicate> hypotheses, Predicate goal,
			FormulaFactory factory, Object origin, IProofMonitor pm);

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

		final FormulaFactory factory = sequent.getFormulaFactory();

		final Object origin = sequent.getOrigin();

		final AbstractXProverCall call = makeCall(input, hypotheses, goal,
				factory, origin, pm);
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