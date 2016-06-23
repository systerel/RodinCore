/*******************************************************************************
 * Copyright (c) 2009, 2016 Systerel and others.
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

import java.util.Collections;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class ReasonerV2 extends EmptyInputReasoner implements IVersionedReasoner {

	public static final String REASONER_ID = "org.eventb.core.tests.reasonerV2";
	public static final int VERSION = 2;
	public static final int OLD_VERSION = VERSION - 1;

	public static boolean fail = false;

	/**
	 * Reset the parameters to their default value.
	 * <p>
	 * This method MUST be called before every test that uses an
	 * UncertainReasoner.
	 * </p>
	 */
	public static void reset() {
		fail = false;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		if (fail) {
			return ProverFactory.reasonerFailure(this, input, "desired failure");
		}
		return makeRule(seq, VERSION, IConfidence.DISCHARGED_MAX);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public int getVersion() {
		return VERSION;
	}

	public static IProofRule makeRule(IProverSequent sequent, int version, int confidence) {
		final IReasonerRegistry registry = SequentProver.getReasonerRegistry();
		final IReasonerDesc desc = registry.getReasonerDesc(REASONER_ID + ":" + version);
		final Set<Predicate> noHyps = Collections.emptySet();
		final IAntecedent ante = makeAntecedent(sequent.getFormulaFactory().makeLiteralPredicate(Formula.BTRUE, null));
		return makeProofRule(desc, new EmptyInput(), sequent.goal(), noHyps, confidence, desc.getName(), ante);
	}
}
