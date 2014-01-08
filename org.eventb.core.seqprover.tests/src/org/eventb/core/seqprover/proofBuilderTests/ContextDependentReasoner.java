/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
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
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;


/**
 * A context dependent reasoner used for testing.
 * 
 * @author beauger
 */
public class ContextDependentReasoner extends EmptyInputReasoner {
	
	public static final String REASONER_ID = "org.eventb.core.seqprover.tests.contextDependentReasoner";
	
	// current validity of the reasoner context
	private static boolean contextValidity = false;

	public static void setContextValidity(boolean contextValidity) {
		ContextDependentReasoner.contextValidity = contextValidity;
	}
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (contextValidity) {
			return ProverFactory.makeProofRule(this, input, seq.goal(),
					"Success with context");
		} else {
			return ProverFactory.reasonerFailure(this, input,
					"Failure with context");
		}
	}

}
