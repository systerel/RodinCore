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

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.internal.ui.utils.Messages.tactics_replaceWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Expression;
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
	 * side of an equality, except the one of that equality.
	 */
	public static class LocalEqRewriteAppliInspector extends
			DefaultApplicationInspector {

		private final Map<Expression, Set<Substitution>> substitutionMap;
		private final Predicate hypothesis;

		public LocalEqRewriteAppliInspector(Predicate hyp, Predicate predicate,
				IProverSequent sequent) {
			super(predicate);
			this.substitutionMap = getSubstitutes(sequent);
			this.hypothesis = hyp;
		}

		@Override
		public void inspect(FreeIdentifier identifier,
				IAccumulator<ITacticApplication> accumulator) {
			if (!substitutionMap.containsKey(identifier)) {
				return;
			}
			for (Substitution subs : substitutionMap.get(identifier)) {
				final Predicate equality = subs.getEquality();
				if (equality.equals(hyp)) {
					continue;
				}
				final String hyperlinkLabel = tactics_replaceWith(
						identifier.toString(), subs.getSubstitute().toString());
				accumulator.add(new LocalEqApplication(hypothesis, accumulator
						.getCurrentPosition(), equality, hyperlinkLabel));
			}
		}

		private Map<Expression, Set<Substitution>> getSubstitutes(
				IProverSequent seq) {
			Map<Expression, Set<Substitution>> map = new HashMap<Expression, Set<Substitution>>();
			for (Predicate visHyp : seq.visibleHypIterable()) {
				if (visHyp.getTag() != EQUAL) {
					continue;
				}
				final RelationalPredicate rHyp = (RelationalPredicate) visHyp;
				final Expression left = rHyp.getLeft();
				final Expression right = rHyp.getRight();
				if (right.getTag() == FREE_IDENT) {
					addToMap(right, visHyp, left, map);
				}
				if (left.getTag() == FREE_IDENT) {
					addToMap(left, visHyp, right, map);
				}
			}
			return map;
		}

		private void addToMap(Expression replace, Predicate equality,
				Expression substitute, Map<Expression, Set<Substitution>> map) {
			final Substitution sub = new Substitution(substitute, equality);
			if (map.containsKey(replace)) {
				map.get(replace).add(sub);
				return;
			}
			final Set<Substitution> set = new HashSet<Substitution>();
			set.add(sub);
			map.put(replace, set);
		}
	}

	public static class Substitution {
		private final Expression substitute;
		private final Predicate equality;

		public Substitution(Expression substitute, Predicate equality) {
			this.substitute = substitute;
			this.equality = equality;
		}

		public Expression getSubstitute() {
			return substitute;
		}

		public Predicate getEquality() {
			return equality;
		}

		@Override
		public String toString() {
			return equality.toString();
		}

	}

	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		final IProverSequent sequent = node.getSequent();
		final Predicate predicate = (hyp == null) ? sequent.goal() : hyp;
		return predicate.inspect(new LocalEqRewriteAppliInspector(hyp,
				predicate, sequent));
	}

}
