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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.ast.Formula.IN;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale;

/**
 * Try to find hypotheses to apply the reasoner MembershipGoal in order to
 * discharge a sequent such as :
 * 
 * <pre>
 * <code>H, x∈A, A⊆B ... C⊂D ⊢ x∈D</code> iff A⊆B⊂ ... ⊆ ... ⊂ ... ⊆C⊂D
 * </pre>
 * 
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoalTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {

		final IProverSequent sequent = ptNode.getSequent();
		final FormulaFactory ff = sequent.getFormulaFactory();
		final Set<Predicate> hyps = getUsefullHyps(sequent);
		final Predicate goal = sequent.goal();
		if (goal.getTag() != IN) {
			return goal + " is not an inclusion";
		}
		final MembershipGoalImpl mbGoalImpl = new MembershipGoalImpl(goal,
				hyps, ff, pm);
		final Rationale search = mbGoalImpl.search();
		if (search == null) {
			return "Cannot find a path";
		}
		final Set<Predicate> neededHyps = search.getLeafs();
		final HypothesesReasoner.Input input = new HypothesesReasoner.Input(
				neededHyps.toArray(new Predicate[neededHyps.size()]));
		return BasicTactics.reasonerTac(new MembershipGoal(), input).apply(
				ptNode, pm);

	}

	private static Set<Predicate> getUsefullHyps(IProverSequent sequent) {
		Set<Predicate> hyps = new HashSet<Predicate>();
		for (Predicate hyp : sequent.visibleHypIterable()) {
			switch (hyp.getTag()) {
			case Formula.IN:
			case Formula.EQUAL:
			case Formula.SUBSET:
			case Formula.SUBSETEQ:
				hyps.add(hyp);
			}
		}
		return hyps;
	}

}