/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "rewrite ⊂" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.sir</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class RemoveStrictInclusion extends AbstractHypGoalTacticProvider {

	public static class RemoveStrictInclusionApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.sir";

		public RemoveStrictInclusionApplication(Predicate hyp,
				IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.removeStrictInclusion(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class RemoveStrictInclusionAppliInspector extends
			DefaultApplicationInspector {

		public RemoveStrictInclusionAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == Predicate.SUBSET) {
				accumulator.add(new RemoveStrictInclusionApplication(hyp,
						accumulator.getCurrentPosition()));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new RemoveStrictInclusionAppliInspector(hyp));
	}
	
}
