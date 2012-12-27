/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - now extends AbstractHypGoalTacticProvider
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Provider for the "arithmetic simplification" tactic.
 * Simplifies predicates using pre-defined arithmetic simplification rewritings.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.arithRewrites</code></li>
 * <li>Target : any (predicate)</li>
 * <ul>
 * 
 * @author htson
 */
public class Arith extends AbstractHypGoalTacticProvider {

	public static class ArithApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.arithRewrites";

		public ArithApplication(Predicate hyp, IPosition position) {
			super(hyp, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.arithRewrites(hyp, position);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	public static class ArithAppliInspector extends DefaultApplicationInspector {

		private final IFormulaRewriter rewriter;

		public ArithAppliInspector(Predicate hyp, IFormulaRewriter rewriter) {
			super(hyp);
			this.rewriter = rewriter;
		}

		@Override
		public void inspect(AssociativeExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (rewriter.rewrite(expression) != expression) {
				accumulator.add(new ArithApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}

		@Override
		public void inspect(BinaryExpression expression,
				IAccumulator<ITacticApplication> accumulator) {
			if (rewriter.rewrite(expression) != expression) {
				accumulator.add(new ArithApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}

		@Override
		public void inspect(RelationalPredicate predicate,
				IAccumulator<ITacticApplication> accumulator) {
			if (rewriter.rewrite(predicate) != predicate) {
				accumulator.add(new ArithApplication(hyp, accumulator
						.getCurrentPosition()));
			}
		}

	}

	@Override
	protected List<ITacticApplication> getApplicationsOnPredicate(
			IProofTreeNode node, Predicate hyp, String globalInput,
			Predicate predicate) {
		final IFormulaRewriter rewriter = Tactics.getArithRewriter(node
				.getFormulaFactory());
		final ArithAppliInspector finder = new ArithAppliInspector(hyp,
				rewriter);
		return predicate.inspect(finder);
	}
	
}
