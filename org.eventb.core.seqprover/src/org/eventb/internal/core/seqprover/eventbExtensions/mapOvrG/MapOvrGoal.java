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
package org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Split goal such as <code>f<+{x↦y}∈A<i>op1</i>B</code> as follows :
 * <ul>
 * <li><code>x∈A</code></li>
 * <li><code>y∈B</code></li>
 * </ul>
 * iff there exists a hypothesis such as <code>f∈A<i>op2</i>B</code> from which
 * we can infer this : <code>f∈A<i>op1</i>B ⇒ f∈A<i>op2</i>B</code>. For more
 * information about those inference, check {@link FunAndRel}.<br>
 * With <i>op1</i> a relation among :
 * <ul>
 * <li>RELATION : ↔</li>
 * <li>TOTAL RELATION : </li>
 * <li>PARTIAL FUNCTION : ⇸</li>
 * <li>TOTAL FUNCTION : →</li>
 * </ul>
 * 
 * @author Emmanuel Billaud
 */
public class MapOvrGoal extends HypothesisReasoner {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".mapOvrG";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {
		final MapOvrGoalImpl mapOvrGoalImpl = new MapOvrGoalImpl(sequent);
		if (!mapOvrGoalImpl.checkGoal()) {
			throw new IllegalArgumentException(
					"Goal does not possessed the correct form.");
		}
		if (!mapOvrGoalImpl.infersGoal(pred)) {
			throw new IllegalArgumentException(
					"Given predicate does not infer a part of the goal.");
		}
		final Predicate[] subgoals = mapOvrGoalImpl.createSubgoals();

		final IAntecedent firstAnt = makeAntecedent(subgoals[0]);
		final IAntecedent secondAnt = makeAntecedent(subgoals[1]);

		return new IAntecedent[] { firstAnt, secondAnt };
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "Remove  in goal";
	}

}
