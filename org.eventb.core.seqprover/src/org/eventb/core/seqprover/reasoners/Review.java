/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasoners;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class Review implements IReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".review";
	
	public static class Input implements IReasonerInput, ITranslatableReasonerInput {

		Set<Predicate> hyps;
		Predicate goal;
		int confidence;

		// TODO add check on confidence parameter
		public Input(IProverSequent sequent, int confidence) {
			this.hyps = ProverLib.collectPreds(sequent.selectedHypIterable());
			this.goal = sequent.goal();
			this.confidence = confidence;
		}
		
		public Input(Set<Predicate> hyps, Predicate goal, int confidence) {
			this.hyps = hyps;
			this.goal = goal;
			this.confidence = confidence;
		}

		@Override
		public void applyHints(ReplayHints hints) {
			Predicate[] newPreds = new Predicate[hyps.size()];
			int i = 0;
			for (Predicate hyp: hyps) {
				newPreds[i++] = hints.applyHints(hyp);
			}
			hyps = new LinkedHashSet<Predicate>(Arrays.asList(newPreds));
			goal = hints.applyHints(goal);
		}

		@Override
		public String getError() {
			return null;
		}

		@Override
		public boolean hasError() {
			return false;
		}
		
		@Override
		public Input translate(FormulaFactory factory) {
			final Set<Predicate> trHyps = new LinkedHashSet<Predicate>(hyps.size());
			for (Predicate hyp : hyps) {
				trHyps.add(hyp.translate(factory));
			}
			final Predicate trGoal = goal.translate(factory);
			return new Input(trHyps, trGoal, confidence);
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			for (Predicate hyp : hyps) {
				typeEnv.addAll(hyp.getFreeIdentifiers());
			}
			typeEnv.addAll(goal.getFreeIdentifiers());
			return typeEnv;
		}

	}
	
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) {
		// Nothing to serialize, all is in the rule.
	}
	
	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		
		return new Input(
				reader.getNeededHyps(),
				reader.getGoal(),
				reader.getConfidence()
		);
	}
	
	@Override
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {
	
		// Organize Input
		Input input = (Input) reasonerInput;
		
		Set<Predicate> hyps = input.hyps;
		Predicate goal = input.goal;
		int reviewerConfidence = input.confidence;
		
		if ((! (seq.goal().equals(goal))) ||
		   (! (seq.containsHypotheses(hyps)))) {
			return ProverFactory.reasonerFailure(this, input,
					"Reviewed sequent does not match");
		}
		
		assert reviewerConfidence > 0;
		assert reviewerConfidence <= IConfidence.REVIEWED_MAX;
	
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				hyps,
				reviewerConfidence,
				"rv ("+reviewerConfidence+") (" + seq.goal().toString()+")",
				new IAntecedent[0]);		
		
		return reasonerOutput;
	}
	
}
