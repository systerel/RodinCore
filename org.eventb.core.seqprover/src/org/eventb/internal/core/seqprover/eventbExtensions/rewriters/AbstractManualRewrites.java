/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.ForwardInfHypAction;

public abstract class AbstractManualRewrites implements IReasoner {

	private final static String POSITION_KEY = "pos";

	public static class Input implements IReasonerInput {

		Predicate pred;

		IPosition position;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
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
			
			final Predicate newGoal = rewrite(goal, position, seq.getFormulaFactory());
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
			final Predicate inferredHyp = rewrite(hyp, position, seq.getFormulaFactory());
			if (inferredHyp == null) {
				return hypReasonerFailure(input, position, hyp);
			}
			
			final Collection<Predicate> inferredHyps = Lib
				.breakPossibleConjunct(inferredHyp);
			// Check if rewriting generated something interesting
			inferredHyps.remove(mDLib(seq.getFormulaFactory()).True());
			final List<IHypAction> hypActions;
			// make the forward inference action
			if (!inferredHyps.isEmpty()) {
				final IHypAction forwardInf = ProverFactory
					.makeForwardInfHypAction(Collections.singleton(hyp),
							inferredHyps);
				hypActions = Arrays.asList(forwardInf,
						getHypAction(hyp, position), ProverFactory
								.makeSelectHypAction(inferredHyps));
			}
			else {
				hypActions = Arrays.asList(getHypAction(hyp, position),
						ProverFactory.makeSelectHypAction(inferredHyps));				
			}
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
	 * @param ff TODO
	 * @return the predicate which is the result of rewriting
	 */
	public abstract Predicate rewrite(Predicate pred, IPosition position, FormulaFactory ff);

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
	 * Returns the action to perform on hypotheses.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten
	 * @return the action to perform on hypotheses.
	 */
	protected abstract IHypAction getHypAction(Predicate pred,
			IPosition position);

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
		if (hypAction instanceof ForwardInfHypAction) {
			final ForwardInfHypAction fHypAction = (ForwardInfHypAction) hypAction;
			final Collection<Predicate> hyps = fHypAction.getHyps();
			if (hyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"Expected single required hyp in first forward hyp action!"));
			}
			return new Input(hyps.iterator().next(), position);
		} else {
			throw new SerializeException(new IllegalStateException(
					"Expected first hyp action to be a forward hyp action!"));
		}
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

}
