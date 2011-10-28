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

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.internal.core.seqprover.Util.getNullProofMonitor;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner.Input;
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
 * @author Emmanuel Billaud
 */
public class MembershipGoalTac implements ITactic {

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {

		if (pm == null) {
			pm = getNullProofMonitor();
		}
		final IProverSequent sequent = ptNode.getSequent();
		final Predicate goal = sequent.goal();
		if (goal.getTag() != IN) {
			return goal + " is not a membership";
		}
		final FormulaFactory ff = sequent.getFormulaFactory();
		final Set<Predicate> hyps = getUsefulHyps(sequent);
		final MembershipGoalImpl mbGoalImpl = new MembershipGoalImpl(goal,
				hyps, ff, pm);
		final Rationale rationale = mbGoalImpl.search();
		if (rationale == null) {
			return "Cannot discharge the goal";
		}
		final Input input = new Input(rationale.getLeafs());
		return reasonerTac(new MembershipGoal(), input).apply(ptNode, pm);
	}

	private static Set<Predicate> getUsefulHyps(IProverSequent sequent) {
		final Set<Predicate> hyps = new HashSet<Predicate>();
		for (final Predicate hyp : sequent.visibleHypIterable()) {
			switch (hyp.getTag()) {
			case IN:
			case SUBSET:
			case SUBSETEQ:
				hyps.add(hyp);
				break;
			case EQUAL:
				if (isSetEquality((RelationalPredicate) hyp)) {
					hyps.add(hyp);
				}
				break;
			}
		}
		return hyps;
	}

	public static boolean isSetEquality(RelationalPredicate rHyp) {
		return rHyp.getLeft().getType().getBaseType() != null;
	}

}