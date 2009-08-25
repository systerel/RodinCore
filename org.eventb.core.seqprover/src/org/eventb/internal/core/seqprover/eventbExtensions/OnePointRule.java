/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.eventbExtensions.Lib.ff;

import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class OnePointRule implements IReasoner {

	public static class Input implements IReasonerInput {

		Predicate pred;

		/**
		 * The parameter is the hypothesis to rewrite. If <code>null</code>,
		 * the rewriting will be applied to the goal.
		 * 
		 * @param pred
		 *            hypothesis to rewrite or <code>null</code>
		 */
		public Input(Predicate pred) {
			this.pred = pred;
		}

		public void applyHints(ReplayHints renaming) {
			if (pred != null)
				pred = renaming.applyHints(pred);
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public Predicate getPred() {
			return pred;
		}

	}

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".onePointRule";

	public String getReasonerID() {
		return REASONER_ID;
	}

	public static boolean isApplicable(Formula<?> formula) {
		if (!(formula instanceof QuantifiedPredicate)) {
			return false;
		}
		final OnePointSimplifier matcher = new OnePointSimplifier(
				(QuantifiedPredicate) formula, ff);
		matcher.match();
		return matcher.isApplicable();
	}

	@ProverRule( { "ONE_POINT_L", "ONE_POINT_R" })
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final Input pInput = (Input) input;
		final Predicate pred = pInput.getPred();

		final boolean isGoal = pred == null;
		final Predicate applyTo = isGoal ? seq.goal() : pred;
		IAntecedent[] antecedents = getAntecedents(applyTo, isGoal);
		if (antecedents == null) {
			return ProverFactory.reasonerFailure(this, pInput, "Inference "
					+ getReasonerID() + " is not applicable for "
					+ applyTo);
		}
		if (isGoal) {
			// Generate the successful reasoner output
			return ProverFactory.makeProofRule(this, input, seq.goal(),
					getDisplayName(pred), antecedents);
		} else {
			return ProverFactory.makeProofRule(this, input, null, pred,
					getDisplayName(pred), antecedents);
		}
	}

	private String getDisplayName(Predicate pred) {
		return "One Point Rule in " + (pred == null ? "goal" : pred);
	}

	private IAntecedent[] getAntecedents(Predicate pred, boolean isGoal) {

		final OnePointSimplifier onePoint = new OnePointSimplifier(pred, ff);
		onePoint.match();

		if (!onePoint.isApplicable()) {
			return null;
		}
		
		final Predicate simplified = onePoint.getSimplifiedPredicate();

		final Expression replacement = onePoint.getReplacement();
		final Predicate replacementWD = Lib.WD(replacement);

		// There will be 2 antecedents
		IAntecedent[] antecedents = new IAntecedent[2];

		if (isGoal) {
			antecedents[0] = ProverFactory.makeAntecedent(simplified);
		} else {
			antecedents[0] = ProverFactory.makeAntecedent(null, null, null,
					asList(makeForwardInfHypAction(singleton(pred),
							singleton(simplified)),
							makeHideHypAction(singleton(pred))));
		}

		antecedents[1] = ProverFactory.makeAntecedent(replacementWD);

		return antecedents;
	}

	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		// Nothing to serialise, the predicate is contained inside the rule
	}

	public Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal simplification
			return new Input(null);
		}
		// Hypothesis simplification
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new Input(pred);
	}


}
