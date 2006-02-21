package org.eventb.core.prover.tactics;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.rules.PLb;
import org.eventb.core.prover.rules.ProofRule;

public class BasicTactics {
	
	public static ITactic prune(){
		return new Prune();
	}
	
	public static ITactic onAllPending(ITactic t){
		return new OnAllPending(t);
	}
	
	public static ITactic onPending(int subgoalNo,ITactic t){
		return new OnPending(subgoalNo,t);
	}
	
	public static ITactic repeat(ITactic t){
		return new Repeat(t);
	}

	public static ITactic compose(ITactic ... tactics){
		return new Compose(tactics);
	}
	
	public static ITactic composeStrict(ITactic ... tactics){
		return new ComposeStrict(tactics);
	}
	
	public static ITactic pluginTac(IExternalReasoner plugin,IExtReasonerInput pluginInput){
		return new PluginTac(plugin,pluginInput);
	}
	
	public static ITactic ruleTac(ProofRule rule){
		return new RuleTac(rule);
	}
	
	private static class Prune implements ITactic {
	
		public Prune(){}
		
		public Object apply(IProofTreeNode pt){
			if (pt.isOpen()) return "Root is already open";
			pt.pruneChildren();
			return null;
		}
	}
	
	private static class RuleTac implements ITactic {
		
		private final ProofRule rule;
		
		public RuleTac(ProofRule rule)
		{
			this.rule = rule;
		}
		
		public Object apply(IProofTreeNode pt){
			if (!pt.isOpen()) return "Root already has children";
			if (pt.applyRule(this.rule)) return null;
			else return "Rule "+this.rule.getName()+" is not applicable";
			
		}
	}
	
	private static class PluginTac implements ITactic {
		
		private final IExternalReasoner plugin;
		private final IExtReasonerInput pluginInput;
		
		public PluginTac(IExternalReasoner plugin,IExtReasonerInput pluginInput)
		{
			this.plugin = plugin;
			this.pluginInput = pluginInput;
		}
		
		public Object apply(IProofTreeNode pt){
			if (!pt.isOpen()) return "Root already has children";
			IExtReasonerOutput po = this.plugin.apply(pt.getSequent(),pluginInput);
			if (po == null) return "! Plugin returned null !";
			if (!(po instanceof SuccessfullExtReasonerOutput)) return po;
			ProofRule plb = new PLb((SuccessfullExtReasonerOutput) po);
			ITactic temp = new RuleTac(plb);
			return temp.apply(pt);
		}
	}
		
	private static class OnAllPending implements ITactic {
		
		private ITactic t;
		
		public OnAllPending(ITactic t){
			this.t = t;
		}
		
		public Object apply(IProofTreeNode pt) {
			String applicable = "onAllPending unapplicable";
			IProofTreeNode[] subgoals = pt.getOpenDescendants();
			for(IProofTreeNode subgoal : subgoals){
				if (t.apply(subgoal) == null) applicable = null;
			}
			return applicable;
		}
	}
	
	
	private static class OnPending implements ITactic {
		
		private ITactic t;
		private int subgoalNo;
		
		public OnPending(int subgoalNo,ITactic t){
			this.t = t;
			this.subgoalNo = subgoalNo;
		}
		
		public Object apply(IProofTreeNode pt) {
			IProofTreeNode[] subgoals = pt.getOpenDescendants();
			if (this.subgoalNo < 0 || this.subgoalNo >= subgoals.length) 
				return "Subgoal "+this.subgoalNo+" non-existent";
			IProofTreeNode subgoal = subgoals[this.subgoalNo];
			if (subgoal == null) return "Subgoal "+this.subgoalNo+" is null!";
			return this.t.apply(subgoal);
		}
		
		
	}
		
	private static class Compose implements ITactic {

		private ITactic[] tactics;
		
		public Compose(ITactic ... tactics){
			this.tactics = tactics;
		}
		
		public Object apply(IProofTreeNode pt) {
			boolean applicable = false;
			Object lastFailure = "compose unapplicable: no tactics";
			for (ITactic tactic : tactics){
				Object tacticApp = tactic.apply(pt);
				if (tacticApp == null) applicable = true; 
				else lastFailure = tacticApp;
			}
			return applicable ? null : lastFailure;
		}

	}
	
	private static class ComposeStrict implements ITactic {

		private ITactic[] tactics;
		
		public ComposeStrict(ITactic ... tactics){
			this.tactics = tactics;
		}
		
		public Object apply(IProofTreeNode pt) {
			for (ITactic tactic : tactics){
				Object tacticApp = tactic.apply(pt);
				if (tacticApp != null) return tacticApp; 
			}
			return null;
		}

	}


	private static class Repeat implements ITactic {

		ITactic t;
		
		public Repeat(ITactic t){
			this.t = t;
		}
		
		public Object apply(IProofTreeNode pt) {
			boolean applicable = false;
			Object tacticApp = t.apply(pt);
			while(tacticApp == null){
				applicable = true;
				tacticApp = t.apply(pt);
			};
			return applicable ? null : tacticApp;
		}

	}
	
//	public static class conjE_auto implements Tactic{
//		
//		ProverPlugin conjE = new conjE();
//		
//		public boolean apply(ProofTree pt){
//			if (pt.rootHasChildren()) return false;
//			for (Predicate hyp : pt.getRootSeq().selectedHypotheses()){
//				if (lib.isConj(hyp)) {
//					Tactic t = new plugin(conjE,new conjE.Input(hyp));
//					return t.apply(pt);
//				}
//			}
//			return false;
//		}
//	}
	
}
