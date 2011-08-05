/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static org.eventb.core.ast.Formula.KFINITE;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

public class FiniteDef extends AbstractHypGoalTacticProvider {

	public static class FiniteDefApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.finiteDef";

		public FiniteDefApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.finiteDef(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return getOperatorPosition(parsedPredicate, parsedString);
		}

	}

	public static class FiniteDefAppliInspector extends
			DefaultApplicationInspector {

		public FiniteDefAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(SimplePredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (predicate.getTag() == KFINITE) {
				accumulator.add(new FiniteDefApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FiniteDefAppliInspector(hyp));
	}

}
