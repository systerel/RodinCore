/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FalseHyp extends EmptyInputReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".falseHyp";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	@ProverRule("FALSE_HYP")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm){
		final Predicate False = DLib.False(seq.getFormulaFactory());
		if (! seq.containsHypothesis(False))
			return ProverFactory.reasonerFailure(this,input,"no false hypothesis");
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				False,
				"⊥ hyp",
				new IAntecedent[0]);
		
		return reasonerOutput;
	}

}
