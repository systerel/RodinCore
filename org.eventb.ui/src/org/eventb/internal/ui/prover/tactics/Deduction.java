/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
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

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "⇒ goal" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.impI</code></li>
 * <li>Target : goal</li>
 * <ul>
 */
public class Deduction implements ITacticProvider {
	
	public static class DeductionApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.impI";

		public DeductionApplication() {
			super(null, IPosition.ROOT);
		}
		
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.impI();
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}
	
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node != null && Tactics.impI_applicable(node.getSequent().goal())) {
			final ITacticApplication appli = new DeductionApplication();
			return singletonList(appli);
		}
		return emptyList();
	}

}
