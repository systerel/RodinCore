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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

/**
 * @author Emmanuel Billaud
 */
public class GoalDisjToImpl implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final List<IPosition> listPosGoal = Tactics
				.disjToImplGetPositions(sequent.goal());
		if (listPosGoal.isEmpty()) {
			return "Tactic unapplicable";
		}
		final IPosition posGoal = listPosGoal.get(0);
		if (!posGoal.isRoot()) {
			return "Tactic unapplicable";
		}
		return Tactics.disjToImpl(null, posGoal).apply(ptNode, pm);
	}

}