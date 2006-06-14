package org.eventb.core.prover.tactics;

import org.eventb.core.prover.IProofTreeNode;

public interface ITactic {
	
	public abstract Object apply(IProofTreeNode pt);
	
//	public static class prune implements ITactic {
//	
//		public prune(){}
//		
//		public Object apply(IProofTreeNode pt){
//			if (pt.isOpen()) return "Root is already open";
//			pt.pruneChildren();
//			return null;
//		}
//	}
//	
//	public static class RuleTac implements ITactic {
//		
//		private final ProofRule rule;
//		
//		public RuleTac(ProofRule rule)
//		{
//			this.rule = rule;
//		}
//		
//		public Object apply(IProofTreeNode pt){
//			if (!pt.isOpen()) return "Root already has children";
//			if (pt.applyRule(this.rule)) return null;
//			else return "Rule "+this.rule.getName()+" is not applicable";
//			
//		}
//	}
//	
//	public static class plugin implements ITactic {
//		
//		private final IExternalReasoner plugin;
//		private final IExtReasonerInput pluginInput;
//		
//		public plugin(IExternalReasoner plugin,IExtReasonerInput pluginInput)
//		{
//			this.plugin = plugin;
//			this.pluginInput = pluginInput;
//		}
//		
//		public Object apply(IProofTreeNode pt){
//			if (!pt.isOpen()) return "Root already has children";
//			IExtReasonerOutput po = this.plugin.apply(pt.getSequent(),pluginInput);
//			if (po == null) return "! Plugin returned null !";
//			if (!(po instanceof SuccessfullExtReasonerOutput)) return po;
//			ProofRule plb = new PLb((SuccessfullExtReasonerOutput) po);
//			ITactic temp = new RuleTac(plb);
//			return temp.apply(pt);
//		}
//	}
//		
//	public static class onAllPending implements ITactic {
//		
//		private ITactic t;
//		
//		public onAllPending(ITactic t){
//			this.t = t;
//		}
//		
//		public Object apply(IProofTreeNode pt) {
//			String applicable = "onAllPending unapplicable";
//			IProofTreeNode[] subgoals = pt.getOpenDescendants();
//			for(IProofTreeNode subgoal : subgoals){
//				if (t.apply(subgoal) == null) applicable = null;
//			}
//			return applicable;
//		}
//	}
//	
//	
//	public static class onPending implements ITactic {
//		
//		private ITactic t;
//		private int subgoalNo;
//		
//		public onPending(int subgoalNo,ITactic t){
//			this.t = t;
//			this.subgoalNo = subgoalNo;
//		}
//		
//		public Object apply(IProofTreeNode pt) {
//			IProofTreeNode[] subgoals = pt.getOpenDescendants();
//			if (this.subgoalNo < 0 || this.subgoalNo >= subgoals.length) 
//				return "Subgoal "+this.subgoalNo+" non-existent";
//			IProofTreeNode subgoal = subgoals[this.subgoalNo];
//			if (subgoal == null) return "Subgoal "+this.subgoalNo+" is null!";
//			return this.t.apply(subgoal);
//		}
//		
//		
//	}
//		
//	public static class compose implements ITactic {
//
//		private ITactic[] tactics;
//		
//		public compose(ITactic ... tactics){
//			this.tactics = tactics;
//		}
//		
//		public Object apply(IProofTreeNode pt) {
//			boolean applicable = false;
//			Object lastFailure = "compose unapplicable: no tactics";
//			for (ITactic tactic : tactics){
//				Object tacticApp = tactic.apply(pt);
//				if (tacticApp == null) applicable = true; 
//				else lastFailure = tacticApp;
//			}
//			return applicable ? null : lastFailure;
//		}
//
//	}
//	
//	public static class composeStrict implements ITactic {
//
//		private ITactic[] tactics;
//		
//		public composeStrict(ITactic ... tactics){
//			this.tactics = tactics;
//		}
//		
//		public Object apply(IProofTreeNode pt) {
//			for (ITactic tactic : tactics){
//				Object tacticApp = tactic.apply(pt);
//				if (tacticApp != null) return tacticApp; 
//			}
//			return null;
//		}
//
//	}
//
//
//	public class repeat implements ITactic {
//
//		ITactic t;
//		
//		public repeat(ITactic t){
//			this.t = t;
//		}
//		
//		public Object apply(IProofTreeNode pt) {
//			boolean applicable = false;
//			Object tacticApp = t.apply(pt);
//			while(tacticApp == null){
//				applicable = true;
//				tacticApp = t.apply(pt);
//			};
//			return applicable ? null : tacticApp;
//		}
//
//	}
//	
////	public static class conjE_auto implements Tactic{
////		
////		ProverPlugin conjE = new conjE();
////		
////		public boolean apply(ProofTree pt){
////			if (pt.rootHasChildren()) return false;
////			for (Predicate hyp : pt.getRootSeq().selectedHypotheses()){
////				if (lib.isConj(hyp)) {
////					Tactic t = new plugin(conjE,new conjE.Input(hyp));
////					return t.apply(pt);
////				}
////			}
////			return false;
////		}
////	}
	
}
