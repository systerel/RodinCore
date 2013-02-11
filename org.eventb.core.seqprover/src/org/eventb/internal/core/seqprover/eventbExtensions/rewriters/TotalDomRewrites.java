/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
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
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;

public class TotalDomRewrites implements IVersionedReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".totalDom";
	private static final int VERSION = 2;
	
	public static class Input extends AbstractManualRewrites.Input {
	
		public static final String POSITION_KEY = "pos";
		public static final String SUBSTITUTE_KEY = "subst";

		Expression substitute;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position, Expression substitute) {
			super(pred, position);
			this.substitute = substitute;
		}	
		
		public Predicate getPred(){
			return pred;
		}
		
		public Expression getSubstitute(){
			return substitute;
		}
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public int getVersion() {
		return VERSION;
	}

	@ProverRule("DERIV_DOM_TOTALREL")
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;

		final TotalDomSubstitutions substitutions = new TotalDomSubstitutions(seq);
		substitutions.computeSubstitutions();

		final Predicate goal = seq.goal();
		final Predicate toRewrite;
		if (hyp == null) {
			toRewrite = goal;
		} else {
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			toRewrite = hyp;
		}
		final Expression function = getFunction(toRewrite, position);
		if (function == null) {
			return failure(input, toRewrite);
		}
		final FormulaFactory ff = seq.getFormulaFactory();
		final Predicate rewritten = rewrite(toRewrite, input, function, substitutions, ff);
		if (rewritten == null) {
			return failure(input, toRewrite);
		}
		final Predicate neededHyp = substitutions.getNeededHyp(function, input.substitute);
		if (neededHyp == null) {
			return failure(input, toRewrite);
		}

		if (hyp == null) {
			// Goal rewriting
			final Predicate newGoal = rewritten;
			final IAntecedent antecedent = ProverFactory
					.makeAntecedent(newGoal);
			return ProverFactory.makeProofRule(this, input, goal, neededHyp,
					getDisplayName(hyp, position), antecedent);
		} else {
			// Hypothesis rewriting
			final Predicate inferredHyp = rewritten;
			final List<IHypAction> hypActions = Arrays.<IHypAction> asList(
					makeForwardInfHypAction(singleton(hyp),
							singleton(inferredHyp)),
					makeHideHypAction(singleton(hyp)),
					makeSelectHypAction(singleton(inferredHyp)));
			final IAntecedent antecedent = ProverFactory.makeAntecedent(null,
					null, null, hypActions);
			return ProverFactory.makeProofRule(this, input, null, neededHyp,
					getDisplayName(hyp, position), antecedent);
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

	private Expression getFunction(Predicate pred, IPosition position) {
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Expression.KDOM) {
			return null;
		}
		return ((UnaryExpression) subFormula).getChild();
	}

	protected Predicate rewrite(Predicate pred, Input input, Expression function,
			TotalDomSubstitutions substitutions, FormulaFactory ff) {

		final Set<Expression> substitutes = substitutions.get(function);
		if (!substitutes.contains(input.substitute)) {
			return null;
		}

		return pred.rewriteSubFormula(input.position, input.substitute);

	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		final String posString = reader.getString(Input.POSITION_KEY);
		final IPosition position = FormulaFactory.makePosition(posString);
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