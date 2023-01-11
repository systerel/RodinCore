/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
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
 * @author Nicolas Beauger
 * 
 */
public class DTDistinctCase extends AbstractHypGoalTacticProvider {

	private static final String TACTIC_ID = "org.eventb.ui.dtDistinctCase";

	private static class DCApplication extends DefaultPositionApplication {

		public DCApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String gInput) {
			return Tactics.dtDistinctCase(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(IProofTreeNode node, Predicate hyp,
			String globalInput, Predicate predicate) {
		return adaptPositionsToApplications(hyp, predicate, Tactics::dtDCInducGetPositions, DCApplication::new);
	}

}
