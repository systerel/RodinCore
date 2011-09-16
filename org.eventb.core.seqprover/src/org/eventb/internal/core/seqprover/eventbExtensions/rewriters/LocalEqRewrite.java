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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Collections.singleton;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;

/**
 * Rewrite an identifier at a given position in the goal or in a hypothesis
 * using the predicate given in input. This predicate must denote an equality,
 * and the identifier must be one of the sides of the operator. <br>
 * Here are the rules implemented (x is the identifier) :<br>
 * H, f(E), x=E ⊢ G(x)<br>
 * --------------------<br>
 * H, f(x), x=E ⊢ G(x)<br>
 * <br>
 * H, f(x), x=E ⊢ G(E)<br>
 * --------------------<br>
 * H, f(x), x=E ⊢ G(x)<br>
 * <br>
 * H, f(E), E=x ⊢ G(x)<br>
 * --------------------<br>
 * H, f(x), E=x ⊢ G(x)<br>
 * <br>
 * H, f(x), E=x ⊢ G(E)<br>
 * --------------------<br>
 * H, f(x), E=x ⊢ G(x)<br>
 * <br>
 * 
 * @author Emmanuel Billaud
 */
public class LocalEqRewrite implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".locEq";

	private static final String POSITION_KEY = "pos";

	public static class Input extends AbstractManualRewrites.Input {

		final Predicate equality;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>, the
		 * rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred, IPosition position, Predicate equality) {
			super(pred, position);
			this.equality = equality;
		}

		public Predicate getEquality() {
			return equality;
		}
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(POSITION_KEY, ((Input) input).getPosition().toString());
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final IPosition position = deserializePosition(reader);
		if (reader.getGoal() != null) {
			// Goal rewriting
			final Set<Predicate> neededHyps = reader.getNeededHyps();
			if (neededHyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"There should be only one needed hypothesis"));
			}
			final Predicate neededHyp = neededHyps.iterator().next();
			return new Input(null, position, neededHyp);
		} else {
			// Hypothesis rewriting
			final Predicate[] hyps = deserializeHypotheses(reader);
			final FormulaFactory ff = reader.getFormulaFactory();
			final Input input = makeInput(hyps, position, ff);
			if (input == null) {
				throw new SerializeException(new IllegalStateException(
						"Cannot proceed re-writing with the given hypotheses"));
			}
			return input;
		}
	}

	/*
	 * Try to re-create the input using the two hypotheses and position
	 * serialized, as well as the predicate created by the rule.
	 */
	private Input makeInput(Predicate[] hyps, IPosition position,
			FormulaFactory ff) {
		final Predicate hyp0 = hyps[0];
		final Predicate hyp1 = hyps[1];
		final Predicate result = hyps[2];
		final Input input0 = makeInput(hyp0, hyp1, position, result, ff);
		if (input0 != null) {
			return input0;
		}
		final Input input1 = makeInput(hyp1, hyp0, position, result, ff);
		if (input1 != null) {
			return input1;
		}
		return null;
	}

	private Input makeInput(Predicate equality, Predicate toRewrite,
			IPosition position, Predicate result, FormulaFactory ff) {
		if (equality.getTag() != EQUAL) {
			return null;
		}
		final Formula<?> replaced = toRewrite.getSubFormula(position);
		if (replaced == null) {
			return null;
		}
		final RelationalPredicate rEq = (RelationalPredicate) equality;
		final Expression right = rEq.getRight();
		final Expression left = rEq.getLeft();
		if (makeInput(right, left, replaced, toRewrite, position, result, ff)) {
			return new Input(toRewrite, position, equality);
		}
		if (makeInput(left, right, replaced, toRewrite, position, result, ff)) {
			return new Input(toRewrite, position, equality);
		}
		return null;
	}

	private boolean makeInput(Expression ident, Expression substitute,
			Formula<?> replaced, Predicate toRewrite, IPosition position,
			Predicate result, FormulaFactory ff) {
		if (ident.getTag() != FREE_IDENT) {
			return false;
		}
		if (!ident.equals(replaced)) {
			return false;
		}
		final Predicate rewritten = toRewrite.rewriteSubFormula(position,
				substitute, ff);
		if (!rewritten.equals(result)) {
			return false;
		}
		return true;
	}

	private IPosition deserializePosition(IReasonerInputReader reader)
			throws SerializeException {
		final String posString = reader.getString(POSITION_KEY);
		try {
			return FormulaFactory.makePosition(posString);
		} catch (IllegalArgumentException e) {
			throw new SerializeException(e);
		}
	}

	/*
	 * Deserializes three hypotheses, one being rewritten, one being the
	 * equality that defines the substitution, and the last one in the array is
	 * the result of the re-writing.
	 */
	private Predicate[] deserializeHypotheses(IReasonerInputReader reader)
			throws SerializeException {
		final IForwardInfHypAction fwd = deserializeForwardInf(reader);
		final Collection<Predicate> hyps = fwd.getHyps();
		if (hyps.size() != 2) {
			throw new SerializeException(new IllegalStateException(
					"There should be exactly two hypotheses in the inference"));
		}
		final Collection<Predicate> inferredHyps = fwd.getInferredHyps();
		if (inferredHyps.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"There should be exactly one inferred hypothesis"));
		}
		final Iterator<Predicate> iterator = hyps.iterator();
		final Predicate[] result = { iterator.next(), iterator.next(),
				inferredHyps.iterator().next() };
		return result;
	}

	private IForwardInfHypAction deserializeForwardInf(
			IReasonerInputReader reader) throws SerializeException {
		final IAntecedent antecedent = deserializeAntecedent(reader);
		final List<IHypAction> actions = antecedent.getHypActions();
		if (actions.size() < 1) {
			throw new SerializeException(new IllegalStateException(
					"There should be at least one action"));
		}
		final IHypAction first = actions.get(0);
		if (!(first instanceof IForwardInfHypAction)) {
			throw new SerializeException(new IllegalStateException(
					"First action shall be a forward inference"));
		}
		return (IForwardInfHypAction) first;
	}

	private IAntecedent deserializeAntecedent(IReasonerInputReader reader)
			throws SerializeException {
		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException(
					"There should be exactly one antecedent"));
		}
		return antecedents[0];
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (!(input instanceof Input)) {
			return reasonerFailure(this, input, "Wrong input's class.");
		}
		final FormulaFactory ff = seq.getFormulaFactory();
		final Input myInput = (Input) input;
		final Predicate hyp = myInput.getPred();
		final IPosition position = myInput.getPosition();
		final Predicate eqPred = myInput.getEquality();
		if (!seq.containsHypothesis(eqPred)) {
			return reasonerFailure(this, input, eqPred
					+ " is not a hypothesis of the given sequent");
		}
		if (eqPred.getTag() != EQUAL) {
			return reasonerFailure(this, input, eqPred
					+ " does not denote an equality");
		}
		if (hyp == null) {
			final Predicate goal = seq.goal();
			final Predicate newGoal = rewrite(goal, position, eqPred, ff);
			if (newGoal == null) {
				return reasonerFailure(this, input,
						"The goal cannot be re-written with the given input.");
			}
			return makeProofRule(this, input, goal, eqPred, "lae in goal",
					makeAntecedent(newGoal));
		} else {
			if (!(seq.containsHypothesis(hyp))) {
				return reasonerFailure(this, input, hyp
						+ " is not a hypothesis of the given sequent");
			}
			final Predicate newHyp = rewrite(hyp, position, eqPred, ff);
			if (newHyp == null) {
				return reasonerFailure(this, input,
						"The hypothesis cannot be re-written with the given input.");
			}
			final Set<Predicate> neededHyps = new HashSet<Predicate>();
			neededHyps.add(hyp);
			neededHyps.add(eqPred);
			List<IHypAction> hypAct = new ArrayList<IHypAction>();
			hypAct.add(makeForwardInfHypAction(neededHyps, singleton(newHyp)));
			hypAct.add(makeHideHypAction(singleton(hyp)));
			return makeProofRule(this, input, null, "lae in " + hyp.toString(),
					hypAct);
		}
	}

	/**
	 * Try to re-write the predicate <code>pred</code> using the given position,
	 * and the predicate denoting an equality. Returns the predicte re-written.
	 */
	private Predicate rewrite(final Predicate pred, final IPosition position,
			final Predicate equality, final FormulaFactory ff) {
		final Expression ident = (Expression) pred.getSubFormula(position);
		final Expression exp = testIdent((RelationalPredicate) equality, ident);
		if (exp == null) {
			return null;
		}
		return pred.rewriteSubFormula(position, exp, ff);
	}

	/**
	 * Check that the left side or the right side of the predicate
	 * <code>pred</code> denoting an equality is an identifier equal to ident.
	 * Returns the other side of the equality.
	 */
	private Expression testIdent(RelationalPredicate pred, Expression ident) {
		final Expression left = pred.getLeft();
		final Expression right = pred.getRight();
		if (left.getTag() == FREE_IDENT && left.equals(ident)) {
			return right;
		}
		if (right.getTag() == FREE_IDENT && right.equals(ident)) {
			return left;
		}
		return null;

	}

}
