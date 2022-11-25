/*******************************************************************************
 * Copyright (c) 2006, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Université de Lorraine - extended for case on unions
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * Provider for the "case distinction" tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.disjE</code></li>
 * <li>Target : hypothesis</li>
 * <ul>
 */
public class DisjunctionElimination implements ITacticProvider {

	public static class DisjunctionEliminationApplication extends
			DefaultPositionApplication {

		private static String TACTIC_ID = "org.eventb.ui.disjE";

		public DisjunctionEliminationApplication(Predicate hyp) {
			super(hyp, IPosition.ROOT);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.disjE(hyp);
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
			Predicate subFormula = (Predicate) predicate.getSubFormula(position);
			Formula<?> first, second;
			if (Lib.isDisj(subFormula)) {
				Predicate[] children = ((AssociativePredicate) subFormula).getChildren();
				first = children[0];
				second = children[1];
			} else {
				// subFormula is x ∈ ...
				var rel = (RelationalPredicate) subFormula;
				first = rel.getLeft();
				second = rel.getRight();
			}
			// Return the operator between the first and second child
			return getOperatorPosition(predStr, first.getSourceLocation()
					.getEnd() + 1, second.getSourceLocation().getStart());
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (Tactics.disjE_applicable(hyp)) {
			final ITacticApplication appli = new DisjunctionEliminationApplication(
					hyp);
			return singletonList(appli);
		}
		return emptyList();
	}

}
