/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import static org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerRegistry;

import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.internal.core.seqprover.ReasonerDesc;

/**
 * @author "Nicolas Beauger"
 * 
 */
public abstract class AbstractFakeReasoner extends EmptyInputReasoner implements
		IVersionedReasoner {

	private int version;
	private boolean success;
	private IAntecedent[] antecedents;

	public AbstractFakeReasoner(int version, boolean success,
			IAntecedent... antecedents) {
		this.version = version;
		this.success = success;
		this.antecedents = antecedents;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (success) {
			final int confidence = getConfidence();
			return ProverFactory.makeProofRule(getDesc(), input, seq.goal(),
					null, confidence, "What a success !", antecedents);
		} else {
			return ProverFactory.reasonerFailure(this, input,
					"What a failure !");
		}
	}

	private int getConfidence() {
		return getDesc().isTrusted() ? IConfidence.DISCHARGED_MAX
				: IConfidence.UNCERTAIN_MAX;
	}

	private ReasonerDesc getDesc() {
		return getReasonerRegistry().getReasonerDesc(
				getReasonerID() + ":" + version);
	}

	public int getVersion() {
		return version;
	}

	public void setSuccess(boolean newSuccess) {
		this.success = newSuccess;
	}

}
