/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "ct" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.falsify</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class Contradict extends AbstractHypGoalTacticProvider {

	public static class ContradictApplication extends
			DefaultPredicateApplication {

		private static final String TACTIC_ID = "org.eventb.ui.falsify";
		private final Predicate hyp;

		public ContradictApplication(Predicate hyp) {
			this.hyp = hyp;
		}
		
		public ContradictApplication() {
			this(null);
		}
		
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			if (hyp == null) {
				return Tactics.contradictGoal();
			}
			return Tactics.falsifyHyp(hyp);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		final ITacticApplication appli;
		if (hyp != null && Tactics.falsifyHyp_applicable(hyp, node.getSequent())) {
			appli = new ContradictApplication(hyp);
			return singletonList(appli);
		}
		if (Tactics.contradictGoal_applicable(node)) {
			appli = new ContradictApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
