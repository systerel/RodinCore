/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;

import java.util.List;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "mp impl" tactic.
 * Rewrites the implication using contraposition.
 * <ul>
 * <li>Provider ID : <code>	org.eventb.ui.contImpIHyp</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 */
public class ContImpHyp implements ITacticProvider {

	public static class CompImpHypApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.contImpIHyp";

		public CompImpHypApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.contImpHyp(hyp, position); // Second level
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class CompImpHypAppliInspector extends
			DefaultApplicationInspector {

		public CompImpHypAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.LIMP) {
				accumulator.add(new CompImpHypApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node == null)
			return emptyList();
		return hyp.inspect(new CompImpHypAppliInspector(hyp));
	}

}
