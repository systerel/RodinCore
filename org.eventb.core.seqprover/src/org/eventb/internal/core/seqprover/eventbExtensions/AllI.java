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

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Generates the introduntion rule for universal quantification.
 * 
 * <p>
 * This reasoner frees all universally quantified variables in a goal.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class AllI extends EmptyInputReasoner{
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("ALL_R")
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProofMonitor pm){
		
		if (! Lib.isUnivQuant(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,input,"Goal is not universally quantified");
		
		QuantifiedPredicate UnivQ = (QuantifiedPredicate)seq.goal();
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(UnivQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		ITypeEnvironmentBuilder newITypeEnvironment = seq.typeEnvironment()
				.makeBuilder();
		final FormulaFactory ff = seq.getFormulaFactory();
		FreeIdentifier[] freeIdents = newITypeEnvironment.makeFreshIdentifiers(boundIdentDecls);		
		assert boundIdentDecls.length == freeIdents.length;
		
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				UnivQ.instantiate(freeIdents,ff),
				null,
				freeIdents,
				null);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∀ goal (frees "+displayFreeIdents(freeIdents)+")",
				anticidents
				);
				
		return reasonerOutput;
	}
	
	private String displayFreeIdents(FreeIdentifier[] freeIdents) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < freeIdents.length; i++) {
				str.append(freeIdents[i].toString());
			if (i != freeIdents.length-1) str.append(",");
		}
		return str.toString();
	}

}
