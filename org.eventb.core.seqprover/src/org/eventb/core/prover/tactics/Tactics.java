package org.eventb.core.prover.tactics;

import java.util.Set;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.externalReasoners.Cut;
import org.eventb.core.prover.externalReasoners.DisjE;
import org.eventb.core.prover.externalReasoners.ExI;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.externalReasoners.Trivial;
import org.eventb.core.prover.rules.AllI;
import org.eventb.core.prover.rules.ConjI;
import org.eventb.core.prover.rules.Hyp;
import org.eventb.core.prover.rules.ImpI;
import org.eventb.core.prover.rules.MngHyp;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class Tactics {
	
	public static final Tactic conjI = new Tactic.RuleTac(new ConjI());

	public static final Tactic hyp = new Tactic.RuleTac(new Hyp());

	public static final Tactic allI = new Tactic.RuleTac(new AllI());

	public static final Tactic impI = new Tactic.RuleTac(new ImpI());

	public static final Tactic trivial = new Tactic.plugin(new Trivial(), null);

	public static final Tactic prune = new Tactic.prune();

	private static final IExternalReasoner cut = new Cut();
	private static final IExternalReasoner disjE = new DisjE();
	private static final IExternalReasoner exI = new ExI();
	
	public static Tactic onAllPending(Tactic t){
		return new Tactic.onAllPending(t);
	}
	
	public static Tactic onPending(int subgoalNo,Tactic t){
		return new Tactic.onPending(subgoalNo,t);
	}
	
	public static Tactic repeat(Tactic t){
		return new Tactic.repeat(t);
	}

	public static Tactic compose(Tactic ... tactics){
		return new Tactic.compose(tactics);
	}
	
	public static Tactic composeStrict(Tactic ... tactics){
		return new Tactic.composeStrict(tactics);
	}
	
	public static Tactic norm(){
		Tactic Ti = repeat(compose(conjI,impI,allI));
		Tactic T = repeat(compose(hyp,trivial,Ti));
		return repeat(onAllPending(T));
	}
	
	public static Tactic auto(){
		return norm();
	}
	
	public static Tactic plugin(IExternalReasoner plugin,IExtReasonerInput pluginInput){
		return new Tactic.plugin(plugin,pluginInput);
	}
	
//	public static Tactic conjE_auto(){
//		Tactic Tp = repeat(onAllPending((new Tactic.conjE_auto())));
//		return compose(Tp,norm());
//	}
	
	public static Tactic doCase(String kase){	
		String lemma = "("+ kase +") ∨¬("+ kase +")";
		return composeStrict(
				plugin(cut,new Cut.Input(lemma)),
				onPending(0,conjI),
				onPending(2,plugin(disjE,null)),
				norm());
	}
	
	public static Tactic provideWitness(String... witnesses){
		return plugin(exI,new ExI.Input(witnesses));
	}
	
	public static Tactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
		return new Tactic.RuleTac(new MngHyp(new HypothesesManagement.Action(type,hypotheses)));
		
	}
	
	public static Tactic legacyProvers(){
		return plugin(new LegacyProvers(),null);
	}
	
}
