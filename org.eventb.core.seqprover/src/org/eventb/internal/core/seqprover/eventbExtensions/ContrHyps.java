/*******************************************************************************
 * Copyright (c) 2009, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.getStrongerNegative;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.getStrongerPositive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner.Input;
import org.eventb.internal.core.seqprover.ReasonerFailure;
import org.eventb.internal.core.seqprover.eventbExtensions.ContradictionFinder.ContradictionInSetFinder;

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

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {
		// Do nothing
	}

	@Override
	public final Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final Set<Predicate> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() == 1) {
			// For backward compatibility with faulty version
			return new Input(neededHyps.iterator().next());
		}
		// Need to find the hypothesis which is contradicted by the others
		final ContradictionInSetFinder finder;
		finder = new ContradictionInSetFinder(neededHyps);
		final Predicate hyp = finder.getContrHyp();
		if (hyp != null) {
			return new Input(hyp);
		}
		throw new SerializeException(new IllegalStateException(
				"Unexpected set of needed hypothesis: " + neededHyps));
	}

	@Override
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

		final Map<Predicate, List<Predicate>> contrHyps = contradictingPredicates(
				hyp);

		final Set<Predicate> neededHyps = getContrHypotheses(seq, contrHyps);
		if (neededHyps == null) {
			return new ReasonerFailure(this, input, "Predicate " + hyp
					+ " is not contradicted by hypotheses");
		}
		neededHyps.add(hyp);

		final Predicate goal = null;
		final String display = "ct in hyps (" + hyp + ")";
		return ProverFactory.makeProofRule(this, input, goal, neededHyps,
				display, NO_ANTECEDENT);
	}

	/**
	 * Returns a map of (Predicate ↦ List of predicate) where Predicate is a
	 * sub-predicate contained in <code>pred</code> and the List of Predicate
	 * is a list of contradictory predicate of Predicate.
	 *
	 * Split a conjunctive hypothesis into its conjuncts and compute for each
	 * conjuncts a list of contradictory predicate.
	 *
	 * @param pred
	 * 			the predicate analyzed
	 */
	public static Map<Predicate, List<Predicate>> contradictingPredicates(
			Predicate pred) {
		Map<Predicate, List<Predicate>> preds = new HashMap<Predicate, List<Predicate>>();

		if (!isNeg(pred)) {
			final List<Predicate> genContrPredicate = getStrongerNegative(pred);
			preds.put(pred, genContrPredicate);
		} else {
			for (Predicate p : breakPossibleConjunct(makeNeg(pred))) {
				preds.put(p, getStrongerPositive(p));
			}
		}
		return preds;
	}


	/**
	 * Return a list of contradictory hypotheses
	 * @param seq
	 *            the current sequent
	 * @param contrHyps
	 *            the map (predicate ↦ contradictory predicate)
	 * @return a set of predicate
	 */
	private Set<Predicate> getContrHypotheses(IProverSequent seq,
			Map<Predicate, List<Predicate>> contrHyps) {
		Set<Predicate> preds = new HashSet<Predicate>();
		boolean contain;
		for (Predicate pred : contrHyps.keySet()) {
			contain = false;
			for (Predicate cntr: contrHyps.get(pred)) {
				if (seq.containsHypothesis(cntr)) {
					contain = true;
					preds.add(cntr);
				}
			}
			if (!contain)
				return null;
		}
		return preds;
	}

}
