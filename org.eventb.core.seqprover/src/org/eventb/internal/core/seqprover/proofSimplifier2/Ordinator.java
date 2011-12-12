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
package org.eventb.internal.core.seqprover.proofSimplifier2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author Nicolas Beauger
 *
 */
public class Ordinator {

	/**
	 * Returns a rule with with unused hypothesis actions removed, or the given
	 * rule if no change occurred.
	 * 
	 * @param node
	 *            a proof tree node with a rule
	 * @return a new DependRule
	 */
	private static IProofRule cleanHypActions(IProofTreeNode node) {
		final IProofRule rule = node.getRule();
		final IProverSequent sequent = node.getSequent();
		final IAntecedent[] antecedents = rule.getAntecedents();
		final IAntecedent[] newAntecedents = new IAntecedent[antecedents.length];
			
		boolean changed = false;
		for (int i = 0; i < antecedents.length; i++) {
			final IAntecedent antecedent = antecedents[i];
			final List<IHypAction> hypActions = new ArrayList<IHypAction>(
					antecedent.getHypActions());
			final Iterator<IHypAction> iter = hypActions.iterator();
			boolean actionsChanged = false;
			while (iter.hasNext()) {
				final IHypAction hypAction = iter.next();

				final Collection<Predicate> hyps;
				if (hypAction instanceof ISelectionHypAction) {
					hyps = ((ISelectionHypAction) hypAction).getHyps();
				} else if (hypAction instanceof IForwardInfHypAction) {
					hyps = ((IForwardInfHypAction) hypAction).getHyps();
				} else {
					assert false;
					hyps = null;
				}
				if (!sequent.containsHypotheses(hyps)) {
					iter.remove();
					actionsChanged = true;
				}
			}
			if (actionsChanged) {
				newAntecedents[i] = ProverFactory.makeAntecedent(
						antecedent.getGoal(), antecedent.getAddedHyps(),
						antecedent.getUnselectedAddedHyps(),
						antecedent.getAddedFreeIdents(), hypActions);
				changed = true;
			} else {
				newAntecedents[i] = antecedent;
			}
		}
		if (changed) {
			return ProverFactory.makeProofRule(rule.getReasonerDesc(),
					rule.generatedUsing(), rule.getGoal(),
					rule.getNeededHyps(), rule.getConfidence(),
					rule.getDisplayName(), newAntecedents);
		}
		return rule;
	}


}
