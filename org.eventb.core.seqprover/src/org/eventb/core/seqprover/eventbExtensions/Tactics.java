package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.compose;
import static org.eventb.core.seqprover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.HypothesesManagement.ActionType;
import org.eventb.core.seqprover.reasonerInputs.CombiInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.reasoners.MngHyp;
import org.eventb.core.seqprover.reasoners.Review;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjE;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjI;
import org.eventb.internal.core.seqprover.eventbExtensions.Contr;
import org.eventb.internal.core.seqprover.eventbExtensions.Cut;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.DoCase;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;
import org.eventb.internal.core.seqprover.eventbExtensions.ExE;
import org.eventb.internal.core.seqprover.eventbExtensions.ExI;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.RewriteGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.RewriteHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjToImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TrivialRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeExpRewrites;


public class Tactics {
	
	// Globally applicable tactics
	
	public static ITactic review(final int reviewerConfidence) {
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				final ITactic tactic = BasicTactics.reasonerTac(
						new Review(),
						new Review.Input(pt.getSequent(), reviewerConfidence));
				return tactic.apply(pt, pm);
			}		
		};
	}

	public static ITactic lemma(final String lemma) {
		
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(
						new Cut(),
						new SinglePredInput(
								lemma,
								pt.getSequent().typeEnvironment())))
								.apply(pt, pm);
			}
			
		};
	}
	
	public static ITactic norm(){
		ITactic Ti = repeat(compose(conjI(),allI(),impI()));
		ITactic T = repeat(compose(hyp(),trivialGoalRewrite(),tautology(),contradiction(),hyp(),Ti));
		return repeat(onAllPending(T));
	}
	
	public static ITactic doCase1(String trueCase,ITypeEnvironment typeEnv){	
		return BasicTactics.reasonerTac(new DoCase(),new SinglePredInput(trueCase,typeEnv));
	}
	
	public static ITactic doCase(final String trueCase) {
		
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(
						new DoCase(),
						new SinglePredInput(
								trueCase,
								pt.getSequent().typeEnvironment())))
								.apply(pt, pm);
			}
			
		};
	}
	
	
	public static ITactic lasoo(){
		
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
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
				return (mngHyp(ActionType.SELECT,hypsToSelect)).apply(pt, pm);
			}
			
		};
	}
	

	// Tactics applicable on the goal
	

	public static ITactic contradictGoal(){
		return BasicTactics.reasonerTac(new Contr(),new SinglePredInput(Lib.True));
	}
	
//	public static boolean contradictGoal_applicable(Predicate goal){
//		return (! Lib.isFalse(goal));
//	}
	
	public static boolean contradictGoal_applicable(IProofTreeNode node) {
		Predicate goal = node.getSequent().goal();
		if (goal.equals(Lib.False)) return false;
		Predicate negGoal = Lib.makeNeg(goal);
		if (negGoal.equals(Lib.True)) return false;
		if (Hypothesis.containsPredicate(
				node.getSequent().selectedHypotheses(),
				negGoal));
		return true;
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
	
	public static ITactic exI(final String... witnesses) {
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pt.getSequent().goal());
				return (
						BasicTactics.reasonerTac(
								new ExI(),
								new MultipleExprInput(
										witnesses,
										boundIdentDecls,
										typeEnv)
								)).apply(pt, pm);
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
		final Predicate pred = univHyp.getPredicate();
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pred);
				final AllD.Input input = new AllD.Input(instantiations,
						boundIdentDecls, typeEnv, pred);
				return (BasicTactics.reasonerTac(new AllD(), input)).apply(pt,
						pm);
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
	
	public static ITactic impE(Hypothesis impHyp){
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

	public static ITactic eqE(Hypothesis eqHyp){
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
				hyp(),
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
	
	public static ITactic mngHyp(ActionType type, Set<Hypothesis> hypotheses) {
		return BasicTactics.reasonerTac(
				new MngHyp(),
				new MngHyp.Input(type, hypotheses));
	}

	public static ITactic mngHyp(ActionType type, Hypothesis hypothesis){
		return mngHyp(type, Collections.singleton(hypothesis));
	}
	
	public static ITactic postProcessBeginner() {
		// System.out.println("* Beginner Mode *");
		return onAllPending(
				compose(
						tautology(),
						hyp(),
						impI())
						);
				
	}
	
	public static ITactic postProcessExpert() {
		// System.out.println("* Expert Mode *");
		return norm();
				
	}
	
	public static ITactic afterLasoo(final ITactic tactic){
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				
				lasoo().apply(pt, pm);
				final IProofTreeNode firstOpenDescendant = pt.getFirstOpenDescendant();
				Object output = tactic.apply(firstOpenDescendant, pm);
				if (output == null){
					// tactic was successful
					return null;
				}
				else
				{ // revert proof tree
					prune().apply(pt, pm);
					return output;
				}
				
			}
			
		};
	}
	
}
