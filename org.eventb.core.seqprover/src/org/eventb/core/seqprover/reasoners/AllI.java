package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class AllI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProgressMonitor progressMonitor){
		
		if (! Lib.isUnivQuant(seq.goal()))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Goal is not universally quantified";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		
		QuantifiedPredicate UnivQ = (QuantifiedPredicate)seq.goal();
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(UnivQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		ITypeEnvironment newITypeEnvironment = seq.typeEnvironment().clone();
		FreeIdentifier[] freeIdents = (Lib.ff).makeFreshIdentifiers(boundIdentDecls,newITypeEnvironment);		
		assert boundIdentDecls.length == freeIdents.length;
		reasonerOutput.display = "∀ goal (frees "+displayFreeIdents(freeIdents)+")";
		
		reasonerOutput.anticidents[0].subGoal = UnivQ.instantiate(freeIdents,Lib.ff);
		reasonerOutput.anticidents[0].addedFreeIdentifiers = freeIdents;
		assert reasonerOutput.anticidents[0].subGoal.isTypeChecked();
				
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
