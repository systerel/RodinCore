/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.transformer.ISimpleSequent;

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
 * <li><code>newProverCall</code> returns a new object encapsulating a call of
 * the external prover on a given sequent</li>
 * </ul>
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.4
 * 
 */
public abstract class XProverReasoner2 extends AbstractXProverReasoner {

	@Override
	final AbstractXProverCall makeCall(IReasonerInput input,
			ISimpleSequent sequent, IProofMonitor pm) {
		return newProverCall(input, sequent, pm);
	}

	/**
	 * Returns a new object encapsulating a run of the external prover with the
	 * given input.
	 * 
	 * @param input
	 *            the input to this reasoner (can contain additional parameters
	 *            specific to the external prover)
	 * @param sequent
	 *            the sequent to prove
	 * @param pm
	 *            proof monitor (might be <code>null</code>)
	 * @return a new call of the external prover
	 */
	public abstract XProverCall2 newProverCall(IReasonerInput input,
			ISimpleSequent sequent, IProofMonitor pm);

}
