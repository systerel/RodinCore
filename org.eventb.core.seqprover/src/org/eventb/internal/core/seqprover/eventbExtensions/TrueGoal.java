/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class TrueGoal extends EmptyInputReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".trueGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("TRUE_GOAL")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
	
		if (! (seq.goal().equals(mDLib(seq.getFormulaFactory()).True())))
			return ProverFactory.reasonerFailure(this,input,"Goal is not a tautology");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),"⊤ goal",
				new IAntecedent[0]);
		
		return reasonerOutput;
	}

}
