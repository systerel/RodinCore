/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Discharges a sequent that contains a hypothesis and its contradiction.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ContrHyps extends HypothesisReasoner {
	
	private static final IAntecedent[] NO_ANTECEDENT = new IAntecedent[0];
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".contrHyps";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("CNTR")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) {
		if (!Lib.isNeg(pred)) {
			throw new IllegalArgumentException("Predicate " + pred
					+ " is not a negation");
		}
		if (!sequent.containsHypotheses(Lib.breakPossibleConjunct(Lib.negPred(pred)))) {
			throw new IllegalArgumentException("Predicate " + pred
					+ " is not contradicted by hypotheses");
		}
		return NO_ANTECEDENT;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "ct in hyps (" + pred + ")";
	}

}
