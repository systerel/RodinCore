/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *     Systerel - deselect WD predicate only if not already selected
 *     Systerel - refactored duplicated code with AllmpD
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
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
	private static final String display = "∀ hyp"; 
	
	
	public static class Input implements IReasonerInput {

		private static final String EXPRS_KEY = "exprs";
		
		MultipleExprInput exprsInput;
		Predicate pred;
		String error;

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
	
	protected String getDisplayedRuleName() {
		return display;
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
		final Input input = (Input) reasonerInput;

		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		final Predicate univHyp = input.pred;
		
		if (! seq.containsHypothesis(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHyp))
			return ProverFactory.reasonerFailure(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		
		final BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHyp);
		
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		final Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
					this,
					reasonerInput,
					"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;
		final String failureCheck = checkInstantiations(instantiations,
				boundIdentDecls);
		if (failureCheck != null) {
			return ProverFactory.reasonerFailure(this, input, failureCheck);
		}
		
		// Generate the well definedness predicate for the instantiations
		final FormulaFactory factory = seq.getFormulaFactory();
		final Predicate WDpred = DLib.WD(factory, instantiations);
		final Set<Predicate> WDpreds = Lib.breakPossibleConjunct(WDpred);
		DLib.removeTrue(factory, WDpreds);

		// Generate the instantiated predicate
		final Predicate instantiatedPred = 
			DLib.instantiateBoundIdents(univHyp, instantiations);
		assert instantiatedPred != null;

		// Generate the antecedents
		final IAntecedent[] antecedents = getAntecedents(factory, WDpreds, univHyp,
				instantiatedPred);

		// Generate the successful reasoner output
		final IProofRule reasonerOutput = ProverFactory.makeProofRule(this,
				input, null, univHyp, getDisplay(instantiations), antecedents);
		return reasonerOutput;
	}
	
	/**
	 * Generates the antecedents for the rule to make.
	 * 
	 * @param ff
	 *            the formula factory
	 * @param WDpreds
	 *            the well definedness predicates for the instantiations.
	 * @param univHyp
	 *            the universal input predicate
	 * @param instantiatedPred
	 *            the universal predicate after instantiation
	 * @return the antecedent of the rule to make
	 */
	protected IAntecedent[] getAntecedents(FormulaFactory ff, Set<Predicate> WDpreds,
			Predicate univHyp, Predicate instantiatedPred) {

		final IAntecedent[] antecedents = new IAntecedent[2];

		// First antecedent : Well Definedness condition
		antecedents[0] = ProverFactory.makeAntecedent(DLib.makeConj(ff, WDpreds));

		// The instantiated goal
		final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(WDpreds);
		addedHyps.addAll(Lib.breakPossibleConjunct(instantiatedPred));

		antecedents[1] = ProverFactory
				.makeAntecedent(
						null,
						addedHyps,
						WDpreds,
						Lib.NO_FREE_IDENT,
						Collections
								.<IHypAction> singletonList(getDeselectAction(univHyp)));
		return antecedents;
	}
	
	protected ISelectionHypAction getDeselectAction(Predicate univHyp) {
		return ProverFactory.makeDeselectHypAction(singleton(univHyp));
	}
	
	protected String checkInstantiations(Expression[] expressions,
			BoundIdentDecl[] boundIdentDecls) {
		return null;
	}

	private String getDisplay(Expression[] instantiations) {
		final StringBuilder str = new StringBuilder();
		str.append(getDisplayedRuleName());
		str.append(" (inst ");
		appendInstantiations(str, instantiations);
		str.append(")");
		return str.toString();
	}
	
	private void appendInstantiations(StringBuilder str,
			Expression[] instantiations) {
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null)
				str.append("_");
			else
				str.append(instantiations[i].toString());
			if (i != instantiations.length - 1)
				str.append(",");
		}
	}

}
