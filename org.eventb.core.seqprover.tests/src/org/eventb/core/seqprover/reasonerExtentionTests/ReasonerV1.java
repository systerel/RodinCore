/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class ReasonerV1 extends EmptyInputReasoner implements IVersionedReasoner {

	private static final int VERSION = 1;
	public static String REASONER_ID = "org.eventb.core.seqprover.tests.reasonerV1";

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				"With V1, it's always a success !", new IAntecedent[0]);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public int getVersion() {
		return VERSION;
	}

}
