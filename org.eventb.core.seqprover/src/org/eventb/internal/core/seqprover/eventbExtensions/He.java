/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring around a hierarchy of classes
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class He extends EqHe {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".he";
	private static final int REASONER_VERSION = 1;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public int getVersion() {
		return REASONER_VERSION;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "he with " + pred;
	}

	@Override
	protected Expression getFrom(Predicate hyp) {
		return Lib.eqRight(hyp);
	}

	@Override
	protected Expression getTo(Predicate hyp) {
		return Lib.eqLeft(hyp);
	}

	@ProverRule("EQL_RL")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate hypEq) throws IllegalArgumentException {
		return super.getAntecedents(sequent, hypEq);
	}

}
