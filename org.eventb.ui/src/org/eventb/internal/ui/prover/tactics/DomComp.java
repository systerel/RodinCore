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
 * Provider for the "Dom. with Comp. rewrites" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.domComp</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class DomComp extends AbstractHypGoalTacticProvider {

	public static class DomCompApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.domComp";

		public DomCompApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.domComp(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class DomCompAppliInspector extends
			DefaultApplicationInspector {

		public DomCompAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (expression.getTag() != Expression.FCOMP) {
				return;
			}
			IPosition childPos = accumulator.getCurrentPosition()
					.getFirstChild();
			for (final Expression child : expression.getChildren()) {

				final int childTag = child.getTag();
				if (childTag == Expression.DOMRES
						|| childTag == Expression.DOMSUB) {
					accumulator.add(new DomCompApplication(hyp, childPos));
				}
				childPos = childPos.getNextSibling();
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new DomCompAppliInspector(hyp));
	}

}
