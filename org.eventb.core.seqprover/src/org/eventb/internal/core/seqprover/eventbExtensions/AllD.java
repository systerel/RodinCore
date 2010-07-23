/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
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
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

// TODO : maybe Rename to AllE or AllF
// TDOD : maybe return a fwd inference instead of a complete rule if WD is trivial
public class AllD implements IReasoner {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allD";
	
	
	public static class Input implements IReasonerInput {

		private static final String EXPRS_KEY = "exprs";
		
		MultipleExprInput exprsInput;
		Predicate pred;
		String error;

		/**
		 * @deprecated use other constructor without boundIdentDecls param instead
		 */
		@Deprecated
		public Input(String[] instantiations, BoundIdentDecl[] decls,
				ITypeEnvironment typeEnv, Predicate pred) {

			this.exprsInput = new MultipleExprInput(instantiations, decls,
					typeEnv);
			this.pred = pred;
			this.error = null;
		}


		public Input(Predicate pred, ITypeEnvironment typeEnv, String[] instantiations) {
			if (pred instanceof QuantifiedPredicate) {
				BoundIdentDecl[] decls = Lib.getBoundIdents(pred);
				this.exprsInput = new MultipleExprInput(instantiations, decls ,
						typeEnv);
				this.pred = pred;
				this.error = null;
			}
			else{
				this.error = "Predicate " + pred +" is not a quantified predicate.";
				this.pred = null;
				this.exprsInput = null;
			}
		}

		
		public Input(IReasonerInputReader reader, Predicate pred)
				throws SerializeException {

			this.exprsInput = new MultipleExprInput(reader, EXPRS_KEY);
			this.pred = pred;
		}

		public void serialize(IReasonerInputWriter writer) throws SerializeException {
			exprsInput.serialize(writer, EXPRS_KEY);
		}
		
		public void applyHints(ReplayHints hints) {
			exprsInput.applyHints(hints);
			pred = hints.applyHints(pred);
		}

		public String getError() {
			if (error != null) return error;
			return exprsInput.getError();
		}

		public boolean hasError() {
			return (error != null || exprsInput.hasError());
		}

		public Expression[] computeInstantiations(BoundIdentDecl[] boundIdentDecls) {
			return exprsInput.computeInstantiations(boundIdentDecls);
		}

	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer)
			throws SerializeException {
		((Input) rInput).serialize(writer);
		// The predicate is accessible from the associated rule
	}

	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		Set<Predicate> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp: neededHyps) {
			pred = hyp;
		}
		return new Input(reader, pred);
	}
	
	@ProverRule("FORALL_INST")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		// Organize Input
		Input input = (Input) reasonerInput;

		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		Predicate univHyp = input.pred;
		
		if (! seq.containsHypothesis(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHyp);
		
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
					this,
					reasonerInput,
					"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;
		
		
		// Generate the well definedness predicate for the instantiations
		final FormulaFactory factory = seq.getFormulaFactory();
		final DLib lib = mDLib(factory);
		final Predicate WDpred = lib.WD(instantiations);
		final Set<Predicate> WDpreds = Lib.breakPossibleConjunct(WDpred);
		lib.removeTrue(WDpreds);
		
		// Generate the instantiated predicate
		Predicate instantiatedPred = lib.instantiateBoundIdents(univHyp,
				instantiations);
	    assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[2];

		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(lib.makeConj(WDpreds));
		
		
		// The instantiated goal
		final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(WDpreds);
		addedHyps.addAll(Lib.breakPossibleConjunct(instantiatedPred));
		
		final Set<Predicate> toDeselect = new HashSet<Predicate>();
		toDeselect.add(univHyp);
		toDeselect.addAll(WDpreds);
		
		anticidents[1] = ProverFactory.makeAntecedent(
				null,
				addedHyps,
				ProverFactory.makeDeselectHypAction(toDeselect)
				);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				null,
				univHyp,
				"∀ hyp (inst "+displayInstantiations(instantiations)+")",
				anticidents
				);
		
		return reasonerOutput;
	}
	
	protected String displayInstantiations(Expression[] instantiations){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null)
				str.append("_");
			else
				str.append(instantiations[i].toString());
			if (i != instantiations.length-1) str.append(",");
		}
		return str.toString();
	}

}
