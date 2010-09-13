/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "dc" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.doCase</code></li>
 * <li>Target : global (predicate)</li>
 * <ul>
 */
public class DoCase implements ITacticProvider {

	public static class DoCaseApplication extends DefaultPredicateApplication {

		private static final String TACTIC_ID = "org.eventb.ui.doCase";

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.doCase(globalInput);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen() && !globalInput.equals("")) {
			final ITacticApplication appli = new DoCaseApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}