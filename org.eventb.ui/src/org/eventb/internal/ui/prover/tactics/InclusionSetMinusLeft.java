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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "remove ⊆ with ∖in" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.inclusionSetMinusLeft</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class InclusionSetMinusLeft extends InclusionSetMinus {


	private static class InclusionSetMinusLeftApplication extends
			InclusionSetMinusApplication {

		private static final String TACTIC_ID = "org.eventb.ui.inclusionSetMinusLeft";

		public InclusionSetMinusLeftApplication(Predicate hyp,
				IPosition position) {
			super(hyp, position);
		}

		@Override
		protected Expression getChild(RelationalPredicate rel) {
			return rel.getLeft();
		}

		@Override
		public String getHyperlinkLabel() {
			return "Rewrite inclusion with set minus on the left";
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.inclusionSetMinusLeftRewrites(hyp, position);
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
		return adaptPositionsToApplications(hyp, predicate, Tactics::inclusionSetMinusLeftRewritesGetPositions,
				InclusionSetMinusLeftApplication::new);
	}

}
