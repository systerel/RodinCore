/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static org.eventb.ui.prover.TacticProviderUtils.adaptPositionsToApplications;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "⇒ with ∨" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.impOr</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class ImpOr extends AbstractHypGoalTacticProvider {

	public static class ImpOrApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.impOr";

		public ImpOrApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.impOrRewrites(hyp, position);
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
		return adaptPositionsToApplications(hyp, predicate, Tactics::impOrGetPositions, ImpOrApplication::new);
	}

}
