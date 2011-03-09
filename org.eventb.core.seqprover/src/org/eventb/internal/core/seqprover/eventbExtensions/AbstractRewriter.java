/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added getters to Input class for testing purposes
 ******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
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
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common implementation for reasoners that rewrite an hypothesis or the goal.
 * <p>
 * This class is intended to be subclassed by clients that contribute a
 * rewriter.
 * </p>
 * 
 * TODO : Modify implementation to generate forward inferences for hyp rewrites.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractRewriter implements IReasoner {

	public static final class Input implements IReasonerInput {

		Predicate pred;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>,
		 * the rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred) {
			this.pred = pred;
		}

		public void applyHints(ReplayHints hints) {
			if (pred != null) pred = hints.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public Predicate getPred() {
			return pred;
		}

	}

	public final IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;

		final Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();
		if (hyp == null) {
			// Goal rewriting
			final Predicate[] newGoals = rewrite(goal, ff);
			if (newGoals == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for goal "
						+ goal);
			}

			final int length = newGoals.length;
			IAntecedent[] antecedents = new IAntecedent[length];
			for (int i = 0; i < length; ++i) {
				antecedents[i] = ProverFactory.makeAntecedent(newGoals[i]);
			}
			return ProverFactory.makeProofRule(this, input, goal,
					getDisplayName(hyp), antecedents);
		} else {
			// Hypothesis rewriting
			if (! seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			
			final Predicate[] rewriteOutput = rewrite(hyp, ff);
			if (rewriteOutput == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " inapplicable for hypothesis "
						+ hyp);
			}

//			final HashSet<Predicate> predSet = new HashSet<Predicate>(Arrays
//					.asList(newHyps));
//			final IAntecedent[] antecedents = new IAntecedent[] { ProverFactory
//					.makeAntecedent(goal, predSet, getHypAction(pred)) };
//			return ProverFactory.makeProofRule(this, input, goal, hyp,
//					getDisplayName(pred), antecedents);
			
			final List<Predicate> newHyps = Arrays.asList(rewriteOutput);
			final IHypAction forwardInf = ProverFactory.makeForwardInfHypAction(Collections.singleton(hyp), newHyps);
			List<IHypAction> hypActions = 
				Arrays.asList(
					forwardInf,
					getHypAction(hyp),
					ProverFactory.makeSelectHypAction(newHyps));
			return ProverFactory.makeProofRule(this, input, getDisplayName(hyp), hypActions);
			
//			
//			final IAntecedent[] antecedents = 
//				new IAntecedent[] { ProverFactory.makeAntecedent(
//						goal,
//						Arrays.asList(
//								forwardInf,
//								getHypAction(hyp),
//								ProverFactory.makeSelectHypAction(newHyps))) };
//			return ProverFactory.makeProofRule(this, input, goal,
//			getDisplayName(hyp), antecedents);
			
		}
	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		Set<Predicate> neededHyps = reader.getNeededHyps();
		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new Input(null);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp: neededHyps) {
			pred = hyp;
		}
		return new Input(pred);
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Nothing to do, all is in the generated rule
	}

	/**
	 * Returns whether this reasoner is applicable to the given predicate.
	 * 
	 * @param pred
	 *            the predicate to test for rewritability
	 * @return <code>true</code> if the given predicate might get rewritten by
	 *         this reasoner
	 */
	public abstract boolean isApplicable(Predicate pred);
	
	/**
	 * Returns the name to display in the generated rule.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten or
	 *            <code>null</code> if it is the goal that gets rewritten
	 * @return the name to display in the rule
	 */
	protected abstract String getDisplayName(Predicate pred);

	/**
	 * Apply the rewriting to the given predicate.
	 * 
	 * @param pred
	 *     predicate to rewrite
	 * @return an array of predicates which are the result of rewriting
	 */
	protected abstract Predicate[] rewrite(Predicate pred, FormulaFactory ff);
	
	/**
	 * Returns the action to perform on hypotheses.
	 * 
	 * @param pred
	 *            the hypothesis predicate that gets rewritten
	 * @return the action to perform on hypotheses.
	 */
	protected abstract IHypAction getHypAction(Predicate pred);

}
