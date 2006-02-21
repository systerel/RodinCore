package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.composeStrict;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.onPending;
import static org.eventb.core.prover.tactics.BasicTactics.pluginTac;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;

import java.util.Set;

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
	
	// Globally applicable tactics
	
	public static ITactic legacyProvers(){
		return pluginTac(new LegacyProvers(),null);
	}
	
	public static ITactic lemma(String strLemma){
		return pluginTac(new Cut(),new Cut.Input(strLemma));
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(hyp(),trivial(),Ti));
		return repeat(onAllPending(T));
	}
	
	public static ITactic doCase(String kase){	
		String lemma = "("+ kase +") ∨¬("+ kase +")";
		return composeStrict(
				pluginTac(new Cut(),new Cut.Input(lemma)),
				onPending(0,conjI()),
				onPending(2,pluginTac(new DisjE(),null)),
				norm());
	}
	

	// Tactics applicable on the goal
	
	public static ITactic impI() {
		return new ITactic.RuleTac(new ImpI());
	}
	
	public static ITactic conjI() {
		return new ITactic.RuleTac(new ConjI());
	}
	
	public static ITactic allI() {
		return new ITactic.RuleTac(new AllI());
	}
	
	public static ITactic provideWitness(String... witnesses){
		return pluginTac(new ExI(),new ExI.Input(witnesses));
	}
	
	// Tactics applicable on a hypothesis
	
	// Misc tactics
	
	public static ITactic hyp() {
		return new ITactic.RuleTac(new Hyp());
	}
	
	public static ITactic trivial() {
		return new ITactic.plugin(new Trivial(), null);
	}
	
	public static ITactic prune() {
		return BasicTactics.prune();
	}
	
	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
	return new ITactic.RuleTac(new MngHyp(new HypothesesManagement.Action(type,hypotheses)));
	
	}
	
	
	
	
	
	
//	
//	
//	
//	public static final ITactic conjI = new ITactic.RuleTac(new ConjI());
//
//	public static final ITactic hyp = new ITactic.RuleTac(new Hyp());
//
//	public static final ITactic allI = new ITactic.RuleTac(new AllI());
//
//	public static final ITactic impI = new ITactic.RuleTac(new ImpI());
//
//	public static final ITactic trivial = new ITactic.plugin(new Trivial(), null);
//
//	public static final ITactic prune = new ITactic.prune();
//
//	private static final IExternalReasoner cut = new Cut();
//	private static final IExternalReasoner disjE = new DisjE();
//	private static final IExternalReasoner exI = new ExI();
//	
//	public static ITactic onAllPending(ITactic t){
//		return new ITactic.onAllPending(t);
//	}
//	
//	public static ITactic onPending(int subgoalNo,ITactic t){
//		return new ITactic.onPending(subgoalNo,t);
//	}
//	
//	public static ITactic repeat(ITactic t){
//		return new ITactic.repeat(t);
//	}
//
//	public static ITactic compose(ITactic ... tactics){
//		return new ITactic.compose(tactics);
//	}
//	
//	public static ITactic composeStrict(ITactic ... tactics){
//		return new ITactic.composeStrict(tactics);
//	}
//	
//	public static ITactic norm(){
//		ITactic Ti = repeat(compose(conjI,impI,allI));
//		ITactic T = repeat(compose(hyp,trivial,Ti));
//		return repeat(onAllPending(T));
//	}
//	
//	public static ITactic auto(){
//		return norm();
//	}
//	
//	public static ITactic plugin(IExternalReasoner plugin,IExtReasonerInput pluginInput){
//		return new ITactic.plugin(plugin,pluginInput);
//	}
//	
////	public static Tactic conjE_auto(){
////		Tactic Tp = repeat(onAllPending((new Tactic.conjE_auto())));
////		return compose(Tp,norm());
////	}
//	
//	public static ITactic doCase(String kase){	
//		String lemma = "("+ kase +") ∨¬("+ kase +")";
//		return composeStrict(
//				plugin(cut,new Cut.Input(lemma)),
//				onPending(0,conjI),
//				onPending(2,plugin(disjE,null)),
//				norm());
//	}
//	
//	public static ITactic provideWitness(String... witnesses){
//		return plugin(exI,new ExI.Input(witnesses));
//	}
//	
//	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
//		return new ITactic.RuleTac(new MngHyp(new HypothesesManagement.Action(type,hypotheses)));
//		
//	}
//	
//	public static ITactic legacyProvers(){
//		return plugin(new LegacyProvers(),null);
//	}	
	
}
