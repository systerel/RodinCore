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
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Discharges a sequent whose goal is <code>f(x)∈A</code> if there exist
 * hypotheses such as <code>f(x)∈B</code> and : <code>B⊆A</code> or
 * <code>B⊂A</code>.
 * 
 * @author Emmanuel Billaud
 */
public class FunImgInclusionGoal extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".funImgInclusionG";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final Predicate goal = seq.goal();
		if (!Lib.isInclusion(goal)) {
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not an inclusion");
		}
		final Expression funApp = ((RelationalPredicate) goal).getLeft();
		if (!Lib.isFunApp(funApp)) {
			return ProverFactory.reasonerFailure(this, input,
					"Left member is not a function application");
		}
		final Expression set = ((RelationalPredicate) goal).getRight();

		for (Predicate hyp : seq.visibleHypIterable()) {
			final Expression setHyp = checkHypothesis(hyp, funApp);
			if (setHyp == null) {
				continue;
			}
			final Predicate neededhyp = subsetOf(seq, setHyp, set);
			if (neededhyp == null) {
				continue;
			}
			final Set<Predicate> neededHyps = new HashSet<Predicate>();
			neededHyps.add(neededhyp);
			neededHyps.add(hyp);
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					"Function Image Inclusion", new IAntecedent[0]);
		}
		return ProverFactory.reasonerFailure(this, input,
				"Cannot find hypothesis discharging the goal");
	}

	/**
	 * If the predicate is in the form <code>f(x)∈A</code> and if the expression
	 * <code>funApp</code> is equal to <code>f(x)</code> it returns the set
	 * <code>A</code>.
	 * 
	 * @param hyp
	 *            the predicate to compared (should be a hypothesis of the
	 *            sequent)
	 * @param funApp
	 *            the function application to find
	 * @return the set containing the image of the function application if the
	 *         hypothesis is in the form <code>f(x)∈A</code> and if the
	 *         expression <code>funApp</code> is equal to <code>f(x)</code>.
	 */
	private Expression checkHypothesis(Predicate hyp, Expression funApp) {
		if (!Lib.isInclusion(hyp)) {
			return null;
		}
		final Expression left = ((RelationalPredicate) hyp).getLeft();
		if (!left.equals(funApp)) {
			return null;
		}
		return ((RelationalPredicate) hyp).getRight();
	}

	/**
	 * Returns the predicate <code>included ⊆ include</code> if it's a
	 * hypothesis, else <code>included ⊂ include</code> if it's a hypothesis,
	 * else <code>null</code>.
	 * 
	 * @param sequent
	 *            the sequent on which the tactic is applied
	 * @param included
	 *            the expression tested to be included in the
	 *            <code>include</code>
	 * @param include
	 *            the expression tested to include the <code>included</code>
	 * 
	 * @return <code>included ⊆ include</code> if it's a hypothesis, else
	 *         <code>included ⊂ include</code> if it's a hypothesis, else
	 *         <code>null</code>.
	 */
	private Predicate subsetOf(final IProverSequent sequent,
			final Expression included, final Expression include) {
		final FormulaFactory ff = sequent.getFormulaFactory();
		final Predicate subseteq = ff.makeRelationalPredicate(Formula.SUBSETEQ,
				included, include, null);
		if (sequent.containsHypothesis(subseteq)) {
			return subseteq;
		}
		final Predicate subset = ff.makeRelationalPredicate(Formula.SUBSET,
				included, include, null);
		if (sequent.containsHypothesis(subset)) {
			return subset;
		}
		return null;
	}
}
