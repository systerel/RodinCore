/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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

/**
 * Provider for the "ah" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.addHypothesis</code></li>
 * <li>Target : global (predicate)</li>
 * <ul>
 */
public class AddHypothesis extends AbstractHypGoalTacticProvider {

	public static class AddHypothesisApplication extends
			DefaultPredicateApplication {

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.insertLemma(globalInput);
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		// removed "&& node.isOpen()"
		if (!globalInput.isEmpty()) {
			final ITacticApplication appli = new AddHypothesisApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
