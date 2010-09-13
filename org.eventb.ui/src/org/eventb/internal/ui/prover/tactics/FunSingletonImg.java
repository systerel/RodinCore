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

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "fun. singleton img." tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.funSingletonImg</code></li>
 * <li>Target : any</li>
 * <ul>
 */
public class FunSingletonImg extends AbstractHypGoalTacticProvider {

	public static class FunSingletonImgApplication extends
			DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.funSingletonImg";

		public FunSingletonImgApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.funSingletonImg(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class FunSingletonImgAppliInspector extends
			DefaultApplicationInspector {

		public FunSingletonImgAppliInspector(Predicate hyp) {
			super(hyp);
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (!(expression.getTag() == Formula.RELIMAGE)) {
				return;
			}
			final Expression right = expression.getRight();
			if (!(right.getTag() == Formula.SETEXT)) {
				return;
			}
			final SetExtension setExtension = ((SetExtension) right);
			// singleton
			if (setExtension.getMembers().length == 1) {
				final IPosition position = accumulator.getCurrentPosition();
				accumulator.add(new FunSingletonImgApplication(hyp, position));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		return predicate.inspect(new FunSingletonImgAppliInspector(hyp));
	}

}
