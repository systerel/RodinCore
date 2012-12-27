/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - now implements new ITacticProvider interface
 *******************************************************************************/
package org.eventb.internal.pp.ui;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.pp.PPCore;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public class PPTacticProviderRestricted implements ITacticProvider {

	public static class PPTacticProviderRestrictedApplication extends
			DefaultPredicateApplication {

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return PPCore.newPP(true, 0, -1);
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			final ITacticApplication appli = new PPTacticProviderRestrictedApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
