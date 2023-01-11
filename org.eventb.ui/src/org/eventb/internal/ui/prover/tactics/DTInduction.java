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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.dtInducApplicable;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * @author Nicolas Beauger
 * 
 */
public class DTInduction implements ITacticProvider {

	private static final String TACTIC_ID = "org.eventb.ui.dtInduction";

	private static class DCApplication extends DefaultPositionApplication {

		public DCApplication() {
			// The induction is applied to the whole goal, but we prefer to highlight the
			// first identifier in the forall in the UI
			super(null, IPosition.ROOT.getFirstChild());
		}

		@Override
		public ITactic getTactic(String[] inputs, String gInput) {
			return Tactics.dtInduction();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}
	}

	private static final List<ITacticApplication> NO_APPLICATIONS = emptyList();

	private static final List<ITacticApplication> GOAL_APPLICATION = singletonList(new DCApplication());

	@Override
	public List<ITacticApplication> getPossibleApplications(IProofTreeNode node, Predicate hyp, String globalInput) {
		// Induction is only applicable on goal
		if (hyp == null && dtInducApplicable(node.getSequent().goal())) {
			return GOAL_APPLICATION;
		} else {
			return NO_APPLICATIONS;
		}
	}

}
