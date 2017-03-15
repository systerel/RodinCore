/*******************************************************************************
 * Copyright (c) 2006, 2017 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeDeselectHypAction;
import static org.eventb.core.seqprover.eventbExtensions.DLib.False;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Generates a proof rule to contradict the goal and falsify a given hypothesis
 * simultaneously.
 * <p>
 * In case no hypothesis is given, the reasoner assumes the given hypothesis is
 * 'true'.
 * </p>
 * 
 * @author Farhad Mehta
 */
public class Contr extends HypothesisReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".contr";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule({ "CONTRADICT_L", "CONTRADICT_R" })
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) {

		final Predicate newGoal;
		final IHypAction deselect;
		if (pred == null) {
			newGoal = False(sequent.getFormulaFactory());
			deselect = null;
		} else {
			newGoal = makeNeg(pred);
			deselect = makeHypAction(pred);
		}
		final Predicate negOldGoal = makeNeg(sequent.goal());
		final Set<Predicate> newHyps = breakPossibleConjunct(negOldGoal);
		return new IAntecedent[] { makeAntecedent(newGoal, newHyps, deselect) };
	}
	
	protected IHypAction makeHypAction(Predicate pred) {
		return makeDeselectHypAction(singleton(pred));
	}

	@Override
	protected String getDisplay(Predicate pred) {
		if (pred == null) {
			return "ct goal";
		}
		return "ct hyp (" + pred + ")";
	}

}
