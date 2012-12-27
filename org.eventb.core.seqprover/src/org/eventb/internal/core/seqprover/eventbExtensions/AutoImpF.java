/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * A reasoner that tries to infer new selected hypotheses using the current selected hypotheses and the 
 * implications that are present in the visible hypotheses.
 * 
 * 
 * @author Farhad Mehta
 */
public class AutoImpF extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".autoImpE";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("AUTO_MH")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final List<IHypAction> hypActions = new ArrayList<IHypAction>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			
			// Check if hypothesis is an implication
			if (! Lib.isImp(hyp)) continue;
			// Check if left side of implicatons has a predicate in common with the selected hyp.
			boolean consider = false;
			for (Predicate pred : Lib.breakPossibleConjunct(Lib.impLeft(hyp))){
				if (seq.isSelected(pred)) consider = true; 
			}
			if (! consider) continue;

			Set<Predicate> sourceHyps = new LinkedHashSet<Predicate>();
			
			// Add present hyps to sourceHyps
			for (Predicate pred : Lib.breakPossibleConjunct(Lib.impLeft(hyp))){
				if (seq.containsHypothesis(pred)) sourceHyps.add(pred); 
			}
			
			// new lhs for inferred implication
			Set<Predicate> newLhs = Lib.breakPossibleConjunct(Lib.impLeft(hyp));
			newLhs.removeAll(sourceHyps);
			// add implicative hyp into the source hyps.
			sourceHyps.add(hyp);
			
			
			// Generate the inferred hypotheses
			Set<Predicate> inferredHyps;
			if (newLhs.size() == 0){
				inferredHyps = Lib.breakPossibleConjunct(Lib.impRight(hyp));
			} else {
				final DLib lib = mDLib(seq.getFormulaFactory());
				inferredHyps = Collections.singleton(lib.makeImp(lib.makeConj(newLhs), Lib.impRight(hyp)));
			}
			
			// Check if rewriting made a change
			if (inferredHyps.size() == 0) continue;
			
			// Check if rewriting generated something new
			if (seq.containsHypotheses(inferredHyps)){
				// hide the implicative hyp
				hypActions.add(ProverFactory.makeHideHypAction(Collections.singleton(hyp)));
				// select the inferred hyps
				// hypActions.add(ProverFactory.makeSelectHypAction(inferredHyps));
				continue;
			}

			// make the forward inference action
			hypActions.add(ProverFactory.makeForwardInfHypAction(
					sourceHyps, inferredHyps));

			// Hide the original hypothesis. IMPORTANT: Do it after the
			// forward inference hypothesis action
			hypActions.add(ProverFactory.makeHideHypAction(Collections.singleton(hyp)));

		}

		if (!hypActions.isEmpty()) {
			return ProverFactory.makeProofRule(this, input, "auto ImpE",
					hypActions);
		}
		return ProverFactory.reasonerFailure(this, input,
				"Auto ImpE no more applicable");
	}

}
