/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.derivEqualInterv;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.derivEqualIntervApplicable;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "equality of intervals" tactic.
 *
 * @author Guillaume Verdier
 */
public class DerivEqualInterv extends AbstractHypGoalTacticProvider {

	public static class DerivEqualIntervApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.derivEqualInterv";

		public DerivEqualIntervApplication(Predicate hyp) {
			super(hyp, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return derivEqualInterv(hyp);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(IProofTreeNode node, Predicate hyp,
			String globalInput, Predicate predicate) {
		if (hyp == null || !derivEqualIntervApplicable(predicate)) {
			return emptyList();
		} else {
			return singletonList(new DerivEqualIntervApplication(predicate));
		}
	}

}
