/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     UPEC - added optional input names for fresh idents
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.seqprover.ProverFactory.makeRewriteHypAction;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.NO_NAMES;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.checkNamesValidity;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.splitInput;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiation;

/**
 * Reasoner that returns a forward inference to free existentially bound
 * variables in a hypothesis.
 * 
 * @author Farhad Mehta
 */
public class ExF extends ForwardInfReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exF";

	/**
	 * An input adding an optional list of names to the super-class input.
	 *
	 * @author Guillaume Verdier
	 */
	public static class Input extends ForwardInfReasoner.Input {

		private String[] names = NO_NAMES;
		private String error;

		/**
		 * Constructs an input with no user-provided names.
		 *
		 * @param pred hypothesis to use for the inference
		 */
		public Input(Predicate pred) {
			super(pred);
		}

		/**
		 * Constructs an input with user-provided names.
		 *
		 * The input should be a comma-separated list of names. It may be empty.
		 *
		 * The names will be checked to be valid identifiers in the provided formula
		 * factory.
		 *
		 * @param pred  hypothesis to use for the inference
		 * @param input comma-separated list of names
		 * @param ff    formula factory to use to check names validity
		 */
		public Input(Predicate pred, String input, FormulaFactory ff) {
			super(pred);
			String[] inputs = splitInput(input);
			error = checkNamesValidity(inputs, ff);
			if (error == null) {
				names = inputs;
			}
		}

		// Private constructor: the caller has to provide valid names
		private Input(Predicate pred, String[] names) {
			super(pred);
			this.names = names;
		}

		public String[] getNames() {
			return names;
		}

		@Override
		public boolean hasError() {
			return super.hasError() || error != null;
		}

		@Override
		public String getError() {
			String superError = super.getError();
			if (superError != null) {
				return superError;
			}
			return error;
		}

		@Override
		public Input translate(FormulaFactory factory) {
			var result = (ForwardInfReasoner.Input) super.translate(factory);
			return new Input(result.getPred(), names);
		}

	}

	private Input input;

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∃ hyp (" + pred + ")";
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput rInput,
			IProofMonitor pm) {
		if (rInput.hasError()) {
			return reasonerFailure(this, rInput, rInput.getError());
		}
		try {
			input = (Input) rInput;
			return super.apply(seq, rInput, pm);
		} finally {
			// Make sure that we do not keep a reference to an old input
			input = null;
		}
	}

	@ProverRule("XST_L")
	@Override
	protected IRewriteHypAction getRewriteAction(IProverSequent sequent,
			Predicate pred) {
		if (pred.getTag() != EXISTS) {
			throw new IllegalArgumentException("Predicate is not existentially quantified: " + pred);
		}
		final QuantifiedPredicate exQ = (QuantifiedPredicate) pred;
		final ISealedTypeEnvironment typenv = sequent.typeEnvironment();
		final FreshInstantiation inst = new FreshInstantiation(exQ, typenv, input.getNames());
		final FreeIdentifier[] freshIdents = inst.getFreshIdentifiers();
		final Set<Predicate> inferredHyps = breakPossibleConjunct(inst.getResult());
		final Set<Predicate> neededHyp = singleton(pred);
		return makeRewriteHypAction(neededHyp, freshIdents, inferredHyps, neededHyp);
	}

	@Override
	public Input deserializeInput(IReasonerInputReader reader) throws SerializeException {
		var result = super.deserializeInput(reader);
		// Based on the super deserializeInput(), we know that there is a single
		// antecedent which first hyp action is a ForwardInfHypAction
		var inf = (IForwardInfHypAction) reader.getAntecedents()[0].getHypActions().get(0);
		String[] names = stream(inf.getAddedFreeIdents()).map(FreeIdentifier::getName).toArray(String[]::new);
		// We know the names are valid: they were parsed by the formula factory
		return new Input(result.getPred(), names);
	}

}
