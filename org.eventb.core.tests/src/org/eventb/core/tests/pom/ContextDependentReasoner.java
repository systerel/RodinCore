/*******************************************************************************
 * Copyright (c) 2014, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * A context dependent reasoner used for testing.
 * 
 * @author beauger
 */
public class ContextDependentReasoner extends EmptyInputReasoner {

	public static final String REASONER_ID = "org.eventb.core.tests.contextDependentReasoner";

	// current validity of the reasoner context
	private static boolean contextValidity = false;

	public static void setContextValidity(boolean contextValidity) {
		ContextDependentReasoner.contextValidity = contextValidity;
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		if (contextValidity) {
			final LiteralPredicate newGoal = seq.getFormulaFactory().makeLiteralPredicate(Formula.BTRUE, null);
			final IAntecedent ante = makeAntecedent(newGoal);
			return makeProofRule(this, input, seq.goal(), "Success with context", ante);
		} else {
			return reasonerFailure(this, input, "Failure with context");
		}
	}

}
