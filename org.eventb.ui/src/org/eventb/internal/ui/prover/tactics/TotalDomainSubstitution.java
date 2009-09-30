/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider2;
import org.eventb.ui.prover.TacticProviderUtils;

public class TotalDomainSubstitution implements ITacticProvider2 {

	private static class TotalDomApplication implements IPositionApplication {

		private static final String HYP_TACTIC_ID = "org.eventb.contributer.seqprover.fr1866809.totalDomHyp";
		private static final String GOAL_TACTIC_ID = "org.eventb.contributer.seqprover.fr1866809.totalDomGoal";

		private final Predicate hyp;
		private final IPosition position;
		private final Expression substitute;
		private final String hyperlinkLabel;

		public TotalDomApplication(Predicate hyp, IPosition position,
				Expression substitute, String hyperlinkLabel) {
			this.hyp = hyp;
			this.position = position;
			this.substitute = substitute;
			this.hyperlinkLabel = hyperlinkLabel;
		}

		public String getHyperlinkLabel() {
			return hyperlinkLabel;
		}

		public Point getHyperlinkBounds(String actualString,
				Predicate parsedPredicate) {
			return TacticProviderUtils.getOperatorPosition(parsedPredicate,
					actualString, position);
		}

		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.totalDomRewrites(hyp, position, substitute);
		}

		public String getTacticID() {
			if (hyp == null) {
				return GOAL_TACTIC_ID;
			} else
				return HYP_TACTIC_ID;
		}

	}

	private static class TotalDomApplicationVisitor extends DefaultVisitor {

		private final IProverSequent sequent;
		private final Predicate hyp;
		private final Predicate predicate;
		private final List<ITacticApplication> applications = new ArrayList<ITacticApplication>();

		public TotalDomApplicationVisitor(IProverSequent sequent,
				Predicate hyp, Predicate predicate) {
			this.sequent = sequent;
			this.hyp = hyp;
			this.predicate = predicate;
		}

		@Override
		public boolean enterKDOM(final UnaryExpression expr) {
			final Expression child = expr.getChild();

			final Set<Expression> substitutes = Tactics
					.totalDomGetSubstitutions(sequent, child);
			if (substitutes.isEmpty()) {
				return true;
			}

			final IPosition position = predicate.getPosition(expr
					.getSourceLocation());
			final String labelStart = "Replace " + expr.toString() + " with ";

			for (final Expression subst : substitutes) {
				final String hyperlinkLabel = labelStart + subst.toString();
				applications.add(new TotalDomApplication(hyp, position, subst,
						hyperlinkLabel));
			}
			return true;
		}

		public List<ITacticApplication> getApplications() {
			return applications;
		}
	}

	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final IProverSequent sequent = node.getSequent();

		final boolean isGoal = (hyp == null);
		final Predicate pred = isGoal ? sequent.goal() : hyp;

		final TotalDomApplicationVisitor visitor = new TotalDomApplicationVisitor(
				sequent, hyp, pred);
		pred.accept(visitor);
		return visitor.getApplications();
	}

}