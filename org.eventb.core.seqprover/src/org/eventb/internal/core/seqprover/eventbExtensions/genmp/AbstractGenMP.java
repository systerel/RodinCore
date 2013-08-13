/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
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
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Class used for all the level of the reasoner GeneralizedModusPonens.
 * 
 * @author Emmanuel Billaud
 */
public abstract class AbstractGenMP extends EmptyInputReasoner {

	private static final String BASE_REASONER_ID = SequentProver.PLUGIN_ID
			+ ".genMP";

	public static enum Level {
		L0, L1, L2;

		public static final Level LATEST = Level.latest();

		private static final Level latest() {
			final Level[] values = Level.values();
			return values[values.length - 1];
		}

		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}

		public String reasonerIdSuffix() {
			if (this == L0) {
				return "";
			}
			return toString();
		}

	}

	protected final Level level;
	private final String reasonerId;

	protected AbstractGenMP(Level level) {
		this.level = level;
		this.reasonerId = BASE_REASONER_ID + level.reasonerIdSuffix();
	}

	public Level level() {
		return level;
	}

	@Override
	public final String getReasonerID() {
		return reasonerId;
	}

	@ProverRule({ "GENMP_HYP_HYP", "GENMP_NOT_HYP_HYP", "GENMP_HYP_GOAL",
			"GENMP_NOT_HYP_GOAL", "GENMP_GOAL_HYP", "GENMP_NOT_GOAL_HYP",
			"GENMP_OR_GOAL_HYP", "GENMP_OR_NOT_GOAL_HYP" })
	public final IReasonerOutput apply(IProverSequent seq,
			IReasonerInput input, IProofMonitor pm) {
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
				return reasonerFailure(this, input,
						"failure computing re-writing");
			}
			final IAntecedent ant = makeAntecedent(genMP.goal(), null, null,
					hypActions);
			return makeProofRule(this, input, genMP.goal(), "generalized MP",
					ant);
		} else { // the goal is not re-written and is not used to re-write
					// hypotheses.
			if (!hypActions.isEmpty()) {
				return makeProofRule(this, input, "generalized MP", hypActions);
			}
			return reasonerFailure(this, input,
					"generalized MP no more applicable");
		}
	}

}
