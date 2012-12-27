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

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "∪ / ∩ distribution" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.unionInterDist</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class UnionInterDist extends AbstractHypGoalTacticProvider {

	public static class UnionInterDistApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.unionInterDist";

		public UnionInterDistApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.unionInterDistRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class UnionInterDistAppliInspector extends
			DefaultApplicationInspector {

		public UnionInterDistAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			int childTag = expression.getTag() == Expression.BUNION ? Expression.BINTER
					: Expression.BUNION;
			IPosition childPos = accumulator.getCurrentPosition()
					.getFirstChild();
			for (final Expression child : expression.getChildren()) {
				if (child.getTag() == childTag) {
					accumulator
							.add(new UnionInterDistApplication(hyp, childPos));
				}
				childPos = childPos.getNextSibling();
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new UnionInterDistAppliInspector(hyp));
	}

}
