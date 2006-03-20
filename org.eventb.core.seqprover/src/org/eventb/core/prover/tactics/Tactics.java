package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.composeStrict;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.onPending;
import static org.eventb.core.prover.tactics.BasicTactics.pluginTac;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;
import static org.eventb.core.prover.tactics.BasicTactics.encapsulate;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eventb.core.prover.externalReasoners.ExternalML;
import org.eventb.core.prover.externalReasoners.ExternalPP;
import org.eventb.core.prover.externalReasoners.ImpD;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.externalReasoners.RewriteGoal;
import org.eventb.core.prover.externalReasoners.RewriteHyp;
import org.eventb.core.prover.externalReasoners.rewriter.RemoveNegation;
import org.eventb.core.prover.externalReasoners.rewriter.TrivialRewrites;
import org.eventb.core.prover.externalReasoners.rewriter.TypeExpRewrites;
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
	
	public static ITactic externalPP(boolean restricted,
			IProgressMonitor monitor) {
		final LegacyProvers.Input input = 
			new ExternalPP.Input(restricted, monitor);
		return pluginTac(new ExternalPP(), input);
	}
	
	public static ITactic externalML(int forces,
			IProgressMonitor monitor) {
		final LegacyProvers.Input input = 
			new ExternalML.Input(forces, monitor);
		return pluginTac(new ExternalML(), input);
	}
	
	public static ITactic lemma(String strLemma){
		return 
		encapsulate("add hyp "+strLemma,
				composeStrict(
						pluginTac(new Cut(),new Cut.Input(strLemma)),
						onPending(0,conjI()),
						onPending(2,impI())
				));
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(trivialGoalRewrite(),hyp(),Ti));
		return repeat(onAllPending(T));
	}
	
	public static ITactic doCase(String kase){	
		String lemma = "("+ kase +") \u2228\u00ac("+ kase +")";
		return 
		encapsulate("do case "+ kase ,
				composeStrict(
						pluginTac(new Cut(),new Cut.Input(lemma)),
						onPending(0,conjI()),
						onPending(2,pluginTac(new DisjE(),null)),
						onPending(2,norm())
				));
	}
	
	public static ITactic contradictGoal(){
		return 
		encapsulate("contradict goal",
		composeStrict(
				pluginTac(new Contr(),new Contr.Input()),
				onPending(0,impI())
		));
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
		return 
		encapsulate("provide witnesses",
		composeStrict(
				pluginTac(new ExI(),new ExI.Input(witnesses)),
						onPending(0,conjI())
				));
	}
	
	public static boolean exI_applicable(Predicate goal){
		return Lib.isExQuant(goal);
	}
	
	public static ITactic removeNegGoal(){
		return pluginTac(new RewriteGoal(),new RewriteGoal.Input(new RemoveNegation()));
	}
	
	public static boolean removeNegGoal_applicable(Predicate goal){
		return (new RemoveNegation()).isApplicable(goal);
	}
	
	
	// Tactics applicable on a hypothesis
	
	public static ITactic allF(Hypothesis univHyp, String... instantiations){
		return 
		encapsulate("instantiate ∀hyp "+ univHyp.toString(),
		composeStrict(
				pluginTac(new AllF(),new AllF.Input(instantiations,univHyp)),
				onPending(0,conjI()),
				onPending(1,impI())
		));
	}
	
	public static boolean allF_applicable(Hypothesis hyp){
		return Lib.isUnivQuant(hyp.getPredicate());
	}
	
	public static ITactic conjD(Hypothesis conjHyp){
		return 
		encapsulate("remove ∧hyp "+conjHyp.toString(),
		composeStrict(
				pluginTac(new ConjD(),new ConjD.Input(conjHyp)),
				onPending(0,impI())
		));
	}
	
	public static boolean conjD_applicable(Hypothesis hyp){
		return Lib.isConj(hyp.getPredicate());
	}
	
	public static ITactic impD(Hypothesis impHyp, boolean useContrapositive){
		return 
		encapsulate("use ⇒ hyp "+impHyp.toString(),
		composeStrict(
				pluginTac(new ImpD(),new ImpD.Input(impHyp,useContrapositive)),
				onPending(0,conjI()),
				onPending(1,impI())
		));
	}
	
	public static boolean impD_applicable(Hypothesis hyp){
		return Lib.isImp(hyp.getPredicate());
	}
	
	public static ITactic disjE(Hypothesis disjHyp){
		return 
		encapsulate("remove ∨hyp "+disjHyp.toString(),
				composeStrict(
						pluginTac(new DisjE(),new DisjE.Input(disjHyp)),
						onPending(0,conjI()),
						onAllPending(impI())
				));
	}
	
	public static boolean disjE_applicable(Hypothesis hyp){
		return Lib.isDisj(hyp.getPredicate());
	}
	
	public static ITactic eqE(Hypothesis eqHyp,boolean useReflexive){
		return 
		encapsulate("use = hyp "+ eqHyp.toString(),
		composeStrict(
				pluginTac(new Eq(),new Eq.Input(eqHyp,useReflexive)),
				onPending(0,impI())
		));
	}
	
	public static boolean eqE_applicable(Hypothesis hyp){
		return Lib.isEq(hyp.getPredicate());
	}
	
	public static ITactic exF(Hypothesis exHyp){
		return 
		encapsulate("remove ∃hyp "+exHyp.toString(),
		composeStrict(
				pluginTac(new ExF(),new ExF.Input(exHyp)),
				onPending(0,allI()),
				onPending(0,impI())
		));
	}
	
	public static boolean exF_applicable(Hypothesis hyp){
		return Lib.isExQuant(hyp.getPredicate());
	}
	
	public static ITactic removeNegHyp(Hypothesis hyp){
		return 
		encapsulate("remove ¬ hyp "+hyp.toString(),
				composeStrict(
						pluginTac(new RewriteHyp(),new RewriteHyp.Input(hyp,new RemoveNegation())),
						onPending(0,mngHyp(ActionType.DESELECT,hyp)),
						onPending(0,impI())
				));
	}
	
	public static boolean removeNegHyp_applicable(Hypothesis hyp){
		return (new RemoveNegation()).isApplicable(hyp.getPredicate());
	}
	
    // Tactics applicable on every hypothesis
	
	public static ITactic falsifyHyp(Hypothesis hyp){
		return 
		encapsulate("falsify hyp: "+hyp.toString(),
				composeStrict(
						pluginTac(new Contr(),new Contr.Input(hyp)),
						onPending(0,impI())
				));
	}
	
	// Misc tactics
	
	public static ITactic hyp() {
		return new ITactic.RuleTac(new Hyp());
	}
	
	public static ITactic trivial() {
		return compose(
				trivialGoalRewrite(),
				hyp()
		);
	}
	
	public static ITactic trivialGoalRewrite() {
		return compose(
				pluginTac(new RewriteGoal(),new RewriteGoal.Input(new TrivialRewrites())),
				pluginTac(new RewriteGoal(),new RewriteGoal.Input(new TypeExpRewrites()))
		);
		
	}
	
	public static ITactic prune() {
		return BasicTactics.prune();
	}
	
	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
		return new ITactic.RuleTac(new MngHyp(new HypothesesManagement.Action(type,hypotheses)));
	}
	

	public static ITactic mngHyp(ActionType type,Hypothesis hypothesis){
		return mngHyp(type,Collections.singleton(hypothesis));
	}
	
	public static ITactic postProcess() {
		return onAllPending(hyp());
		
	}
	
}
