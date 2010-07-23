/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
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
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

public class ExI implements IReasoner {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exI";
	
	private static final String EXPRS_KEY = "exprs"; 
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {

		MultipleExprInput input = (MultipleExprInput) rInput;
		input.serialize(writer, EXPRS_KEY);
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		return new MultipleExprInput(reader, EXPRS_KEY);
	}
	
	@ProverRule("EXISTS_INST")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		if (! Lib.isExQuant(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,reasonerInput,
					"Goal is not existentially quantified"); 
		
		// Organize Input
		MultipleExprInput input = (MultipleExprInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(seq.goal());
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
				this,reasonerInput,
				"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;	
		
		
		// Generate the well definedness predicate for the witnesses
		final DLib lib = DLib.mDLib(seq.getFormulaFactory());
		final Predicate WDpred = lib.WD(instantiations);
		final Set<Predicate> WDpreds = Lib.breakPossibleConjunct(WDpred);
		lib.removeTrue(WDpreds);
		
		// Generate the instantiated predicate
		Predicate instantiatedPred = lib.instantiateBoundIdents(seq.goal(),instantiations);
		assert instantiatedPred != null;
		
		// Generate the anticidents
		IAntecedent[] anticidents = new IAntecedent[2];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAntecedent(WDpred);
		
		// The instantiated goal
		// Replaced, adding WD predicate into hyps
		// anticidents[1] = ProverFactory.makeAntecedent(instantiatedPred);
		anticidents[1] = ProverFactory.makeAntecedent(
				instantiatedPred, 
				WDpreds, 
				null);
		
		// Generate the successful reasoner output
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∃ goal (inst "+displayInstantiations(instantiations)+")",
				anticidents);
				
		return reasonerOutput;
	}
	
	private String displayInstantiations(Expression[] instantiations){
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
