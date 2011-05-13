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

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.removeNeg;

import java.util.List;

import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;

/**
 * Applies reasoner RemoveNeg to a hypothesis or goal in order to reduce it to
 * Negation Normal Form.
 */
public class NNFRewritesOnceTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		final IProverSequent sequent = ptNode.getSequent();
		final ITactic tactic = getTactic(sequent);
		if (tactic != null) {
			tactic.apply(ptNode, pm);
		}
		return "Tactic unapplicable";
	}

	private ITactic getTactic(IProverSequent sequent) {
		for (Predicate hyp : sequent.visibleHypIterable()) {
			final IPosition posHyp = getPosition(hyp);
			if (posHyp != null) {
				return removeNeg(hyp, posHyp);
			}
		}
		final IPosition posGoal = getPosition(sequent.goal());
		if (posGoal != null) {
			return removeNeg(null, posGoal);
		}
		return null;
	}

	private static IPosition getPosition(Predicate pred) {
		final List<IPosition> listPos = pred.inspect(nnfInspector);
		if (listPos.isEmpty()) {
			return null;
		}
		return listPos.get(0);
	}

	/**
	 * Inspector returning the first position which is not in negation normal
	 * form.
	 */
	private static IFormulaInspector<IPosition> nnfInspector = new DefaultInspector<IPosition>() {
		@Override
		public void inspect(UnaryPredicate predicate,
				IAccumulator<IPosition> accumulator) {
			if (predicate.getTag() != NOT) {
				return;
			}
			final Predicate child = predicate.getChild();
			switch (child.getTag()) {
			case EXISTS:
			case FORALL:
			case LAND:
			case LIMP:
			case LOR:
			case NOT:
				accumulator.add(accumulator.getCurrentPosition());
				accumulator.skipAll();
				break;
			default:
				break;
			}
		}
	};

}