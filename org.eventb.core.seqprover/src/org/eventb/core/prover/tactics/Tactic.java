package org.eventb.core.prover.tactics;

import java.util.List;

import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.SuccessfullExtReasonerOutput;
import org.eventb.core.prover.rules.PLb;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.rules.Rule;

public interface Tactic {
	
	public abstract Object apply(ProofTree pt);
	
	public static class prune implements Tactic {
	
		public prune(){}
		
		public Object apply(ProofTree pt){
			if (pt.rootIsOpen()) return "Root is already open";
			pt.pruneChildren();
			return null;
		}
	}
	
	public static class RuleTac implements Tactic {
		
		private final Rule rule;
		
		public RuleTac(Rule rule)
		{
			this.rule = rule;
		}
		
		public Object apply(ProofTree pt){
			if (!pt.rootIsOpen()) return "Root already has children";
			if (pt.applyRule(this.rule)) return null;
			else return "Rule "+this.rule.name()+" is not applicable";
			
		}
	}
	
	public static class plugin implements Tactic {
		
		private final IExternalReasoner plugin;
		private final IExtReasonerInput pluginInput;
		
		public plugin(IExternalReasoner plugin,IExtReasonerInput pluginInput)
		{
			this.plugin = plugin;
			this.pluginInput = pluginInput;
		}
		
		public Object apply(ProofTree pt){
			if (!pt.rootIsOpen()) return "Root already has children";
			IExtReasonerOutput po = this.plugin.apply(pt.getRootSeq(),pluginInput);
			if (po == null) return "! Plugin returned null !";
			if (!(po instanceof SuccessfullExtReasonerOutput)) return po;
			Rule plb = new PLb((SuccessfullExtReasonerOutput) po);
			Tactic temp = new RuleTac(plb);
			return temp.apply(pt);
		}
	}
		
	public static class onAllPending implements Tactic {
		
		private Tactic t;
		
		public onAllPending(Tactic t){
			this.t = t;
		}
		
		public Object apply(ProofTree pt) {
			String applicable = "onAllPending unapplicable";
			List<ProofTree> subgoals = pt.pendingSubgoals();
			for(ProofTree subgoal : subgoals){
				if (t.apply(subgoal) == null) applicable = null;
			}
			return applicable;
		}
	}
	
	public static class onPending implements Tactic {
		
		private Tactic t;
		private int subgoalNo;
		
		public onPending(int subgoalNo,Tactic t){
			this.t = t;
			this.subgoalNo = subgoalNo;
		}
		
		public Object apply(ProofTree pt) {
			List<ProofTree> subgoals = pt.pendingSubgoals();
			if (this.subgoalNo < 0 || this.subgoalNo >= subgoals.size()) 
				return "Subgoal "+this.subgoalNo+" non-existent";
			ProofTree subgoal = subgoals.get(this.subgoalNo);
			if (subgoal == null) return "Subgoal "+this.subgoalNo+" is null!";
			return this.t.apply(subgoal);
		}
		
		
	}
		
	public static class compose implements Tactic {

		private Tactic[] tactics;
		
		public compose(Tactic ... tactics){
			this.tactics = tactics;
		}
		
		public Object apply(ProofTree pt) {
			boolean applicable = false;
			Object lastFailure = "compose unapplicable: no tactics";
			for (Tactic tactic : tactics){
				Object tacticApp = tactic.apply(pt);
				if (tacticApp == null) applicable = true; 
				else lastFailure = tacticApp;
			}
			return applicable ? null : lastFailure;
		}

	}
	
	public static class composeStrict implements Tactic {

		private Tactic[] tactics;
		
		public composeStrict(Tactic ... tactics){
			this.tactics = tactics;
		}
		
		public Object apply(ProofTree pt) {
			for (Tactic tactic : tactics){
				Object tacticApp = tactic.apply(pt);
				if (tacticApp != null) return tacticApp; 
			}
			return null;
		}

	}


	public class repeat implements Tactic {

		Tactic t;
		
		public repeat(Tactic t){
			this.t = t;
		}
		
		public Object apply(ProofTree pt) {
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
