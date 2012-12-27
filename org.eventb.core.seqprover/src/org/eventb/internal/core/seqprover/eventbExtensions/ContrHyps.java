/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner.Input;
import org.eventb.internal.core.seqprover.ReasonerFailure;

/**
 * Discharges a sequent that contains a hypothesis and its contradiction.
 * <p>
 * For serialization, we do not remember anything about the input, as the
 * information can be inferred from the set of needed hypotheses. Upon
 * deserialization, we just extract the input from the set of needed hypotheses.
 * </p>
 * 
 * @author Nicolas Beauger
 * @author Laurent Voisin
 */
public class ContrHyps implements IVersionedReasoner {

	private static String REASONER_ID = SequentProver.PLUGIN_ID + ".contrHyps";

	private static final IAntecedent[] NO_ANTECEDENT = new IAntecedent[0];

	public String getReasonerID() {
		return REASONER_ID;
	}

	public int getVersion() {
		return 1;
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {
		// Do nothing
	}

	public final Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final Set<Predicate> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() == 1) {
			// For backward compatibility with faulty version
			return new Input(neededHyps.iterator().next());
		}

		// Need to find the hypothesis which is contradicted by the others
		for (final Predicate hyp : neededHyps) {
			final Set<Predicate> cntrs = contradictingPredicates(hyp);
			if (cntrs != null && neededHyps.containsAll(cntrs)) {
				return new Input(hyp);
			}
		}
		throw new SerializeException(new IllegalStateException(
				"Unexpected set of needed hypothesis: " + neededHyps));
	}

	@ProverRule("CNTR")
	public final IReasonerOutput apply(IProverSequent seq,
			IReasonerInput input, IProofMonitor pm) {
		final Predicate hyp = ((Input) input).getPred();
		if (hyp == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Invalid input (null hypothesis)");
		}
		if (!seq.containsHypothesis(hyp)) {
			return ProverFactory.reasonerFailure(this, input,
					"Nonexistent hypothesis: " + hyp);
		}
		final Set<Predicate> neededHyps = contradictingPredicates(hyp);
		if (neededHyps == null) {
			return new ReasonerFailure(this, input, "Predicate " + hyp
					+ " is not a negation");
		}
		if (!seq.containsHypotheses(neededHyps)) {
			return new ReasonerFailure(this, input, "Predicate " + hyp
					+ " is not contradicted by hypotheses");
		}
		neededHyps.add(hyp);

		final Predicate goal = null;
		final String display = "ct in hyps (" + hyp + ")";
		return ProverFactory.makeProofRule(this, input, goal, neededHyps,
				display, NO_ANTECEDENT);
	}

	public static Set<Predicate> contradictingPredicates(Predicate pred) {
		if (!Lib.isNeg(pred)) {
			return null;
		}
		return Lib.breakPossibleConjunct(Lib.negPred(pred));
	}

}
