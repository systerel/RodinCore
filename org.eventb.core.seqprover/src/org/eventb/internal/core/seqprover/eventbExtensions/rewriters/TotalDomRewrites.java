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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeSelectHypAction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class TotalDomRewrites implements IReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".totalDom";

	public static class Input implements IReasonerInput {
		public static final String POSITION_KEY = "pos";
		public static final String SUBSTITUTE_KEY = "subst";

		Predicate pred;

		IPosition position;

		Expression substitute;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position, Expression substitute) {
			this.pred = pred;
			this.position = position;
			this.substitute = substitute;
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

	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;

		final TotalDomSubstitutions substitutions = new TotalDomSubstitutions(seq);
		substitutions.computeSubstitutions();

		final Predicate goal = seq.goal();
		if (hyp == null) {
			// Goal rewriting
			Predicate newGoal = rewrite(goal, input, substitutions);

			if (newGoal == null) {
				return failure(input, goal);
			}
			final IAntecedent antecedent = ProverFactory
					.makeAntecedent(newGoal);
			return ProverFactory.makeProofRule(this, input, goal,
					getDisplayName(hyp, position), antecedent);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}

			Predicate inferredHyp = rewrite(hyp, input, substitutions);
			if (inferredHyp == null) {
				return failure(input, hyp);
			}

			final List<IHypAction> hypActions = Arrays.<IHypAction> asList(
					makeForwardInfHypAction(singleton(hyp),
							singleton(inferredHyp)),
					makeHideHypAction(singleton(hyp)),
					makeSelectHypAction(singleton(inferredHyp)));

			return ProverFactory.makeProofRule(this, input, getDisplayName(hyp,
					position), hypActions);
		}
	}

	private IReasonerOutput failure(final Input input, final Predicate pred) {
		return ProverFactory.reasonerFailure(this, input, "Rewriter "
				+ getReasonerID() + " is inapplicable for"
				+ (input.pred == null ? " goal " : " hypothesis ") + pred
				+ " at position " + input.position + " with parameters "
				+ input.substitute);
	}

	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return "total function dom substitution in goal";
		return "total function dom substitution in hyp ("
				+ pred.getSubFormula(position) + ")";
	}

	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	protected Predicate rewrite(Predicate pred, Input input,
			TotalDomSubstitutions substitutions) {
		final Formula<?> subFormula = pred.getSubFormula(input.position);
		if (subFormula.getTag() != Expression.KDOM) {
			return null;
		}

		final Set<Expression> substitutes = substitutions
				.get(((UnaryExpression) subFormula).getChild());
		if (substitutes.isEmpty()) {
			return null;
		}

		final Expression substitute = fetchSubstitute(input.substitute,
				substitutes);
		if (substitute == null) {
			return null;
		}

		return pred.rewriteSubFormula(input.position, substitute,
				FormulaFactory.getDefault());

	}

	private static Expression fetchSubstitute(Expression substCandidate,
			Set<Expression> substitutes) {
		if (substitutes.contains(substCandidate)) {
			return substCandidate;
		}
		return null;
	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		final FormulaFactory ff = FormulaFactory.getDefault();
		final String posString = reader.getString(Input.POSITION_KEY);
		final IPosition position = ff.makePosition(posString);
		final Expression[] substitutes = reader.getExpressions(Input.SUBSTITUTE_KEY);
		
		if (substitutes.length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one substitute!"));
		}
		
		final Expression substitute = substitutes[0];
		
		if (reader.getGoal() != null) {
			// Goal rewriting
			return new Input(null, position, substitute);
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
		if (hypAction instanceof IForwardInfHypAction) {
			final IForwardInfHypAction fHypAction = (IForwardInfHypAction) hypAction;
			final Collection<Predicate> hyps = fHypAction.getHyps();
			if (hyps.size() != 1) {
				throw new SerializeException(
						new IllegalStateException(
								"Expected single required hyp in first forward hyp action!"));
			}
			return new Input(hyps.iterator().next(), position, substitute);
		} else {
			throw new SerializeException(new IllegalStateException(
					"Expected first hyp action to be a forward hyp action!"));
		}
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {

		// Serialise position only, the predicate is contained inside the rule
		writer.putString(Input.POSITION_KEY, ((Input) input).position
				.toString());
		writer.putExpressions(Input.SUBSTITUTE_KEY, ((Input) input).substitute);
	}

}