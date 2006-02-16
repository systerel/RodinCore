package org.eventb.core.prover.externalReasoners;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.proofs.Proof;
import org.eventb.core.prover.proofs.TrustedProof;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.ISequent;
import org.eventb.core.prover.sequent.SimpleSequent;


public class Eq implements IExternalReasoner{
	
	public String name(){
		return "ApplyEquality";
	}
	
//	public boolean isApplicable(ProverSequent S,PluginInput I) {
//		return true;
//	}

	public IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I) {
		if (! (I instanceof Input)) throw (new AssertionError(this));
		Input aI = (Input) I;
		
		Hypothesis eqHyp = aI.eqHyp;
		Predicate eqHypPred = eqHyp.getPredicate();
		if (! S.hypotheses().contains(eqHyp))
			return new UnSuccessfulExtReasonerOutput(this,I,"Nonexistent hypothesis:"+eqHyp);
		if (! Lib.isEq(eqHypPred))
			return new UnSuccessfulExtReasonerOutput(this,I,"Hypothesis not an equality:"+eqHyp);
		
		Expression from;
		Expression to;
		if (aI.useReflexive) 
		{
			from = Lib.eqRight(eqHypPred);
			to = Lib.eqLeft(eqHypPred);
		}
		else 
		{ 
			from = Lib.eqLeft(eqHypPred);
			to = Lib.eqRight(eqHypPred);
		}
		
		// TODO remove when full equality possible.
		if (!(from instanceof FreeIdentifier)) 
			return new UnSuccessfulExtReasonerOutput(this,I,"Identifier expected:"+from);
		
		Set<Hypothesis> toDeselect = Hypothesis.Hypotheses();
		toDeselect.add(eqHyp);
		Set<Predicate> rewrittenHyps = new HashSet<Predicate>();
		for (Hypothesis shyp : S.selectedHypotheses()){
			if (! shyp.equals(eqHyp)){
				Predicate rewritten = (Lib.rewrite(shyp.getPredicate(),(FreeIdentifier)from,to));
				if (! rewritten.equals(shyp.getPredicate())){
					toDeselect.add(shyp);
					rewrittenHyps.add(rewritten);
				}
			}
		}
		ITypeEnvironment te = S.typeEnvironment();
		Predicate rewrittenGoal = Lib.rewrite(S.goal(),(FreeIdentifier)from,to);
		Predicate newGoal = Lib.makeGoal(te,rewrittenHyps,rewrittenGoal);
		Predicate seqGoal = Lib.makeImp(te,newGoal,S.goal());
		ISequent outputSequent = new SimpleSequent(S.typeEnvironment(),toDeselect,seqGoal);
		Proof outputProof = new TrustedProof(outputSequent);
		return new SuccessfullExtReasonerOutput(this,I,outputProof,Lib.deselect(toDeselect));
	}
	

//	public PluginInput defaultInput(){
//		return null;
//	}
	
	
	public static class Input implements IExtReasonerInput{

		public final Hypothesis eqHyp;
		public final boolean useReflexive;
		
		public Input(Hypothesis eqHyp){
			assert Lib.isEq(eqHyp.getPredicate());
			assert (Lib.eqLeft(eqHyp.getPredicate()) instanceof FreeIdentifier);
			this.eqHyp = eqHyp;
			this.useReflexive = false;
		}
		
		public Input(Hypothesis eqHyp,boolean useReflexive){
			assert Lib.isEq(eqHyp.getPredicate());
			assert useReflexive ? (Lib.eqRight(eqHyp.getPredicate()) instanceof FreeIdentifier)
					: (Lib.eqLeft(eqHyp.getPredicate()) instanceof FreeIdentifier);
			this.eqHyp = eqHyp;
			this.useReflexive = useReflexive;
		}
	}

}
