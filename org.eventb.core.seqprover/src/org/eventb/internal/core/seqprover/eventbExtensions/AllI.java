package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
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
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";
	
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
		ITypeEnvironment newITypeEnvironment = seq.typeEnvironment().clone();
		FreeIdentifier[] freeIdents = (Lib.ff).makeFreshIdentifiers(boundIdentDecls,newITypeEnvironment);		
		assert boundIdentDecls.length == freeIdents.length;
		
		IAntecedent[] anticidents = new IAntecedent[1];
		anticidents[0] = ProverFactory.makeAntecedent(
				UnivQ.instantiate(freeIdents,Lib.ff),
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
