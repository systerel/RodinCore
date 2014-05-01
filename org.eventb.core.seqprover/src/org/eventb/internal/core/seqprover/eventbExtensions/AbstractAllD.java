/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactor class hierarchy
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;

import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
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
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

/**
 * Common implementation for the AllD, AllmpD and AllmtD reasoners.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractAllD implements IReasoner {

	public static class Input implements IReasonerInput, ITranslatableReasonerInput {
	
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
	
		// constructor used for translation
		private Input(Predicate pred, MultipleExprInput exprsInput) {
			this.pred = pred;
			this.exprsInput = exprsInput;
			this.error = null;
		}
	
		public Input(IReasonerInputReader reader, Predicate pred)
				throws SerializeException {
	
			this.exprsInput = new MultipleExprInput(reader, EXPRS_KEY);
			this.pred = pred;
		}
	
		public void serialize(IReasonerInputWriter writer) throws SerializeException {
			exprsInput.serialize(writer, EXPRS_KEY);
		}
		
		@Override
		public void applyHints(ReplayHints hints) {
			exprsInput.applyHints(hints);
			pred = hints.applyHints(pred);
		}
	
		@Override
		public String getError() {
			if (error != null) return error;
			return exprsInput.getError();
		}
	
		@Override
		public boolean hasError() {
			return (error != null || exprsInput.hasError());
		}
	
		public Expression[] computeInstantiations(BoundIdentDecl[] boundIdentDecls) {
			return exprsInput.computeInstantiations(boundIdentDecls);
		}
	
		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			final MultipleExprInput trExprsInput = (MultipleExprInput) exprsInput
					.translate(factory);
			final Predicate trPred = pred == null ? null : pred
					.translate(factory);
			return new Input(trPred, trExprsInput);
		}
	
		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory.makeTypeEnvironment();
			typeEnv.addAll(exprsInput.getTypeEnvironment(factory));
			if (pred != null) {
				typeEnv.addAll(pred.getFreeIdentifiers());
			}
			return typeEnv;
		}
	
	}

	@Override
	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer)
			throws SerializeException {
				((Input) rInput).serialize(writer);
				// The predicate is accessible from the associated rule
			}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
	
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

	@Override
	@ProverRule("FORALL_INST")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm) {
	
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

	protected abstract String checkInstantiations(Expression[] instantiations,
			BoundIdentDecl[] boundIdentDecls);

	protected abstract IAntecedent[] getAntecedents(FormulaFactory factory,
			Set<Predicate> wDpreds, Predicate univHyp,
			Predicate instantiatedPred);

	private String getDisplay(Expression[] instantiations) {
		final StringBuilder str = new StringBuilder();
		str.append(getDisplayedRuleName());
		str.append(" (inst ");
		appendInstantiations(str, instantiations);
		str.append(")");
		return str.toString();
	}
	
	protected abstract String getDisplayedRuleName();

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

	protected ISelectionHypAction getDeselectAction(Predicate univHyp) {
		return ProverFactory.makeDeselectHypAction(singleton(univHyp));
	}

}