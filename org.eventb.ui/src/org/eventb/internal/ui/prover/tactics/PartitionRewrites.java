/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "Partition rewrites" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.partitionRewrites</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class PartitionRewrites extends AbstractHypGoalTacticProvider {

	public static class PartitionRewritesApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.partitionRewrites";

		public PartitionRewritesApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.partitionRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class PartitionRewritesAppliInspector extends
			DefaultApplicationInspector {

		public PartitionRewritesAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(MultiplePredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.KPARTITION) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new PartitionRewritesApplication(hyp,
						position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new PartitionRewritesAppliInspector(hyp));
	}

}
