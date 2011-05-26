/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Refactored class hierarchy
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeDeselectHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeSelectHypAction;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjToImplRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegationRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TrivialRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;

/**
 * Common implementation for rewriting reasoners that rewrite a simple predicate
 * to another simple predicate.
 * 
 * @deprecated
 * 			All SimpleRewrites in this class are deprecated since their functionality is redundant.
 * 			Use {@link AutoRewrites} instead
 * 			
 * 
 * @author Laurent Voisin
 */
public abstract class SimpleRewriter extends HypothesisReasoner {
	
	@Deprecated
	public static class RemoveNegation extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".removeNegation";
		private static final Rewriter REWRITER = new RemoveNegationRewriter();
		public RemoveNegation() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	@Deprecated
	public static class DisjToImpl extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".disjToImpl";
		private static final Rewriter REWRITER = new DisjToImplRewriter();
		public DisjToImpl() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	@Deprecated
	public static class Trivial extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".trivial";
		private static final Rewriter REWRITER = new TrivialRewriter();
		public Trivial() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	@Deprecated
	public static class TypePred extends SimpleRewriter {
		public static final String REASONER_ID =
			SequentProver.PLUGIN_ID + ".typePred";
		private static final Rewriter REWRITER = new TypePredRewriter();
		public TypePred() {
			super(REWRITER);
		}
		public String getReasonerID() {
			return REASONER_ID;
		}
	}

	private final Rewriter rewriter;
	
	public SimpleRewriter(Rewriter rewriter) {
		this.rewriter = rewriter;
	}
	
	@Override
	protected String getDisplay(Predicate pred) {
		if (pred == null) {
			return "rewrite " + rewriter.getName() + " in goal";
		}
		return "rewrite " + rewriter.getName() + " in hyp(" + pred + ")";
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent, Predicate hyp) {
		final FormulaFactory ff = sequent.getFormulaFactory();
		final IAntecedent antecedent;
		if (hyp == null) {
			antecedent = getGoalAntecedent(sequent.goal(), ff);
		} else {
			antecedent = getHypAntecedent(hyp, ff);
		}
		return new IAntecedent[] { antecedent };
	}

	private IAntecedent getGoalAntecedent(final Predicate goal,
			final FormulaFactory ff) {
		final Predicate newGoal = rewrite(goal, ff);
		if (newGoal == null) {
			throw new IllegalArgumentException("Rewriter " + getReasonerID()
					+ " inapplicable for goal " + goal);
		}
		return makeAntecedent(newGoal);
	}

	private IAntecedent getHypAntecedent(Predicate hyp, final FormulaFactory ff) {
		final Predicate newHyp = rewrite(hyp, ff);
		if (newHyp == null) {
			throw new IllegalArgumentException("Rewriter " + getReasonerID()
					+ " inapplicable for hypothesis " + hyp);
		}
		final Set<Predicate> hypSingleton = singleton(hyp);
		final List<Predicate> newHyps = asList(newHyp);
		final List<IHypAction> hypActions = asList(
				makeForwardInfHypAction(hypSingleton, newHyps),
				makeDeselectHypAction(hypSingleton),
				makeSelectHypAction(newHyps));
		return makeAntecedent(null, null, null, hypActions);
	}

	private Predicate rewrite(Predicate pred, FormulaFactory ff) {
		return rewriter.apply(pred, ff);
	}

}
