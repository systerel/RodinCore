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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
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
 * and the identifier must be one of the sides of the operator.
 * <br>
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

	public static class Input extends AbstractManualRewrites.Input {

		public static final String POSITION_KEY = "pos";

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
		writer.putString(Input.POSITION_KEY, ((Input) input).getPosition()
				.toString());
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String posString = reader.getString(Input.POSITION_KEY);
		if (reader.getAntecedents().length != 1) {
			throw new SerializeException(new IllegalStateException(
					"There should be exactly one antecedent"));
		}
		final IPosition position = FormulaFactory.makePosition(posString);
		final Set<Predicate> neededHyps = reader.getNeededHyps();
		final Predicate[] array = neededHyps.toArray(new Predicate[0]);
		final Predicate goal = reader.getGoal();
		if (goal != null) {
			if (neededHyps.size() != 1) {
				throw new SerializeException(new IllegalStateException(
						"There should be only one needed hypothesis"));
			}
			final Predicate neededHyp = array[0];
			return computeInput(position, goal, neededHyp, true);
		} else {
			if (neededHyps.size() != 2) {
				throw new SerializeException(new IllegalStateException(
						"There should be exactly two hypotheses"));
			}
			final Predicate pred0 = array[0];
			final Predicate pred1 = array[1];
			try {
				return computeInput(position, pred0, pred1, false);
			} catch (SerializeException se1) {
				try {
					return computeInput(position, pred1, pred0, false);
				} catch (SerializeException se2) {
					throw new SerializeException(new IllegalStateException(
							"Impossible to proceed re-writing"));
				}
			}
		}
	}

	private IReasonerInput computeInput(final IPosition position,
			final Predicate rewritten, final Predicate neededHyp, boolean isGoal)
			throws SerializeException {
		if (neededHyp.getTag() != EQUAL) {
			throw new SerializeException(new IllegalStateException(neededHyp
					+ " does not denote an equality"));
		}
		final Expression[] testIdent = testIdent((RelationalPredicate) neededHyp);
		if (testIdent == null) {
			throw new SerializeException(new IllegalStateException(neededHyp
					+ " is not related to any identifier"));
		}
		final Expression ident = testIdent[0];
		final Formula<?> subFormula = rewritten.getSubFormula(position);
		if (!ident.equals(subFormula)) {
			throw new SerializeException(new IllegalStateException(ident
					+ " is not equal to " + subFormula));
		}
		if (isGoal) {
			return new Input(null, position, neededHyp);
		}
		return new Input(rewritten, position, neededHyp);
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (!(input instanceof Input)) {
			return reasonerFailure(this, input, "Wrong input's class.");
		}
		final FormulaFactory ff = seq.getFormulaFactory();
		final Input myInput = (Input) input;
		final Predicate pred = myInput.getPred();
		final IPosition position = myInput.getPosition();
		final Predicate equality = myInput.getEquality();
		if (!seq.containsHypothesis(equality)) {
			return reasonerFailure(this, input, equality
					+ " is not a hypothesis of the given sequent");
		}
		if (equality.getTag() != EQUAL) {
			return reasonerFailure(this, input, equality
					+ " does not denote an equality");
		}
		final Expression[] testIdent = testIdent((RelationalPredicate) equality);
		if (testIdent == null) {
			return reasonerFailure(this, input, equality
					+ " is not related to any identifier");
		}
		final Expression ident = testIdent[0];
		final Expression exp = testIdent[1];
		if (pred == null) {
			final Predicate goal = seq.goal();
			final Formula<?> subFormula = goal.getSubFormula(position);
			if (!ident.equals(subFormula)) {
				return reasonerFailure(this, input, ident + " is not equal to "
						+ subFormula);
			}
			final Predicate newGoal = goal.rewriteSubFormula(position, exp, ff);
			return makeProofRule(this, input, goal, equality, "lae in goal",
					makeAntecedent(newGoal));
		} else {
			if (!(seq.containsHypothesis(pred))) {
				return reasonerFailure(this, input, pred
						+ " is not a hypothesis of the given sequent");
			}
			final Formula<?> subFormula = pred.getSubFormula(position);
			if (!ident.equals(subFormula)) {
				return reasonerFailure(this, input, ident + " is not equal to "
						+ subFormula);
			}
			final Predicate newHyp = pred.rewriteSubFormula(position, exp, ff);
			final Set<Predicate> neededHyps = new HashSet<Predicate>();
			neededHyps.add(pred);
			neededHyps.add(equality);
			List<IHypAction> hypAct = new ArrayList<IHypAction>();
			hypAct.add(makeForwardInfHypAction(neededHyps, singleton(newHyp)));
			hypAct.add(makeHideHypAction(singleton(pred)));
			return makeProofRule(this, input, neededHyps, "lae in "+pred, hypAct);
		}
	}

	private Expression[] testIdent(RelationalPredicate pred) {
		final Expression left = pred.getLeft();
		final Expression right = pred.getRight();
		Expression[] result = new Expression[2];
		if (left.getTag() == FREE_IDENT) {
			result[0] = left;
			result[1] = right;
			return result;
		}
		if (right.getTag() == FREE_IDENT) {
			result[0] = right;
			result[1] = left;
			return result;
		}
		return null;

	}

}
