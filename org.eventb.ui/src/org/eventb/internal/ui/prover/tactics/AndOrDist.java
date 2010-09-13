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

import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "∧ / ∨ distribution" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.andOrDist</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class AndOrDist extends AbstractHypGoalTacticProvider {

	public static class AndOrDistApplication extends DefaultPositionApplication {

		public AndOrDistApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		private static final String TACTIC_ID = "org.eventb.ui.andOrDist";

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.andOrDistRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class AndOrDistAppliInspector extends
			DefaultApplicationInspector {

		public AndOrDistAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(AssociativePredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			int childTag = predicate.getTag() == Predicate.LAND ? Predicate.LOR
					: Predicate.LAND;
			IPosition childPos = accumulator.getCurrentPosition()
					.getFirstChild();
			for (final Predicate child : predicate.getChildren()) {
				if (child.getTag() == childTag) {
					accumulator.add(new AndOrDistApplication(hyp, childPos));
				}
				childPos = childPos.getNextSibling();
			}
		}
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new AndOrDistAppliInspector(hyp));
	}

}
