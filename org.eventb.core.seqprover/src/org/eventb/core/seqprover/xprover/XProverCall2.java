/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.xprover;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.transformer.ISimpleSequent;

/**
 * Common protocol for running an external prover.
 * <p>
 * Each instance of this class represents a call to an external prover.
 * Instances are created by the
 * {@link XProverReasoner2#newProverCall(IReasonerInput, ISimpleSequent, IProofMonitor)}
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
 * @author Nicolas Beauger
 * @since 2.4
 */
public abstract class XProverCall2 extends AbstractXProverCall {

	protected final ISimpleSequent sequent;

	/**
	 * Creates a new instance of this class.
	 * <p>
	 * It is important that clients who extend this class do not perform any
	 * expensive computations in their constructors. These should be done inside
	 * the {@link #run()} method.
	 * </p>
	 * 
	 * @param sequent
	 *            the sequent to discharge
	 * @param pm
	 *            proof monitor (might be <code>null</code>) used for
	 *            cancellation
	 */
	protected XProverCall2(ISimpleSequent sequent, IProofMonitor pm) {
		super(pm);
		this.sequent = sequent;
	}

}
