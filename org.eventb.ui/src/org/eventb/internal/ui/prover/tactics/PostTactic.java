/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
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

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "post tactic" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.postTactic</code></li>
 * <li>Target : global (predicate)</li>
 * <ul>
 */
public class PostTactic implements ITacticProvider {

	public static class PostTacticApplication extends
			DefaultPredicateApplication {

		private static final String TACTIC_ID = "org.eventb.ui.postTactic";
		private final IEventBRoot root;

		public PostTacticApplication(IEventBRoot root) {
			this.root = root;
		}
		
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			final IAutoPostTacticManager manager = EventBPlugin.getAutoPostTacticManager();
			final ITactic postTactic = manager.getSelectedPostTactics(root);
			return postTactic;
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && node.isOpen()) {
			final Object origin = node.getProofTree().getOrigin();
			if (origin instanceof IProofAttempt) {
				final IProofAttempt pa = (IProofAttempt) origin;
				final IEventBRoot root = pa.getComponent().getPORoot();
				final ITacticApplication appli = new PostTacticApplication(root);
				return singletonList(appli);
			}
		}
		return emptyList();
	}

}
