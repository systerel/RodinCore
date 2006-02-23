package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.composeStrict;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.onPending;
import static org.eventb.core.prover.tactics.BasicTactics.pluginTac;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;

import java.util.Set;

import org.eventb.core.ast.Predicate;

import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.AllF;
import org.eventb.core.prover.externalReasoners.ConjD;
import org.eventb.core.prover.externalReasoners.Contr;
import org.eventb.core.prover.externalReasoners.Cut;
import org.eventb.core.prover.externalReasoners.DisjE;
import org.eventb.core.prover.externalReasoners.Eq;
import org.eventb.core.prover.externalReasoners.ExF;
import org.eventb.core.prover.externalReasoners.ExI;
import org.eventb.core.prover.externalReasoners.ImpD;
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
	
	public static ITactic legacyProvers() {
		final LegacyProvers.Input input = new LegacyProvers.Input();
		return pluginTac(new LegacyProvers(), input);
	}
	
	public static ITactic legacyProvers(long timeOutDelay) {
		final LegacyProvers.Input input = new LegacyProvers.Input(timeOutDelay);
		return pluginTac(new LegacyProvers(), input);
	}
	
	public static ITactic lemma(String strLemma){
		return composeStrict(
				pluginTac(new Cut(),new Cut.Input(strLemma)),
				onPending(0,conjI()),
				onPending(2,impI())
				);
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
				onPending(2,norm())
		);
	}
	
	public static ITactic contradictGoal(){
		return composeStrict(
				pluginTac(new Contr(),new Contr.Input()),
				onPending(0,impI())
		);
	}
	
	public static ITactic searchHyps(String str){
		return BasicTactics.searchTac(str);
	}
	
	
	

	// Tactics applicable on the goal
	
	public static ITactic impI() {
		return new ITactic.RuleTac(new ImpI());
	}
	
	public static boolean impI_applicable(Predicate goal){
		return Lib.isImp(goal);
	}
	
	public static ITactic conjI() {
		return new ITactic.RuleTac(new ConjI());
	}
	
	public static boolean conjI_applicable(Predicate goal){
		return Lib.isConj(goal);
	}
	
	public static ITactic allI() {
		return new ITactic.RuleTac(new AllI());
	}
	
	public static boolean allI_applicable(Predicate goal){
		return Lib.isUnivQuant(goal);
	}
	
	public static ITactic exI(String... witnesses){
		return composeStrict(
				pluginTac(new ExI(),new ExI.Input(witnesses)),
						onPending(0,conjI())
				);
	}
	
	public static boolean exI_applicable(Predicate goal){
		return Lib.isExQuant(goal);
	}
	
	
	// Tactics applicable on a hypothesis
	
	public static ITactic allF(Hypothesis univHyp, String... instantiations){
		return composeStrict(
				pluginTac(new AllF(),new AllF.Input(instantiations,univHyp)),
				onPending(0,conjI()),
				onPending(1,impI())
		);
	}
	
	public static boolean allF_applicable(Hypothesis hyp){
		return Lib.isUnivQuant(hyp.getPredicate());
	}
	
	public static ITactic conjD(Hypothesis conjHyp){
		return composeStrict(
				pluginTac(new ConjD(),new ConjD.Input(conjHyp)),
				onPending(0,impI())
		);
	}
	
	public static boolean conjD_applicable(Hypothesis hyp){
		return Lib.isConj(hyp.getPredicate());
	}
	
	public static ITactic impD(Hypothesis impHyp, boolean useContrapositive){
		return composeStrict(
				pluginTac(new ImpD(),new ImpD.Input(impHyp,useContrapositive)),
				onPending(0,conjI()),
				onPending(1,impI())
		);
	}
	
	public static boolean impD_applicable(Hypothesis hyp){
		return Lib.isImp(hyp.getPredicate());
	}
	
	public static ITactic disjE(Hypothesis disjHyp){
		return composeStrict(
				pluginTac(new DisjE(),new DisjE.Input(disjHyp)),
				onPending(0,conjI()),
				onAllPending(impI())
		);
	}
	
	public static boolean disjE_applicable(Hypothesis hyp){
		return Lib.isDisj(hyp.getPredicate());
	}
	
	public static ITactic eqE(Hypothesis eqHyp,boolean useReflexive){
		return composeStrict(
				pluginTac(new Eq(),new Eq.Input(eqHyp,useReflexive)),
				onPending(0,impI())
		);
	}
	
	public static boolean eqE_applicable(Hypothesis hyp){
		return Lib.isEq(hyp.getPredicate());
	}
	
	public static ITactic exF(Hypothesis exHyp){
		return composeStrict(
				pluginTac(new ExF(),new ExF.Input(exHyp)),
				onPending(0,allI()),
				onPending(0,impI())
		);
	}
	
	public static boolean exF_applicable(Hypothesis hyp){
		return Lib.isExQuant(hyp.getPredicate());
	}
	
	
    // Tactics applicable on every hypothesis
	
	public static ITactic falsifyHyp(Hypothesis hyp){
		return composeStrict(
				pluginTac(new Contr(),new Contr.Input(hyp)),
				onPending(0,impI())
		);
	}
	
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
