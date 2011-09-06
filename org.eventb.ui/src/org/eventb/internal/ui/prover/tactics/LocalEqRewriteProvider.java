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

import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.internal.ui.utils.Messages.tactics_replaceWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;
import org.eventb.ui.prover.TacticProviderUtils;

/**
 * Provider for the LocalEqRewrite tactic.
 * <ul>
 * <li>Provider ID : <code>org.eventb.ui.locEq</code></li>
 * <li>Target : hypotheses and goal</li>
 * <ul>
 * 
 * @author Emmanuel Billaud
 */
public class LocalEqRewriteProvider implements ITacticProvider {

	private static class LocalEqApplication implements IPositionApplication {

		private static final String TACTIC_ID = "org.eventb.ui.locEq";

		private final Predicate hyp;
		private final IPosition position;
		private final Predicate equality;
		private final String hyperlinkLabel;

		public LocalEqApplication(Predicate hyp, IPosition position,
				Predicate equality, String hyperlinkLabel) {
			this.hyp = hyp;
			this.position = position;
			this.equality = equality;
			this.hyperlinkLabel = hyperlinkLabel;
		}

		@Override
		public String getHyperlinkLabel() {
			return hyperlinkLabel;
		}

		@Override
		public Point getHyperlinkBounds(String actualString,
				Predicate parsedPredicate) {
			return TacticProviderUtils.getOperatorPosition(parsedPredicate,
					actualString, position);
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return Tactics.localEqRewrite(hyp, position, equality);
		}

		@Override
		public String getTacticID() {
			return TACTIC_ID;
		}

	}

	/**
	 * The inspector finding all the occurrences of identifiers appearing on one
	 * side of an equality, except the one of that equqlity.
	 */
	public static class LocalEqRewriteAppliInspector extends
			DefaultApplicationInspector {

		private final Set<RelationalPredicate> setEquality;
		private final Predicate hypothesis;

		public LocalEqRewriteAppliInspector(Predicate hyp,
				Set<RelationalPredicate> setEquality, Predicate predicate) {
			super(predicate);
			this.setEquality = setEquality;
			this.hypothesis = hyp;
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<ITacticApplication> accumulator) {
			for (RelationalPredicate pred : setEquality) {
				if (pred.equals(hyp)) {
					continue;
				}
				final Expression left = pred.getLeft();
				final Expression right = pred.getRight();
				final IPosition position = accumulator.getCurrentPosition();
				if (identifier.equals(left)) {
					final String hyperlinkLabel = tactics_replaceWith(
							identifier.toString(), right.toString());
					accumulator.add(new LocalEqApplication(hypothesis,
							position, pred, hyperlinkLabel));
				} else if (identifier.equals(right)) {
					final String hyperlinkLabel = tactics_replaceWith(
							identifier.toString(), left.toString());
					accumulator.add(new LocalEqApplication(hypothesis,
							position, pred, hyperlinkLabel));
				}
			}
		}
	}

	private Set<RelationalPredicate> computeSetEquality(IProverSequent seq) {
		Set<RelationalPredicate> computedSet = new HashSet<RelationalPredicate>();
		for (Predicate hyp : seq.visibleHypIterable()) {
			if (hyp.getTag() != Formula.EQUAL) {
				continue;
			}
			final RelationalPredicate rHyp = (RelationalPredicate) hyp;
			if (rHyp.getLeft().getTag() != FREE_IDENT
					&& rHyp.getRight().getTag() != FREE_IDENT) {
				continue;
			}
			computedSet.add(rHyp);
		}
		return computedSet;
	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final IProverSequent sequent = node.getSequent();
		final Predicate predicate = (hyp == null) ? sequent.goal() : hyp;
		return predicate.inspect(new LocalEqRewriteAppliInspector(hyp,
				computeSetEquality(sequent), predicate));
	}

}
