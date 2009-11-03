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
import org.eventb.internal.ui.utils.Messages;
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
				final String hyperlinkLabel = Messages.bind(
						Messages.tactics_replaceWith, replaced, replaceWith);
				applications.add(new TotalDomApplication(hyp, position, subst,
						hyperlinkLabel));
			}
		}
		
	}

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