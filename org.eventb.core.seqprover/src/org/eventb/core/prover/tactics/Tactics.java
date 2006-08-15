package org.eventb.core.prover.tactics;

import static org.eventb.core.prover.tactics.BasicTactics.compose;
import static org.eventb.core.prover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.prover.tactics.BasicTactics.repeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.reasonerInputs.CombiInput;
import org.eventb.core.prover.reasonerInputs.EmptyInput;
import org.eventb.core.prover.reasonerInputs.MultipleExprInput;
import org.eventb.core.prover.reasonerInputs.MultiplePredInput;
import org.eventb.core.prover.reasonerInputs.SinglePredInput;
import org.eventb.core.prover.reasonerInputs.SingleStringInput;
import org.eventb.core.prover.reasoners.AllD;
import org.eventb.core.prover.reasoners.AllI;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.ConjI;
import org.eventb.core.prover.reasoners.Contr;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.reasoners.DisjE;
import org.eventb.core.prover.reasoners.DoCase;
import org.eventb.core.prover.reasoners.Eq;
import org.eventb.core.prover.reasoners.ExE;
import org.eventb.core.prover.reasoners.ExI;
import org.eventb.core.prover.reasoners.ExternalML;
import org.eventb.core.prover.reasoners.ExternalPP;
import org.eventb.core.prover.reasoners.FalseHyp;
import org.eventb.core.prover.reasoners.Hyp;
import org.eventb.core.prover.reasoners.ImpE;
import org.eventb.core.prover.reasoners.ImpI;
import org.eventb.core.prover.reasoners.MngHyp;
import org.eventb.core.prover.reasoners.Review;
import org.eventb.core.prover.reasoners.RewriteGoal;
import org.eventb.core.prover.reasoners.RewriteHyp;
import org.eventb.core.prover.reasoners.TrueGoal;
import org.eventb.core.prover.reasoners.rewriter.DisjToImpl;
import org.eventb.core.prover.reasoners.rewriter.RemoveNegation;
import org.eventb.core.prover.reasoners.rewriter.TrivialRewrites;
import org.eventb.core.prover.reasoners.rewriter.TypeExpRewrites;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;


public class Tactics {
	
	// Globally applicable tactics
	
	public static ITactic externalPP(boolean restricted,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted),monitor);
	}
	
	public static ITactic externalPP(boolean restricted, long timeOutDelay,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalPP(),
				new ExternalPP.Input(restricted, timeOutDelay),
				monitor);
	}
	
	public static ITactic externalML(int forces,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces),
				monitor);
	}
	
	public static ITactic externalML(int forces, long timeOutDelay,
			IProgressMonitor monitor) {
		return BasicTactics.reasonerTac(
				new ExternalML(),
				new ExternalML.Input(forces, timeOutDelay),
				monitor);
	}
	
	
//	public static ITactic review(Set<Hypothesis> hyps,Predicate goal) {
//		return BasicTactics.reasonerTac(new Review(),new Review.Input(hyps,goal));
//	}
	
	public static ITactic review() {
		return review(1);
	}
	
	public static ITactic review(final int reviewerConfidence) {
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt) {
				return (BasicTactics.reasonerTac(
						new Review(),
						new CombiInput(
								new MultiplePredInput(Hypothesis.Predicates(pt.getSequent().selectedHypotheses())),
								new SinglePredInput(pt.getSequent().goal()),
								new SingleStringInput(Integer.toString(reviewerConfidence)))))
								.apply(pt);
			}
			
		};
	}
	
//	public static ITactic lemma1(String lemma,ITypeEnvironment typeEnv) {
//		return BasicTactics.reasonerTac(new Cut(),new SinglePredInput(lemma,typeEnv));
//	}

	public static ITactic lemma(final String lemma) {
		
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt) {
				return (BasicTactics.reasonerTac(
						new Cut(),
						new SinglePredInput(
								lemma,
								pt.getSequent().typeEnvironment())))
								.apply(pt);
			}
			
		};
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(trivialGoalRewrite(),tautology(),contradiction(),hyp(),Ti));
		return repeat(onAllPending(T));
	}
	
	public static ITactic doCase1(String trueCase,ITypeEnvironment typeEnv){	
		return BasicTactics.reasonerTac(new DoCase(),new SinglePredInput(trueCase,typeEnv));
	}
	
	public static ITactic doCase(final String trueCase) {
		
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt) {
				return (BasicTactics.reasonerTac(
						new DoCase(),
						new SinglePredInput(
								trueCase,
								pt.getSequent().typeEnvironment())))
								.apply(pt);
			}
			
		};
	}
	
//	public static ITactic lasoo1(IProverSequent seq){
//		
//		Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
//		freeIdents.addAll(Arrays.asList(seq.goal().getFreeIdentifiers()));
//		for (Hypothesis hyp : seq.selectedHypotheses()){
//			freeIdents.addAll(Arrays.asList(hyp.getPredicate().getFreeIdentifiers()));
//		}
//		
//		Set<Hypothesis> hypsToSelect = Hypothesis.freeIdentsSearch(seq.hypotheses(),freeIdents);
//		hypsToSelect.removeAll(seq.selectedHypotheses());
//		if (hypsToSelect.isEmpty())
//			return BasicTactics.failTac("No more Hyps found");
//
//		return mngHyp(ActionType.SELECT,hypsToSelect);
//	}
	
	public static ITactic lasoo(IProverSequent seq){
		
		return new ITactic(){

			public Object apply(IProofTreeNode pt) {
				IProverSequent seq = pt.getSequent();
				Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
				freeIdents.addAll(Arrays.asList(seq.goal().getFreeIdentifiers()));
				for (Hypothesis hyp : seq.selectedHypotheses()){
					freeIdents.addAll(Arrays.asList(hyp.getPredicate().getFreeIdentifiers()));
				}
				
				Set<Hypothesis> hypsToSelect = Hypothesis.freeIdentsSearch(seq.hypotheses(),freeIdents);
				hypsToSelect.removeAll(seq.selectedHypotheses());
				if (hypsToSelect.isEmpty())
					return "No more hypotheses found";
				return (mngHyp(ActionType.SELECT,hypsToSelect)).apply(pt);
			}
			
		};
	}
	

	// Tactics applicable on the goal
	

	public static ITactic contradictGoal(){
		return BasicTactics.reasonerTac(new Contr(),new SinglePredInput(Lib.True));
	}
	
	public static boolean contradictGoal_applicable(Predicate goal){
		return (! Lib.isFalse(goal));
	}
	
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
	
//	public static ITactic exI(String... witnesses) {
//		return BasicTactics.reasonerTac(new ExI(),new ExI.Input(witnesses));
//	}
	
	public static ITactic exI(final String... witnesses) {
		return new ITactic(){

			public Object apply(IProofTreeNode pt) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pt.getSequent().goal());
				return (
						BasicTactics.reasonerTac(
								new ExI(),
								new MultipleExprInput(
										witnesses,
										boundIdentDecls,
										typeEnv)
								)).apply(pt);
			}
			
		};
	}
	
	public static boolean exI_applicable(Predicate goal){
		return Lib.isExQuant(goal);
	}
	
	public static ITactic removeNegGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new SingleStringInput(new RemoveNegation().getRewriterID()));
	}
	
	public static boolean removeNegGoal_applicable(Predicate goal){
		return (new RemoveNegation()).isApplicable(goal);
	}
	
	public static ITactic disjToImpGoal(){
		return BasicTactics.reasonerTac(new RewriteGoal(),new SingleStringInput(new DisjToImpl().getRewriterID()));
	}
	
	public static boolean disjToImpGoal_applicable(Predicate goal){
		return (new DisjToImpl()).isApplicable(goal);
	}
	
	
	// Tactics applicable on a hypothesis
	
	// TODO : change order of input in one of the two places
	public static ITactic allD(final Hypothesis univHyp, final String... instantiations){
		return new ITactic(){

			public Object apply(IProofTreeNode pt) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHyp.getPredicate());
				return (
						BasicTactics.reasonerTac(
								new AllD(),
								new CombiInput(
										new MultipleExprInput(
												instantiations,
												boundIdentDecls,
												typeEnv),
												new SinglePredInput(univHyp))
				)).apply(pt);
			}
			
		};
	}
	
	public static boolean allD_applicable(Hypothesis hyp){
		return Lib.isUnivQuant(hyp.getPredicate());
	}
	
	public static ITactic conjD(Hypothesis conjHyp){
		return BasicTactics.reasonerTac(new ConjE(),new SinglePredInput(conjHyp));
	}
	
	
	public static boolean conjD_applicable(Hypothesis hyp){
		return Lib.isConj(hyp.getPredicate());
	}
	
	
	// TODO : remove use contrapositive
	public static ITactic impE(Hypothesis impHyp, boolean useContrapositive){
		return BasicTactics.reasonerTac(new ImpE(),new SinglePredInput(impHyp));
	}
	
	public static boolean impE_applicable(Hypothesis hyp){
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
	public static ITactic exE(Hypothesis exHyp){
		return BasicTactics.reasonerTac(new ExE(),new SinglePredInput(exHyp));
	}
	
	public static boolean exE_applicable(Hypothesis hyp){
		return Lib.isExQuant(hyp.getPredicate());
	}
	

	public static ITactic removeNegHyp(Hypothesis hyp){
		return  BasicTactics.reasonerTac(new RewriteHyp(),
				new CombiInput(
						new SinglePredInput(hyp),
						new SingleStringInput(new RemoveNegation().getRewriterID())));
	}
	
	public static boolean removeNegHyp_applicable(Hypothesis hyp){
		return (new RemoveNegation()).isApplicable(hyp.getPredicate());
	}
	
	public static ITactic falsifyHyp(Hypothesis hyp){
		return BasicTactics.reasonerTac(new Contr(),new SinglePredInput(hyp));
	}
	
	public static boolean falsifyHyp_applicable(Hypothesis hyp, IProverSequent seq){
		return (!seq.goal().equals(Lib.makeNeg(hyp.getPredicate())));
	}
	
    // Tactics applicable on every hypothesis
	
	
	// Misc tactics
	
	public static ITactic hyp() {
		return BasicTactics.reasonerTac(new Hyp(),new EmptyInput());
	}
	
	public static ITactic tautology() {
		return BasicTactics.reasonerTac(new TrueGoal(),new EmptyInput());
	}
	
	public static ITactic contradiction() {
		return BasicTactics.reasonerTac(new FalseHyp(),new EmptyInput());
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
				BasicTactics.reasonerTac(new RewriteGoal(),new SingleStringInput(new TrivialRewrites().getRewriterID())),
				BasicTactics.reasonerTac(new RewriteGoal(),new SingleStringInput(new TypeExpRewrites().getRewriterID()))
		);
	}
	
	public static ITactic prune() {
		return BasicTactics.prune();
	}
	
	public static ITactic mngHyp(ActionType type,Set<Hypothesis> hypotheses){
		return BasicTactics.reasonerTac(
				new MngHyp(),
				new CombiInput(
						new SingleStringInput(type.toString()),
						new MultiplePredInput(Hypothesis.Predicates(hypotheses))));
	}

	public static ITactic mngHyp(ActionType type,Hypothesis hypothesis){
		return mngHyp(type,Collections.singleton(hypothesis));
	}
	
	public static ITactic postProcessBeginner() {
		System.out.println("* Beginner Mode *");
		return onAllPending(
				compose(
						tautology(),
						hyp(),
						impI())
						);
				
	}
	
	public static ITactic postProcessExpert() {
		System.out.println("* Expert Mode *");
		return norm();
				
	}
	
}
