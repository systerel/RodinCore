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
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Simplifies the visible hypotheses and goal in a sequent by replacing
 * sub-predicates <code>P</code> by <code>⊤</code> (or <code>⊥</code>) if
 * <code>P</code> (or <code>¬P</code>) appears as hypothesis (global and local).
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonens extends EmptyInputReasoner {
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".genMP";
	private Map<Predicate, Map<Predicate, List<IPosition>>> modifHypMap;
	private Map<Predicate, List<IPosition>> modifGoalMap;
	private Set<Predicate> hypSet;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		modifHypMap = new HashMap<Predicate, Map<Predicate, List<IPosition>>>();

		hypSet = GenMPC.createHypSet(seq);
		final Predicate goal = seq.goal();
		Map<Predicate, List<IPosition>> m = GenMPC.analyzePred(goal, hypSet);
		if (m != null)
			modifGoalMap = m;
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (pm != null && pm.isCanceled()) {
				return ProverFactory.reasonerFailure(this, input,
						"Generalized MP has been canceled");
			}
			m = GenMPC.analyzePred(hyp, hypSet);
			if (!m.isEmpty())
				modifHypMap.put(hyp, m);
		}

		Set<Predicate> neededHyps = new HashSet<Predicate>();
		final Predicate rewrittenGoal = GenMPC.rewriteGoal(goal, seq,
				modifGoalMap, neededHyps);
		final List<IHypAction> hypActions = GenMPC
				.rewriteHyps(seq, modifHypMap);

		if (rewrittenGoal != null) {
			final IAntecedent ant;
			if (!hypActions.isEmpty()) {
				ant = ProverFactory.makeAntecedent(rewrittenGoal, null, null,
						hypActions);
			} else {
				ant = ProverFactory.makeAntecedent(rewrittenGoal);
			}
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					"generalized MP", ant);
		} else {
			if (!hypActions.isEmpty()) {
				return ProverFactory.makeProofRule(this, input,
						"generalized MP", hypActions);
			}
			return ProverFactory.reasonerFailure(this, input,
					"generalized MP no more applicable");
		}
	}

}