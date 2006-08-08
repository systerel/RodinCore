package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.reasoners.AllD;
import org.eventb.core.prover.reasoners.AllI;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.ConjI;
import org.eventb.core.prover.reasoners.Contr;
import org.eventb.core.prover.reasoners.Contradiction;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.reasoners.DisjE;
import org.eventb.core.prover.reasoners.DoCase;
import org.eventb.core.prover.reasoners.EmptyInput;
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
import org.eventb.core.prover.reasoners.SinglePredInput;
import org.eventb.core.prover.reasoners.Tautology;
import org.eventb.core.prover.reasoners.rewriter.DisjToImpl;
import org.eventb.core.prover.reasoners.rewriter.RemoveNegation;
import org.eventb.core.prover.reasoners.rewriter.TrivialRewrites;
import org.eventb.core.prover.reasoners.rewriter.TypeExpRewrites;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;


public class Tactics {
	
	// Globally applicable tactics
	
	public static ITactic externalPP(boolean restricted,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted, monitor));
	}
	
	public static ITactic externalPP(boolean restricted, long timeOutDelay,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted, timeOutDelay, monitor));
	}
	
	public static ITactic externalML(int forces,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces, monitor));
	}
	
	public static ITactic externalML(int forces, long timeOutDelay,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces, timeOutDelay, monitor));
	}
	
	
//	public static ITactic review(Set<Hypothesis> hyps,Predicate goal) {
//		return BasicTactics.reasonerTac(new Review(),new Review.Input(hyps,goal));
//	}
	
	public static ITactic review() {
		return BasicTactics.reasonerTac(new Review(),new Review.Input(1));
	}
	
	public static ITactic review(int reviewerConfidence) {
		return BasicTactics.reasonerTac(new Review(),new Review.Input(reviewerConfidence));
	}
	
	public static ITactic lemma(String lemma,ITypeEnvironment typeEnv) {
		return BasicTactics.reasonerTac(new Cut(),new SinglePredInput(lemma,typeEnv));
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(trivialGoalRewrite(),tautology(),contradiction(),hyp(),Ti));
		return repeat(onAllPending(T));
	}
	
	public static ITactic doCase(String trueCase,ITypeEnvironment typeEnv){	
		return BasicTactics.reasonerTac(new DoCase(),new SinglePredInput(trueCase,typeEnv));
	}
	
	
	public static ITactic contradictGoal(){
		return BasicTactics.reasonerTac(new Contr(),new Contr.Input());
	}
	
//	public static ITactic lasoo(IProverSequent seq){
//		
//		Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
//		freeIdents.addAll(Arrays.asList(seq.goal().getFreeIdentifiers()));
//		for (Hyp)
//		
//		Set<Hypothesis> hyps;
//		
//		
//		return mngHyp(ActionType.SELECT,hyps);
//	}
	

	// Tactics applicable on the goal
	
	
	public static ITactic impI() {
		return BasicTactics.reasonerTac(new ImpI(),new EmptyInput());
	}
	
	public static boolean impI_applicable(Predicate goal){
		return Lib.isImp(goal);
	}
	
	public static ITactic conjI() {
		return BasicTactics.reasonerTac(new ConjI(),new EmptyInput());
	}
	
	public static boolean conjI_applicable(Predicate goal){
		return Lib.isConj(goal);
	}
	
	public static ITactic allI() {
		return BasicTactics.reasonerTac(new AllI(),new EmptyInput());
	}
	
	public static boolean allI_applicable(Predicate goal){
		return Lib.isUnivQuant(goal);
	}
	
	public static ITactic exI(String... witnesses) {
		return BasicTactics.reasonerTac(new ExI(),new ExI.Input(witnesses));
	}
	
	public static boolean exI_applicable(Predicate goal){
		return Lib.isExQuant(goal);
	}
	
	public static ITactic removeNegGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new RemoveNegation()));
	}
	
	public static boolean removeNegGoal_applicable(Predicate goal){
		return (new RemoveNegation()).isApplicable(goal);
	}
	
	public static ITactic disjToImpGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new DisjToImpl()));
	}
	
	public static boolean disjToImpGoal_applicable(Predicate goal){
		return (new DisjToImpl()).isApplicable(goal);
	}
	
	
	// Tactics applicable on a hypothesis
	
	// TODO : rename to allD , change order of input in one of the two places
	public static ITactic allF(Hypothesis univHyp, String... instantiations){
		return BasicTactics.reasonerTac(new AllD(),new AllD.Input(instantiations,univHyp));
	}
	
	public static boolean allF_applicable(Hypothesis hyp){
		return Lib.isUnivQuant(hyp.getPredicate());
	}
	
	public static ITactic conjD(Hypothesis conjHyp){
		return BasicTactics.reasonerTac(new ConjE(),new SinglePredInput(conjHyp));
	}
	
	
	public static boolean conjD_applicable(Hypothesis hyp){
		return Lib.isConj(hyp.getPredicate());
	}
	
	
	// TODO : rename to impE.. remove use contrapositive
	public static ITactic impD(Hypothesis impHyp, boolean useContrapositive){
		return BasicTactics.reasonerTac(new ImpE(),new SinglePredInput(impHyp));
	}
	
	public static boolean impD_applicable(Hypothesis hyp){
		return Lib.isImp(hyp.getPredicate());
	}
	
	public static ITactic disjE(Hypothesis disjHyp){
		return BasicTactics.reasonerTac(new DisjE(),new SinglePredInput(disjHyp));
	}
	
	public static boolean disjE_applicable(Hypothesis hyp){
		return Lib.isDisj(hyp.getPredicate());
	}

	// TODO : remove use reflexive
	public static ITactic eqE(Hypothesis eqHyp,boolean useReflexive){
		return BasicTactics.reasonerTac(new Eq(),new SinglePredInput(eqHyp));
	}
	
	public static boolean eqE_applicable(Hypothesis hyp){
		return Lib.isEq(hyp.getPredicate());
	}
	
	// TODO : rename to exE
	public static ITactic exF(Hypothesis exHyp){
		return BasicTactics.reasonerTac(new ExE(),new SinglePredInput(exHyp));
	}
	
	public static boolean exF_applicable(Hypothesis hyp){
		return Lib.isExQuant(hyp.getPredicate());
	}
	

	public static ITactic removeNegHyp(Hypothesis hyp){
		return  BasicTactics.reasonerTac(new RewriteHyp(),new RewriteHyp.Input(new RemoveNegation(),hyp));
	}
	
	public static boolean removeNegHyp_applicable(Hypothesis hyp){
		return (new RemoveNegation()).isApplicable(hyp.getPredicate());
	}
	
    // Tactics applicable on every hypothesis
	
	public static ITactic falsifyHyp(Hypothesis hyp){
		return BasicTactics.reasonerTac(new Contr(),new Contr.Input(hyp));
	}
	
	// Misc tactics
	
	public static ITactic hyp() {
		return BasicTactics.reasonerTac(new Hyp(),new EmptyInput());
	}
	
	public static ITactic tautology() {
		return BasicTactics.reasonerTac(new Tautology(),new EmptyInput());
	}
	
	public static ITactic contradiction() {
		return BasicTactics.reasonerTac(new Contradiction(),new EmptyInput());
	}
	
	public static ITactic trivial() {
		return compose(
				trivialGoalRewrite(),
				tautology(),
				hyp()
		);
	}
	
	public static ITactic trivialGoalRewrite() {
		return compose(
				BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new TrivialRewrites())),
				BasicTactics.reasonerTac(new RewriteGoal(),new RewriteGoal.Input(new TypeExpRewrites()))
		);
	}
	
	public static ITactic prune() {
		return BasicTactics.prune();
	}
	
	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
		return BasicTactics.reasonerTac(
				new MngHyp(),
				new MngHyp.Input(new HypothesesManagement.Action(type,hypotheses)));
	}

	public static ITactic mngHyp(ActionType type,Hypothesis hypothesis){
		return mngHyp(type,Collections.singleton(hypothesis));
	}
	
	public static ITactic postProcessBeginner() {
		return onAllPending(
				compose(
						tautology(),
						hyp(),
						impI())
						);
				
	}
	
	public static ITactic postProcessExpert() {
		return onAllPending(
				norm()
						);
				
	}
	
}
