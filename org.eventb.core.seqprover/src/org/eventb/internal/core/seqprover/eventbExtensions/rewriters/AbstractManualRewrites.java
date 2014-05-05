/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added support for rewriting with needed hypotheses
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeRewriteHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeSelectHypAction;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public abstract class AbstractManualRewrites implements IReasoner {

	private final static String POSITION_KEY = "pos";

	public static class Input implements IReasonerInput, ITranslatableReasonerInput {

		Predicate pred;

		IPosition position;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code> for goal
		 * @param position
		 *            the position where the reasoner can apply
		 */
		public Input(Predicate pred, IPosition position) {
			this.pred = pred;
			this.position = position;
		}

		public void applyHints(ReplayHints renaming) {
			if (pred != null)
				pred = renaming.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}
		
		public IPosition getPosition(){
			return position;
		}

		public Predicate getPred(){
			return pred;
		}

		@Override
		public Input translate(FormulaFactory factory) {
			if (pred == null) {
				return this;
			}
			return new Input(pred.translate(factory), position);
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			if (pred != null) {
				typeEnv.addAll(pred.getFreeIdentifiers());
			}
			return typeEnv;
		}

	}

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;

		final Predicate goal = seq.goal();
		if (hyp == null) {
			// Goal rewriting
			final Set<Predicate> neededHyps = getNeededHyps(seq, goal, position);
	
			if (neededHyps == null) {
				return goalReasonerFailure(input, position, goal);
			}
			
			final Predicate newGoal = rewrite(goal, position);
			if (newGoal == null) {
				return goalReasonerFailure(input, position, goal);
			}
			final Collection<Predicate> newGoals = Lib.breakPossibleConjunct(newGoal);
			final int length = newGoals.size();
			final IAntecedent[] antecedents = new IAntecedent[length];
			int i = 0;
			for (Predicate pred : newGoals) {
				antecedents[i] = ProverFactory.makeAntecedent(pred);
				++i;
			}
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					getDisplayName(hyp, position), antecedents);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			final Set<Predicate> neededHyps = getNeededHyps(seq, hyp, position);
			if (neededHyps == null) {
				return hypReasonerFailure(input, position, hyp);
			}
			final Predicate inferredHyp = rewrite(hyp, position);
			if (inferredHyp == null) {
				return hypReasonerFailure(input, position, hyp);
			}
			
			final List<IHypAction> hypActions = getHypActions(hyp, position,
					inferredHyp, seq.getFormulaFactory());
			return ProverFactory.makeProofRule(this, input, neededHyps,
					getDisplayName(hyp, position), hypActions);
		}
	}



	private IReasonerOutput hypReasonerFailure(Input input, IPosition position,
			Predicate hyp) {
		return ProverFactory.reasonerFailure(this, input, "Rewriter "
				+ getReasonerID() + " is inapplicable for hypothesis "
				+ hyp + " at position " + position);
	}



	private IReasonerOutput goalReasonerFailure(final Input input,
			final IPosition position, final Predicate goal) {
		return ProverFactory.reasonerFailure(this, input, "Rewriter "
				+ getReasonerID() + " is inapplicable for goal " + goal
				+ " at position " + position);
	}

	/**
	 * Returns the list of hypotheses needed to perform rewriting.
	 * 
	 * @param seq
	 *            seq the sequent to which this reasoner is applied
	 * @param goal
	 *            the predicate to be rewriten
	 * @param position
	 *            the position of the predicate in the formula tree
	 * @return the needed hypotheses to apply the rewriting, an empty set if no
	 *         hypotheses are needed, or <code>null</code> if the rewriting can
	 *         not be applied.
	 */
	protected Set<Predicate> getNeededHyps(IProverSequent seq, Predicate pred,
			IPosition position) {
			return Collections.emptySet();
	}

	/**
	 * Apply the rewriting to the given predicate.
	 * 
	 * @param pred
	 *            predicate to rewrite
	 * @return the predicate which is the result of rewriting
	 */
	public abstract Predicate rewrite(Predicate pred, IPosition position);

	/**
	 * Returns the name to display in the generated rule.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten or
	 *            <code>null</code> if it is the goal that gets rewritten
	 * @return the name to display in the rule
	 */
	protected abstract String getDisplayName(Predicate pred, IPosition position);

	/**
	 * Returns the actions to perform on hypotheses.
	 * <p>
	 * Implementations may override this method to perform more hypothesis
	 * actions.
	 * </p>
	 * 
	 * @param hyp
	 *            the hypothesis predicate that gets rewritten
	 * @param inferredHyp
	 *            the inferred hypothesis after rewrite occurred
	 * @return the actions to perform on hypotheses
	 */
	protected List<IHypAction> getHypActions(Predicate hyp, IPosition position,
			Predicate inferredHyp, FormulaFactory ff) {
		final List<IHypAction> actions = new ArrayList<IHypAction>();
		// check if rewriting generated something interesting
		final Collection<Predicate> inferredHyps = breakPossibleConjunct(inferredHyp);
		inferredHyps.remove(DLib.True(ff));
		final List<Predicate> lHyp = singletonList(hyp);
		// if it is the case, make the rewrite action followed by select action
		if (!inferredHyps.isEmpty()) {
			actions.add(makeRewriteHypAction(lHyp, inferredHyps, lHyp));
			actions.add(makeSelectHypAction(inferredHyps));
		} else {
			// nothing inferred, make the sole hide action
			actions.add(makeHideHypAction(lHyp));
		}
		return actions;
	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		final String posString = reader.getString(POSITION_KEY);
		final IPosition position = FormulaFactory.makePosition(posString);
		
		if (reader.getGoal() != null) {
			// Goal rewriting
			return new Input(null, position);
		}
		
		// else hypothesis rewriting
		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one antecedent!"));
		}
		final IAntecedent antecedent = antecedents[0];
		final List<IHypAction> hypActions = antecedent.getHypActions();
		if (hypActions.size() == 0) {
			throw new SerializeException(new IllegalStateException(
					"Expected at least one hyp action!"));
		}
		final IHypAction hypAction = hypActions.get(0);
		if (hypAction instanceof IRewriteHypAction) {
			final IRewriteHypAction rwHypAction = (IRewriteHypAction) hypAction;
			final Collection<Predicate> hyps = rwHypAction.getHyps();
			if (hyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"Expected single required hyp in rewrite hyp action!"));
			}
			return new Input(hyps.iterator().next(), position);
		} else if (hypAction.getActionType().equals(HIDE_ACTION_TYPE)) {
			final ISelectionHypAction selHypAction = (ISelectionHypAction) hypAction;
			final Collection<Predicate> hyps = selHypAction.getHyps();
			if (hyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"Expected single required hyp in hide hyp action!"));
			}
			return new Input(hyps.iterator().next(), position);
		}
		else {
			throw new SerializeException(
					new IllegalStateException(
							"Expected first hyp action to be a rewrite or hide hyp action!"));
		}
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

}
