/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IRepairableInputReasoner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * AdversarialReasoner reasoner and tactic that can fail badly (e.g. throw any
 * exception). This class is used to ensure that the sequent prover is resilient
 * to errors in contributed extensions.
 * 
 * @author Laurent Voisin
 */
public class AdversarialReasoner implements IReasoner, IRepairableInputReasoner {

	public static final String REASONER_ID = "org.eventb.core.tests.adversarial";
	private static final IAntecedent[] NO_ANTE = new IAntecedent[0];

	public static boolean erroneousSerializeInput = false;
	public static boolean erroneousDeserializeInput = false;
	public static boolean erroneousApply = false;
	public static boolean erroneousRepairInput = false;

	public static void reset() {
		erroneousSerializeInput = false;
		erroneousDeserializeInput = false;
		erroneousApply = false;
		erroneousRepairInput = false;
	}

	static void maybeFail(boolean condition) {
		if (condition) {
			throw new RuntimeException("Error in adversarial reasoner input");
		}
	}

	public static class Tactic implements ITactic {

		public static boolean erroneous = false;

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			maybeFail(erroneous);
			return new AdversarialReasoner().asTactic().apply(ptNode, pm);
		}

	}

	public static class Input implements IReasonerInput {

		public static boolean erroneousHasError = false;
		public static boolean erroneousGetError = false;
		public static boolean erroneousApplyHints = false;

		public static void reset() {
			erroneousHasError = false;
			erroneousGetError = false;
			erroneousApplyHints = false;
		}

		@Override
		public boolean hasError() {
			maybeFail(erroneousHasError);
			return false;
		}

		@Override
		public String getError() {
			maybeFail(erroneousGetError);
			return null;
		}

		@Override
		public void applyHints(ReplayHints renaming) {
			maybeFail(erroneousApplyHints);
		}

	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		maybeFail(erroneousSerializeInput);
		// nothing to do
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		maybeFail(erroneousDeserializeInput);
		if (erroneousRepairInput) {
			// Force running the repair method
			final Exception e = new RuntimeException(
					"Error in adversarial reasoner input");
			throw new SerializeException(e);
		}
		return new Input();
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		maybeFail(erroneousApply);
		final Predicate goal = seq.goal();
		if (goal.getTag() != BTRUE) {
			return reasonerFailure(this, input, "Goal is not a tautology");
		}
		// needing all hypotheses
		final Set<Predicate> neededHyps = new LinkedHashSet<Predicate>();
		for (Predicate hyp : seq.hypIterable()) {
			neededHyps.add(hyp);
		}
		return makeProofRule(this, input, goal, neededHyps, "adversarial", NO_ANTE);
	}

	public ITactic asTactic() {
		return BasicTactics.reasonerTac(this, new Input());
	}

	@Override
	public IReasonerInput repair(IReasonerInputReader reader) {
		maybeFail(erroneousRepairInput);
		return new Input();
	}

}
