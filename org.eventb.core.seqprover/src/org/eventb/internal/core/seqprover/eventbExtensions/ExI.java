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
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
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
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {

		MultipleExprInput input = (MultipleExprInput) rInput;
		input.serialize(writer, EXPRS_KEY);
	}
	
	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		return new MultipleExprInput(reader, EXPRS_KEY);
	}
	
	@Override
	@ProverRule("EXISTS_INST")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		final Predicate goal = seq.goal();
		if (! Lib.isExQuant(goal))
			return ProverFactory.reasonerFailure(
					this,reasonerInput,
					"Goal is not existentially quantified"); 
		
		// Organize Input
		MultipleExprInput input = (MultipleExprInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(goal);
		
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
		final FormulaFactory ff = seq.getFormulaFactory();
		final Predicate WDpred = DLib.WD(ff, instantiations);
		final Set<Predicate> WDpreds = Lib.breakPossibleConjunct(WDpred);
		DLib.removeTrue(ff, WDpreds);
		
		// Generate the instantiated predicate
		Predicate instantiatedPred = DLib.instantiateBoundIdents(goal,instantiations);
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
				goal,
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
