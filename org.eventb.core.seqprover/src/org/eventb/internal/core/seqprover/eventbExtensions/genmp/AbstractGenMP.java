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
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.genmp.GeneralizedModusPonens.Level;

/**
 * Class used for all the level of the reasoner GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public abstract class AbstractGenMP extends EmptyInputReasoner {

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm, Level level) {
		final GenMPC genMP = new GenMPC(level, pm, seq);

		if (!genMP.runGenMP()) {
			return reasonerFailure(this, input,
					"Generalized MP has been canceled");
		}

		final Predicate rewrittenGoal = genMP.rewrittenGoal();
		final List<IHypAction> hypActions = genMP.hypActions();
		final boolean isGoalDependent = genMP.isGoalDependent();
		if (rewrittenGoal != null) { // The goal is re-written.
			final IAntecedent ant;
			if (!hypActions.isEmpty()) {
				ant = makeAntecedent(rewrittenGoal, null, null, hypActions);
			} else {
				ant = makeAntecedent(rewrittenGoal);
			}
			return makeProofRule(this, input, genMP.goal(), genMP.neededHyps(),
					"generalized MP", ant);
		} else if (isGoalDependent) { // the goal is not re-written but is used 
			// to re-write hypotheses. There should necessarily be IHypActions.
			if (hypActions.isEmpty()) {
				return reasonerFailure(this, input, "failure computing re-writing");
			}
			final IAntecedent ant = makeAntecedent(genMP.goal(), null, null, hypActions);
			return makeProofRule(this, input, genMP.goal(), "generalized MP", ant);
		} else { // the goal is not re-written and is not used to re-write hypotheses.
			if (!hypActions.isEmpty()) {
				return makeProofRule(this, input, "generalized MP", hypActions);
			}
			return reasonerFailure(this, input,
					"generalized MP no more applicable");
		}
	}

}
