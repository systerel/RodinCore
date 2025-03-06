/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static org.eventb.ui.prover.TacticProviderUtils.adaptPositionsToApplications;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "parallel product fun. image" tactic.
 *
 * @author Guillaume Verdier
 */
public class FunPprodImg extends AbstractHypGoalTacticProvider {

	public static class FunPprodImgApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funPprodImg";

		public FunPprodImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funPprodImg(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			// The reasoner's position points to an expression matching (f || g)(x).
			// We want to put the hyperlink on the || operator.
			var pos = position.getFirstChild().getFirstChild();
			var left = predicate.getSubFormula(pos);
			var right = predicate.getSubFormula(pos.getNextSibling());
			return getOperatorPosition(predStr, left.getSourceLocation().getEnd() + 1,
					right.getSourceLocation().getStart());
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(IProofTreeNode node, Predicate hyp,
			String globalInput, Predicate predicate) {
		return adaptPositionsToApplications(hyp, predicate, Tactics::funPprodImgGetPositions,
				FunPprodImgApplication::new);
	}

}
