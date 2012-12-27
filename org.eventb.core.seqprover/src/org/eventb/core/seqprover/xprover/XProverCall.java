/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - improved documentation about concurrency
 *     Systerel - extracted a super class to make new API
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import java.util.ArrayList;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;

/**
 * Common protocol for running an external prover.
 * <p>
 * Each instance of this class represents a call to an external prover.
 * Instances are created by the
 * {@link XProverReasoner#newProverCall(IReasonerInput, Iterable, Predicate, IProofMonitor)}
 * method.
 * </p>
 * <p>
 * Once the instance created, the <code>run()</code> method will be called once
 * to run the external prover. The <code>cleanup()</code> method will be called
 * eventually.
 * </p>
 * <p>
 * Beware that method <code>run()</code> is called in a separate thread. All
 * other methods are called in the same thread as the constructor. This can lead
 * to subtle concurrency bugs and subclasses should take care to ensure proper
 * synchronization between <code>run()</code> and other methods, if needed.
 * </p>
 * <p>
 * Clients that wish to implement a call to an external prover should extend
 * this class.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @deprecated use {@link XProverCall2} instead
 */
@Deprecated
public abstract class XProverCall extends AbstractXProverCall {
	
	protected final ArrayList<Predicate> hypotheses;
	
	protected final Predicate goal;

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
		super(pm);
		this.hypotheses = new ArrayList<Predicate>();
		for (Predicate hyp : hypotheses) {
			this.hypotheses.add(hyp);
		}
		this.goal = goal;
	}
	
}
