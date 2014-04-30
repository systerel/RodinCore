/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.MapOvrGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.MapOvrGoalImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.mapOvrG.Operator;

/**
 * Split goal such as <code>f<+{x↦y}∈A<i>op1</i>B</code> as follows :
 * <ul>
 * <li><code>x∈A</code></li>
 * <li><code>y∈B</code></li>
 * </ul>
 * iff there exists a hypothesis such as <code>f∈A<i>op2</i>B</code> from which
 * we can infer this : <code>f∈A<i>op1</i>B ⇒ f∈A<i>op2</i>B</code>. For more
 * information about those inference, check {@link Operator}.<br>
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
public class MapOvrGoalTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final MapOvrGoalImpl mapOvrGoalImpl = new MapOvrGoalImpl(sequent);
		if (!mapOvrGoalImpl.checkGoal()) {
			return "Goal does not possessed the correct form.";
		}
		final Predicate neededHyp = mapOvrGoalImpl.findNeededHyp(sequent);
		if (neededHyp == null) {
			return "There is no hypothesis which allow to infer the goal.";
		}
		final HypothesisReasoner.Input input = new HypothesisReasoner.Input(
				neededHyp);
		return BasicTactics.reasonerTac(new MapOvrGoal(), input).apply(ptNode,
				pm);
	}

}
