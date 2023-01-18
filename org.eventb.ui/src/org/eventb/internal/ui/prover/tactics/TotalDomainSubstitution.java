/*******************************************************************************
 * Copyright (c) 2009, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - replaced Messages.bind() by tactics_replaceWith()
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;
import static org.eventb.internal.ui.utils.Messages.tactics_replaceWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public class TotalDomainSubstitution implements ITacticProvider {

	private static class TotalDomApplication extends DefaultPositionApplication {

		private static final String HYP_TACTIC_ID = "org.eventb.contributer.seqprover.fr1866809.totalDomHyp";
		private static final String GOAL_TACTIC_ID = "org.eventb.contributer.seqprover.fr1866809.totalDomGoal";

		private final Expression substitute;
		private final String hyperlinkLabel;

		public TotalDomApplication(Predicate hyp, IPosition position,
				Expression substitute, String hyperlinkLabel) {
			super(hyp, position);
			this.substitute = substitute;
			this.hyperlinkLabel = hyperlinkLabel;
		}

		@Override
		public String getHyperlinkLabel() {
			return hyperlinkLabel;
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.totalDomRewrites(hyp, position, substitute);
		}

		@Override
		public String getTacticID() {
			if (hyp == null) {
				return GOAL_TACTIC_ID;
			} else
				return HYP_TACTIC_ID;
		}

	}

	private static class ApplicationComputer {

		private final IProverSequent sequent;
		private final Predicate hyp;
		private final Predicate predicate;
		private final List<ITacticApplication> applications = new ArrayList<ITacticApplication>();

		public ApplicationComputer(IProverSequent sequent,
				Predicate hyp, Predicate predicate) {
			this.sequent = sequent;
			this.hyp = hyp;
			this.predicate = predicate;
		}

		public List<ITacticApplication> getApplications() {
			final List<IPosition> domPositions = predicate.getPositions(new DefaultFilter() {
				@Override
				public boolean select(UnaryExpression expression) {
					return expression.getTag() == Formula.KDOM;
				}
			});
			for (IPosition position : domPositions) {
				final UnaryExpression domExpr = (UnaryExpression) predicate.getSubFormula(position);
				final Expression child = domExpr.getChild();

				final Set<Expression> substitutes = Tactics
						.totalDomGetSubstitutions(sequent, child);
				if (substitutes.isEmpty()) {
					continue;
				}
				
				addApplications(position, domExpr, substitutes);
			}
			return applications;
		}

		private void addApplications(IPosition position,
				Expression domainExpression, Set<Expression> substitutes) {
			final String replaced = domainExpression.toString();

			for (final Expression subst : substitutes) {
				final String replaceWith = subst.toString();
				final String hyperlinkLabel = tactics_replaceWith(replaced,
						replaceWith);
				applications.add(new TotalDomApplication(hyp, position, subst,
						hyperlinkLabel));
			}
		}
		
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final IProverSequent sequent = node.getSequent();

		final boolean isGoal = (hyp == null);
		final Predicate pred = isGoal ? sequent.goal() : hyp;

		final ApplicationComputer appliComputer = new ApplicationComputer(
				sequent, hyp, pred);
		return appliComputer.getApplications();
	}

}