package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.reasoners.AllD;
import org.eventb.core.prover.reasoners.AllI;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.ConjI;
import org.eventb.core.prover.reasoners.Contr;
import org.eventb.core.prover.reasoners.Contradiction;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.reasoners.DisjE;
import org.eventb.core.prover.reasoners.DoCase;
import org.eventb.core.prover.reasoners.Eq;
import org.eventb.core.prover.reasoners.ExE;
import org.eventb.core.prover.reasoners.ExI;
import org.eventb.core.prover.reasoners.ExternalML;
import org.eventb.core.prover.reasoners.ExternalPP;
import org.eventb.core.prover.reasoners.Hyp;
import org.eventb.core.prover.reasoners.ImpE;
import org.eventb.core.prover.reasoners.ImpI;
import org.eventb.core.prover.reasoners.MngHyp;
import org.eventb.core.prover.reasoners.Review;
import org.eventb.core.prover.reasoners.RewriteGoal;
import org.eventb.core.prover.reasoners.RewriteHyp;
import org.eventb.core.prover.reasoners.Tautology;
import org.eventb.core.prover.reasoners.rewriter.DisjToImpl;
import org.eventb.core.prover.reasoners.rewriter.RemoveNegation;
import org.eventb.core.prover.reasoners.rewriter.TrivialRewrites;
import org.eventb.core.prover.reasoners.rewriter.TypeExpRewrites;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;


public class Tactics {
	
	// Globally applicable tactics
	
	public static ITactic externalPP(boolean restricted,
			IProgressMonitor monitor) {
//		final org.eventb.core.prover.reasoner.LegacyProvers.Input input = 
//			new ;
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted, monitor));
	}
	
	public static ITactic externalPP(boolean restricted, long timeOutDelay,
			IProgressMonitor monitor) {
//		final LegacyProvers.Input input = 
//			new ExternalPP.Input(restricted, timeOutDelay, monitor);
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted, timeOutDelay, monitor));
	}
	
	public static ITactic externalML(int forces,
			IProgressMonitor monitor) {
//		final LegacyProvers.Input input = 
//			new ExternalML.Input(forces, monitor);
//		return pluginTac(new ExternalML(), input);
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces, monitor));
	}
	
	public static ITactic externalML(int forces, long timeOutDelay,
			IProgressMonitor monitor) {
//		final LegacyProvers.Input input = 
//			new ExternalML.Input(forces, timeOutDelay, monitor);
//		return pluginTac(new ExternalML(), input);
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces, timeOutDelay, monitor));
	}
	
//	public static ITactic lemma(String strLemma){
//		return 
//		encapsulate("add hyp "+strLemma,
//				composeStrict(
//						pluginTac(new Cut(),new Cut.Input(strLemma)),
//						onPending(0,conjI()),
//						onPending(2,impI())
//				));
//	}
	
	public static ITactic review(Set<Hypothesis> hyps,Predicate goal) {
		return BasicTactics.reasonerTac(new Review(),new Review.Input(hyps,goal));
	}
	
	public static ITactic review() {
		return BasicTactics.reasonerTac(new Review(),null);
	}
	
	public static ITactic lemma(String lemma) {
		return BasicTactics.reasonerTac(new Cut(),new Cut.Input(lemma));
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(trivialGoalRewrite(),tautology(),contradiction(),hyp(),Ti));
		return repeat(onAllPending(T));
	}
	
//	public static ITactic doCase(String kase){	
//		String lemma = "("+ kase +") \u2228\u00ac("+ kase +")";
//		return 
//		encapsulate("do case "+ kase ,
//				composeStrict(
//						pluginTac(new org.eventb.core.prover.externalReasoners.Cut(),new org.eventb.core.prover.externalReasoners.Cut.Input(lemma)),
//						onPending(0,conjI()),
//						onPending(2,pluginTac(new org.eventb.core.prover.externalReasoners.DisjE(),null)),
//						onPending(2,norm()),
//						// onPending(0,externalPP(true,null))
//						onPending(0,excludedMiddle())
//				));
//		
//	}
	
	public static ITactic doCase(String trueCase){	
		return BasicTactics.reasonerTac(new DoCase(),new DoCase.Input(trueCase));
	}
	
//	public static ITactic contradictGoal(){
//		return 
//		encapsulate("contradict goal",
//		composeStrict(
//				pluginTac(new Contr(),new Contr.Input()),
//				onPending(0,impI())
//		));
//	}
	
	public static ITactic contradictGoal(){
		return BasicTactics.reasonerTac(new Contr(),new Contr.Input());
	}
	

	// Tactics applicable on the goal
	
//	public static ITactic impI() {
//		return new ITactic.RuleTac(new ImpI());
//	}
	
	public static ITactic impI() {
		return BasicTactics.reasonerTac(new ImpI(),new Reasoner.DefaultInput());
	}
	
	public static boolean impI_applicable(Predicate goal){
		return Lib.isImp(goal);
	}
	
//	public static ITactic conjI() {
//		return new ITactic.RuleTac(new ConjI());
//	}
	
	public static ITactic conjI() {
		return BasicTactics.reasonerTac(new ConjI(),new Reasoner.DefaultInput());
	}
	
	public static boolean conjI_applicable(Predicate goal){
		return Lib.isConj(goal);
	}
	
	public static ITactic allI() {
		return BasicTactics.reasonerTac(new AllI(),new Reasoner.DefaultInput());
	}
	
	public static boolean allI_applicable(Predicate goal){
		return Lib.isUnivQuant(goal);
	}
	
//	public static ITactic exI(String... witnesses){
//		return 
//		encapsulate("provide witnesses",
//		composeStrict(
//				pluginTac(new ExI(),new ExI.Input(witnesses)),
//						onPending(0,conjI())
//				));
//	}
	
	public static ITactic exI(String... witnesses) {
		return BasicTactics.reasonerTac(new ExI(),new ExI.Input(witnesses));
	}
	
	public static boolean exI_applicable(Predicate goal){
		return Lib.isExQuant(goal);
	}
	
//	public static ITactic removeNegGoal(){
//		return pluginTac(new RewriteGoal(),new RewriteGoal.Input(new RemoveNegation()));
//	}
	
	public static ITactic removeNegGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new RemoveNegation()));
	}
	
	public static boolean removeNegGoal_applicable(Predicate goal){
		return (new RemoveNegation()).isApplicable(goal);
	}
	
//	public static ITactic disjToImpGoal(){
//		return pluginTac(new RewriteGoal(),new RewriteGoal.Input(new DisjToImpl()));
//	}
	
	public static ITactic disjToImpGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new DisjToImpl()));
	}
	
	public static boolean disjToImpGoal_applicable(Predicate goal){
		return (new DisjToImpl()).isApplicable(goal);
	}
	
	
	// Tactics applicable on a hypothesis
	
//	public static ITactic allF(Hypothesis univHyp, String... instantiations){
//		return 
//		encapsulate("instantiate ∀hyp "+ univHyp.toString(),
//		composeStrict(
//				pluginTac(new AllF(),new AllF.Input(instantiations,univHyp)),
//				onPending(0,conjI()),
//				onPending(1,impI())
//		));
//	}
	
	// TODO : rename to allD , change order of input in one of the two places
	public static ITactic allF(Hypothesis univHyp, String... instantiations){
		return BasicTactics.reasonerTac(new AllD(),new AllD.Input(instantiations,univHyp));
	}
	
	public static boolean allF_applicable(Hypothesis hyp){
		return Lib.isUnivQuant(hyp.getPredicate());
	}
	
//	public static ITactic conjD(Hypothesis conjHyp){
//		return 
//		encapsulate("remove ∧hyp "+conjHyp.toString(),
//		composeStrict(
//				pluginTac(new ConjD(),new ConjD.Input(conjHyp)),
//				onPending(0,impI())
//		));
//	}
	
	public static ITactic conjD(Hypothesis conjHyp){
		return BasicTactics.reasonerTac(new ConjE(),new ConjE.Input(conjHyp));
	}
	
	
	public static boolean conjD_applicable(Hypothesis hyp){
		return Lib.isConj(hyp.getPredicate());
	}
	
	
	// TODO : rename to impE.. remove use contrapositive
	public static ITactic impD(Hypothesis impHyp, boolean useContrapositive){
		return BasicTactics.reasonerTac(new ImpE(),new ImpE.Input(impHyp));
	}
	
	public static boolean impD_applicable(Hypothesis hyp){
		return Lib.isImp(hyp.getPredicate());
	}
	
//	public static ITactic disjE(Hypothesis disjHyp){
//		return 
//		encapsulate("remove ∨hyp "+disjHyp.toString(),
//				composeStrict(
//						pluginTac(new DisjE(),new DisjE.Input(disjHyp)),
//						onPending(0,conjI()),
//						onAllPending(impI())
//				));
//	}
	
	public static ITactic disjE(Hypothesis disjHyp){
		return BasicTactics.reasonerTac(new DisjE(),new DisjE.Input(disjHyp));
	}
	
	public static boolean disjE_applicable(Hypothesis hyp){
		return Lib.isDisj(hyp.getPredicate());
	}
	
//	public static ITactic eqE(Hypothesis eqHyp,boolean useReflexive){
//		return 
//		encapsulate("use = hyp "+ eqHyp.toString(),
//		composeStrict(
//				pluginTac(new Eq(),new Eq.Input(eqHyp,useReflexive)),
//				onPending(0,impI())
//		));
//	}

	// TODO : remove use reflexive
	public static ITactic eqE(Hypothesis eqHyp,boolean useReflexive){
		return BasicTactics.reasonerTac(new Eq(),new Eq.Input(eqHyp));
	}
	
	public static boolean eqE_applicable(Hypothesis hyp){
		return Lib.isEq(hyp.getPredicate());
	}
	
//	public static ITactic exF(Hypothesis exHyp){
//		return 
//		encapsulate("remove ∃hyp "+exHyp.toString(),
//		composeStrict(
//				pluginTac(new ExF(),new ExF.Input(exHyp)),
//				onPending(0,allI()),
//				onPending(0,impI())
//		));
//	}
	
	// TODO : rename to exE
	public static ITactic exF(Hypothesis exHyp){
		return BasicTactics.reasonerTac(new ExE(),new ExE.Input(exHyp));
	}
	
	public static boolean exF_applicable(Hypothesis hyp){
		return Lib.isExQuant(hyp.getPredicate());
	}
	
//	public static ITactic removeNegHyp(Hypothesis hyp){
//		return 
//		encapsulate("remove ¬ hyp "+hyp.toString(),
//				composeStrict(
//						pluginTac(new RewriteHyp(),new RewriteHyp.Input(hyp,new RemoveNegation())),
//						onPending(0,mngHyp(ActionType.DESELECT,hyp)),
//						onPending(0,impI())
//				));
//	}
//	
	public static ITactic removeNegHyp(Hypothesis hyp){
		return  BasicTactics.reasonerTac(new RewriteHyp(),new RewriteHyp.Input(new RemoveNegation(),hyp));
	}
	
	public static boolean removeNegHyp_applicable(Hypothesis hyp){
		return (new RemoveNegation()).isApplicable(hyp.getPredicate());
	}
	
    // Tactics applicable on every hypothesis
	
//	public static ITactic falsifyHyp(Hypothesis hyp){
//		return 
//		encapsulate("falsify hyp: "+hyp.toString(),
//				composeStrict(
//						pluginTac(new Contr(),new Contr.Input(hyp)),
//						onPending(0,impI())
//				));
//	}
	
	public static ITactic falsifyHyp(Hypothesis hyp){
		return BasicTactics.reasonerTac(new Contr(),new Contr.Input(hyp));
	}
	
	// Misc tactics
	
	public static ITactic hyp() {
		return BasicTactics.reasonerTac(new Hyp(),new Reasoner.DefaultInput());
	}
	
	public static ITactic tautology() {
		return BasicTactics.reasonerTac(new Tautology(),new Reasoner.DefaultInput());
	}
	
	public static ITactic contradiction() {
		return BasicTactics.reasonerTac(new Contradiction(),new Reasoner.DefaultInput());
	}
	
	public static ITactic trivial() {
		return compose(
				trivialGoalRewrite(),
				tautology(),
				hyp()
		);
	}
	
//	public static ITactic trivialGoalRewrite() {
//		return compose(
//				pluginTac(new RewriteGoal(),new RewriteGoal.Input(new TrivialRewrites())),
//				pluginTac(new RewriteGoal(),new RewriteGoal.Input(new TypeExpRewrites()))
//		);
//	}
	
	public static ITactic trivialGoalRewrite() {
		return compose(
				BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new TrivialRewrites())),
				BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new TypeExpRewrites()))
		);
	}
	
	public static ITactic prune() {
		return BasicTactics.prune();
	}
	
//	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
//		return new ITactic.RuleTac(new MngHyp(new HypothesesManagement.Action(type,hypotheses)));
//	}
	
	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
		return BasicTactics.reasonerTac(
				new MngHyp(),
				new MngHyp.Input(new HypothesesManagement.Action(type,hypotheses)));
	}

	public static ITactic mngHyp(ActionType type,Hypothesis hypothesis){
		return mngHyp(type,Collections.singleton(hypothesis));
	}
	
	public static ITactic postProcess() {
		return onAllPending(
				compose(
						tautology(),
						hyp(),
						impI())
						);
				
	}
	
//	private static ITactic excludedMiddle() {
//		return compose(
//				disjToImpGoal(),
//				norm()
//				);
//	}
	
}
