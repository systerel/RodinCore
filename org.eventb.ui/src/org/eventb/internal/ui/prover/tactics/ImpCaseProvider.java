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

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;

/**
 * Provider for the ImpCase tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.impCase</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 * 
 * @author Emmanuel Billaud
 */
public class ImpCaseProvider implements ITacticProvider {
	
	public static class ImpCaseProviderApplication extends
	DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.impCase";

		public ImpCaseProviderApplication(Predicate hyp) {
			super(hyp, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.impCase(hyp);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}	

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (Tactics.impCase_applicable(hyp)) {
			final ITacticApplication appli = new ImpCaseProviderApplication(hyp);
			return singletonList(appli);
		}
		return emptyList();
	}

}
