/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "rewrites set equality" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.setEql</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class SetEql extends AbstractHypGoalTacticProvider {

	public static class SetEqlApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.setEql";

		public SetEqlApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.setEqlRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class SetEqlAppliInspector extends
			DefaultApplicationInspector {

		public SetEqlAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (!(predicate.getTag() == Predicate.EQUAL)) {
				return;
			}
			final Expression left = predicate.getLeft();
			final Type type = left.getType();
			if (type instanceof PowerSetType) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new SetEqlApplication(hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new SetEqlAppliInspector(hyp));
	}

}
