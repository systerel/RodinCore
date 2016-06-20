/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import static org.eventb.core.seqprover.IConfidence.DISCHARGED_MAX;
import static org.eventb.core.seqprover.IConfidence.UNCERTAIN_MAX;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.Q;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Reasoner with setters for confidence and success.
 * <p>
 * Produces antecedent "1=0" with Q as added identifier, if not already in the
 * type environment.
 * </p>
 * 
 * @author beauger
 *
 */
public class UncertainReasoner extends EmptyInputReasoner {

	private static final String REASONER_ID = "org.eventb.core.seqprover.tests.uncertainReasoner";

	// static fields because we need to change the behavior of the reasoner
	// that is cached in the registry
	public static boolean certain = false;
	public static boolean fail = false;

	/**
	 * Reset the parameters to their default value.
	 * <p>
	 * This method MUST be called before every test that uses an
	 * UncertainReasoner.
	 * </p>
	 */
	public static void reset() {
		certain = false;
		fail = false;
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		if (fail) {
			return ProverFactory.reasonerFailure(this, input, "desired failure");
		}
		final FreeIdentifier[] identsQ = Q.getFreeIdentifiers();
		// add only if fresh ident (else rule application fails)
		final boolean addQ = !seq.typeEnvironment().contains(identsQ[0]);
		final IAntecedent ante = makeAntecedent(Q, null, addQ ? identsQ : null, null);
		return makeProofRule(this, input, seq.goal(), null, certain ? DISCHARGED_MAX : UNCERTAIN_MAX, "Ante", ante);
	}

}
