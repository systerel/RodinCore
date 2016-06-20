/*******************************************************************************
 * Copyright (c) 2009, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * @author "Nicolas Beauger"
 * 
 */
public abstract class AbstractFakeReasoner extends EmptyInputReasoner implements IVersionedReasoner {

	private final int version;
	private final boolean success;

	public AbstractFakeReasoner(int version, boolean success) {
		this.version = version;
		this.success = success;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		if (success) {
			return ProverFactory.makeProofRule(this, input, seq.goal(), "What a success !");
		} else {
			return ProverFactory.reasonerFailure(this, input, "What a failure !");
		}
	}

	public int getVersion() {
		return version;
	}

}
