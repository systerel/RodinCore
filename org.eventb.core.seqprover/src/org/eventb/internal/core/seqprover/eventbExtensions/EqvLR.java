/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Manual rewriter reasoner.
 * <p>
 * Rewrites the predicates contained in the sequent using the predicate given in
 * input. This predicate must denote an equivalence (A ⇔ B). Replace all
 * occurences of A by the predicate B in the sequent.
 * </p>
 * @author Josselin Dolhen
 */
public class EqvLR extends Eqv {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".eqvLR";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected Predicate getFrom(Predicate hyp) {
		return Lib.eqvLeft(hyp);
	}

	@Override
	protected Predicate getTo(Predicate hyp) {
		return Lib.eqvRight(hyp);
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "eqv LR with " + pred;
	}

	@ProverRule("EQV_LR")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate hypEq) throws IllegalArgumentException {
		return super.getAntecedents(sequent, hypEq);
	}

}
