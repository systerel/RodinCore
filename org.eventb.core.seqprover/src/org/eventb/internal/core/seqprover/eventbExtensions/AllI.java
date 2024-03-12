/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
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
import static java.util.stream.Collectors.joining;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.NO_NAMES;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.checkNamesValidity;
import static org.eventb.core.seqprover.reasonerInputs.OptionalNamesInputHelper.splitInput;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiation;

/**
 * Generates the introduction rule for universal quantification.
 * 
 * <p>
 * This reasoner frees all universally quantified variables in a goal.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 */
public class AllI implements IReasoner {

	/**
	 * An input containing an optional list of names.
	 *
	 * @author Guillaume Verdier
	 */
	public static class Input implements IReasonerInput {

		private String[] names = NO_NAMES;
		private String error;

		/**
		 * Constructs an empty input with no names.
		 */
		public Input() {
			// Nothing to do: leave attributes to their default value
		}

		/**
		 * Constructs an input with names extracted from a string.
		 *
		 * The input should be a comma-separated list of names. It may be empty.
		 *
		 * The names will be checked to be valid identifiers in the provided formula
		 * factory.
		 *
		 * @param input comma-separated list of names
		 * @param ff    formula factory to use to check names validity
		 */
		public Input(String input, FormulaFactory ff) {
			String[] inputs = splitInput(input);
			error = checkNamesValidity(inputs, ff);
			if (error == null) {
				names = inputs;
			}
		}

		/**
		 * Deserializes names from an input reader.
		 *
		 * The list of names will be obtained from the added free identifiers of the
		 * only antecedent.
		 *
		 * @param reader input reader to use
		 */
		public Input(IReasonerInputReader reader) {
			IAntecedent[] antecedents = reader.getAntecedents();
			if (antecedents.length == 1) {
				names = stream(antecedents[0].getAddedFreeIdents()).map(FreeIdentifier::getName).toArray(String[]::new);
				// We know the names are valid: they were parsed by the formula factory
			}
		}

		@Override
		public boolean hasError() {
			return error != null;
		}

		@Override
		public String getError() {
			return error;
		}

		public String[] getNames() {
			return names;
		}

		@Override
		public void applyHints(ReplayHints renaming) {
			// Nothing to do
		}

	}

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	@ProverRule("ALL_R")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		if (input.hasError()) {
			return reasonerFailure(this, input, input.getError());
		}
		var namesInput = (Input) input;
		final Predicate goal = seq.goal();
		if (goal.getTag() != FORALL) {
			return reasonerFailure(this, input, "Goal is not universally quantified");
		}
		final QuantifiedPredicate univQ = (QuantifiedPredicate) goal;
		final ISealedTypeEnvironment typenv = seq.typeEnvironment();
		final FreshInstantiation inst = new FreshInstantiation(univQ, typenv, namesInput.getNames());
		final FreeIdentifier[] freshIdents = inst.getFreshIdentifiers();
		final IAntecedent[] antecedents = new IAntecedent[] {//
				makeAntecedent(inst.getResult(), null, freshIdents, null),//
		};
		final IProofRule reasonerOutput = makeProofRule(this, input, goal,
				"∀ goal (frees " + displayFreeIdents(freshIdents) + ")",
				antecedents);
		return reasonerOutput;
	}

	private String displayFreeIdents(FreeIdentifier[] freeIdents) {
		return stream(freeIdents).map(Object::toString).collect(joining(","));
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		// Nothing to do: names are saved as added free identifiers in the antecedent
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		return new Input(reader);
	}

}
