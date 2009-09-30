/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;

/**
 * Common protocol for running an external prover.
 * <p>
 * Each instance of this class represents a call to an external prover.
 * Instances are created by the
 * {@link XProverReasoner#newProverCall(IReasonerInput, Iterable, Predicate, IProofMonitor)}
 * method.
 * </p>
 * <p>
 * Once the instance created, the <code>run()</code> method will be called
 * once to run the external prover. Then, the <code>cleanup()</code> method
 * will be called eventually.
 * </p>
 * <p>
 * Clients that wish to implement a call to an external prover should extend
 * this class.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public abstract class XProverCall implements Runnable, Cancellable {
	
	protected final ArrayList<Predicate> hypotheses;
	
	protected final Predicate goal;

	protected final IProofMonitor proofMonitor;
	
	// true iff an explicit cancellation has been asked through a call to
	// cancel().
	private FutureTask<Object> task;

	/**
	 * Creates a new instance of this class.
	 * <p>
	 * It is important that clients who extend this class do not perform any
	 * expensive computations in their constructors. These should be done inside
	 * the {@link #run()} method.
	 * </p>
	 * 
	 * @param hypotheses
	 *            hypotheses of the sequent to discharge
	 * @param goal
	 *            goal of the sequent to discharge
	 * @param pm
	 *            proof monitor (might be <code>null</code>) used for
	 *            cancellation
	 */
	protected XProverCall(Iterable<Predicate> hypotheses, Predicate goal,
			IProofMonitor pm) {
		this.hypotheses = new ArrayList<Predicate>();
		for (Predicate hyp : hypotheses) {
			this.hypotheses.add(hyp);
		}
		this.goal = goal;
		this.proofMonitor = pm;
	}
	
	/*package*/ final void setTask(FutureTask<Object> task) {
		this.task = task;
	}

	protected final void setMonitorMessage(String message) {
		if (proofMonitor != null) {
			proofMonitor.setTask(message);
		}
	}
	
	/**
	 * Run the external prover on the sequent given at instance creation.
	 */
	public abstract void run();

	/**
	 * Tells whether the user cancelled this prover call.
	 * 
	 * @return <code>true</code> iff the user cancelled this call
	 */
	public final boolean isCancelled() {
		if (proofMonitor != null && proofMonitor.isCanceled()) {
			if (XProverReasoner.DEBUG) {
				System.out.println("External prover has been cancelled by proof monitor");
			}
			return true;
		}
		if (task != null && task.isCancelled()) {
			if (XProverReasoner.DEBUG) {
				System.out.println("External prover has been cancelled by timeout");
			}
			return true;
		}
		if (XProverReasoner.DEBUG) {
			System.out.println("External prover has not been cancelled yet");
		}
		return false;
	}
	
	/**
	 * Tells whether the sequent passed to the constructor is proved valid by
	 * the external prover.
	 * 
	 * @return <code>true</code> if the sequent to prove is valid
	 */
	public abstract boolean isValid();
	
	/**
	 * Returns the hypotheses that were used to discharge this sequent, or
	 * <code>null</code> if they are unknown (the sequent prover will then
	 * assume that all the hypotheses of the sequent were needed).
	 * <p>
	 * Most provers do not tell which hypotheses were relevant to their proof.
	 * In that case, one should not override this method as the default will
	 * apply.
	 * </p>
	 * <p>
	 * This method is called only when {@link #isValid()} returned
	 * <code>true</code>.
	 * </p>
	 * 
	 * @return a set containing all the hypotheses that are needed to discharge
	 *         the sequent to prove, or <code>null</code> if unknown
	 */
	public Set<Predicate> neededHypotheses() {
		return null;
	}

	/**
	 * Tells whether the goal was relevant to the proof of the sequent. For
	 * instance, if a sequent was discharged because its hypotheses were
	 * contradictory, this method should return <code>false</code>.
	 * <p>
	 * However, most provers do not tell whether the goal was relevant to their
	 * proof. In that case, one should not override this method as the default
	 * will apply.
	 * </p>
	 * <p>
	 * This method is called only when {@link #isValid()} returned
	 * <code>true</code>.
	 * </p>
	 * 
	 * @return <code>false</code> iff the sequent was discharged independently
	 *         of its goal
	 */
	public boolean isGoalNeeded() {
		return true;
	}
	
	/**
	 * Returns the human-readable message that will be displayed for this proof.
	 * <p>
	 * This method is called only when {@link #isValid()} returned
	 * <code>true</code>.
	 * </p>
	 * 
	 * @return the message to display for this proof
	 */
	public abstract String displayMessage();
	
	/**
	 * Cleans up this prover call. This is the place to put any code that does
	 * some clean up after a prover call. This method will always be called
	 * after <code>run()</code>. For instance, a client could put here the
	 * code to delete any intermediate file that was created by this prover
	 * call.
	 */
	public abstract void cleanup();

}
