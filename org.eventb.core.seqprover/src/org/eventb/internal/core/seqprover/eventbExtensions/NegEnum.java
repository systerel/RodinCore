/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInput;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInputReasoner;

/**
 * 
 * @author htson, Farhad Mehta
 * 
 * TODO : make this an empty input reasoner, similar to AutoImpF
 *
 */
public class NegEnum extends MultiplePredInputReasoner implements IReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".negEnum";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule( { "NEG_IN_L", "NEG_IN_R" })
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (!(input instanceof MultiplePredInput)) {
			return ProverFactory.reasonerFailure(this, input,
					"Input is not a multiple predicate input");
		}
		MultiplePredInput mInput = (MultiplePredInput) input;
		Predicate[] predicates = mInput.getPredicates();
		if (predicates.length != 2) {
			return ProverFactory.reasonerFailure(this, input,
					"Invalid number of predicate input");			
		}
		Predicate pred0 = predicates[0];
		Predicate pred1 = predicates[1];
		if (!Lib.isInclusion(pred0)) {
			return ProverFactory.reasonerFailure(this, input, "Hypothesis "
					+ pred0 + " is not an inclusion");
		}
		Expression right = ((RelationalPredicate) pred0).getRight();
		if (!Lib.isSetExtension(right)) {
			return ProverFactory.reasonerFailure(this, input, "Predicate "
					+ right + " is not a set extension");
		}
		if (!Lib.isNeg(pred1)) {
			return ProverFactory.reasonerFailure(this, input, "Hypothesis "
					+ pred1 + " is not a negation");		
		}
		Predicate child = ((UnaryPredicate) pred1).getChild();
		if (!Lib.isEq(child)) {
			return ProverFactory.reasonerFailure(this, input, "Predicate "
					+ child + " is not an equality");
		}
		
		Expression[] members = ((SetExtension) right).getMembers();
		Expression E = ((RelationalPredicate) pred0).getLeft();
		RelationalPredicate rPred = (RelationalPredicate) child;
		Expression b = null;
		if (E.equals(rPred.getLeft())) {
			b = rPred.getRight();
		}
		else if (E.equals(rPred.getRight())) {
			b = rPred.getLeft();
		}
		if (b != null) {
			Collection<Expression> newMembers = new ArrayList<Expression>(
					members.length);
			for (Expression member : members) {
				if (!b.equals(member)) {
					newMembers.add(member);
				}
			}
			if (newMembers.size() < members.length) {
				List<IHypAction> hypActions = new ArrayList<IHypAction>(3);
				Set<Predicate> hyps = new HashSet<Predicate>(2);
				hyps.add(pred0);
				hyps.add(pred1);
				final FormulaFactory ff = seq.getFormulaFactory();
				SetExtension setExtension = ff.makeSetExtension(newMembers,
						null);
				Predicate inferredHyp = ff.makeRelationalPredicate(
						Predicate.IN, E, setExtension, null);
				hypActions.add(ProverFactory.makeForwardInfHypAction(hyps,
						Collections.singleton(inferredHyp)));
				hypActions.add(ProverFactory.makeDeselectHypAction(hyps));

				IAntecedent anticident = ProverFactory.makeAntecedent(null,
						null, null, hypActions);

				IProofRule reasonerOutput = ProverFactory.makeProofRule(this,
						input, null, hyps, null, "negEnum (" + pred0 + ","
								+ pred1 + ")", anticident);

				return reasonerOutput;
			}
		}
		return ProverFactory.reasonerFailure(this, input,
				"Negation enumeration is not applicable for hypotheses "
						+ pred0 + " and " + pred1);
	}


}
