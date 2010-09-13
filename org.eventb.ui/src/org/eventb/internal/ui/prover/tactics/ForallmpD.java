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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "Forall Modus Ponens" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.allmpD</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 */
public class ForallmpD implements ITacticProvider {

	public static class ForallmpDApplication extends DefaultPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.allmpD";

		public ForallmpDApplication(Predicate hyp) {
			super(hyp, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.allmpD(hyp, inputs);
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

		@Override
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			final Formula<?> subFormula = predicate.getSubFormula(position);
			final Predicate pred = (Predicate) subFormula;
			final QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
			final BinaryPredicate impPred = (BinaryPredicate) qPred
					.getPredicate();

			final Predicate left = impPred.getLeft();
			final Predicate right = impPred.getRight();
			return getOperatorPosition(predStr, left.getSourceLocation()
					.getEnd() + 1, right.getSourceLocation().getStart());
		}
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (Tactics.allmpD_applicable(hyp)) {
			final ITacticApplication appli = new ForallmpDApplication(hyp);
			return singletonList(appli);
		}
		return emptyList();
	}

}
