/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * 
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

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.TacticProviderUtils;

/**
 * Provider for the "Functional image simplification" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funImgSimp</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunImgSimp extends AbstractHypGoalTacticProvider {

	private static class FunImgSimpApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funImgSimp";

		public FunImgSimpApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

		@Override
		public ITactic getTactic(String[] inputs, String gInput) {
			return Tactics.funImgSimplifies(hyp, position);
		}
		
		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return TacticProviderUtils.getOperatorPosition(parsedPredicate,
					parsedString, position.getFirstChild());
		}

	}

	public static class FunImgSimpApplicationInspector extends
			DefaultApplicationInspector {

		private final IProofTreeNode node;

		public FunImgSimpApplicationInspector(IProofTreeNode node, Predicate hyp) {
			super(hyp);
			this.node = node;
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (Tactics.isFunImgSimpApplicable(expression, node.getSequent())) {
				final IPosition position = accumulator.getCurrentPosition().getParent();
				accumulator.add(new FunImgSimpApplication(hyp, position));
			}
		}
		
	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunImgSimpApplicationInspector(node, hyp));
	}

}
