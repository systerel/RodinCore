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
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "Forall Modus Tollens" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.allmtD</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 */
public class ForallmtD implements ITacticProvider {

	public static class ForallmtDApplication extends ForallFollowedApplication {

		private static final String TACTIC_ID = "org.eventb.ui.allmtD";

		public ForallmtDApplication(Predicate hyp) {
			super(hyp);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.allmtD(hyp, inputs);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (Tactics.allmpD_applicable(hyp)) {
			final ITacticApplication appli = new ForallmtDApplication(hyp);
			return singletonList(appli);
		}
		return emptyList();
	}

}
