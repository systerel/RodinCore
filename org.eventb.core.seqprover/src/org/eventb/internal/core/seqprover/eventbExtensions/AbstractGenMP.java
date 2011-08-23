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

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.analyzePred;
import static org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.createGoalToHypSet;
import static org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.createHypSet;
import static org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.rewriteGoal;
import static org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.rewriteHyps;

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
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.GenMPC.RewriteHypsOutput;
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonens.Level;

/**
 * Class used for all the level of the reasoner GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public abstract class AbstractGenMP extends EmptyInputReasoner {
	private Map<Predicate, Map<Predicate, List<IPosition>>> modifHypMap;
	private Map<Predicate, List<IPosition>> modifGoalMap;
	private Set<Predicate> hypSet;
	private Set<Predicate> goalToHypSet;

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm, Level level) {
		modifHypMap = new HashMap<Predicate, Map<Predicate, List<IPosition>>>();

		hypSet = createHypSet(seq);
		goalToHypSet = createGoalToHypSet(seq.goal(), level);
		goalToHypSet.addAll(hypSet);
		final Predicate goal = seq.goal();
		Map<Predicate, List<IPosition>> m = analyzePred(goal, hypSet);
		if (m != null) {
			modifGoalMap = m;
		}
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (pm != null && pm.isCanceled()) {
				return reasonerFailure(this, input,
						"Generalized MP has been canceled");
			}
			m = analyzePred(hyp, goalToHypSet);
			if (!m.isEmpty()) {
				modifHypMap.put(hyp, m);
			}
		}

		Set<Predicate> neededHyps = new HashSet<Predicate>();
		final Predicate rewrittenGoal = rewriteGoal(goal, seq, modifGoalMap,
				neededHyps);
		final RewriteHypsOutput output = rewriteHyps(seq, modifHypMap, level);
		final List<IHypAction> hypActions = output.getHypActions();
		final boolean isGoalDependent = output.isGoalDependent();
		if (rewrittenGoal != null) {
			final IAntecedent ant;
			if (!hypActions.isEmpty()) {
				ant = makeAntecedent(rewrittenGoal, null, null, hypActions);
			} else {
				ant = makeAntecedent(rewrittenGoal);
			}
			return makeProofRule(this, input, goal, neededHyps,
					"generalized MP", ant);
		} else if (isGoalDependent) {
			reasonerFailure(this, input, "failure computing re-writing");
			if (hypActions.isEmpty()) {
			}
			final IAntecedent ant = makeAntecedent(goal, null, null, hypActions);
			return makeProofRule(this, input, goal, "generalized MP", ant);
		} else {
			if (!hypActions.isEmpty()) {
				return makeProofRule(this, input, "generalized MP", hypActions);
			}
			return reasonerFailure(this, input,
					"generalized MP no more applicable");
		}
	}

}
