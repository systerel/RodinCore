package org.eventb.core.seqprover.tactics;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

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
	
	
	public static ITactic reasonerTac(IReasoner reasoner,
			IReasonerInput reasonerInput) {
		return new ReasonerTac(reasoner,reasonerInput);
	}
	
	public static ITactic reasonerTac(IProofRule reasonerOutput){
		return new ReuseTac(reasonerOutput);
	}
	
//	public static ITactic ruleTac(ProofRule rule){
//		return new RuleTac(rule);
//	}
	
	public static ITactic pasteTac(IProofTreeNode toPaste){
		return new PasteTac(toPaste);
	}
	
	public static ITactic rebuildTac(final IProofSkeleton proofSkeleton){
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				if (!pt.isOpen()) return "Root already has children";
				ProofBuilder.rebuild(pt,proofSkeleton,new ReplayHints());
				if (!pt.isOpen()) return null;
				return "Rebuild unsuccessful";
			}
			
		};
	}
	
	public static ITactic failTac(String message){
		return new FailTac(message);
	}
	
	private static class Prune implements ITactic {
	
		public Prune(){}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm){
			if (pt.isOpen()) return "Root is already open";
			pt.pruneChildren();
			return null;
		}
	}
	
	private static class FailTac implements ITactic {
		
		private final String message;
		
		public FailTac(String message){
			this.message = message;
		}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm){
			return message;
		}
	}
	
//	private static class RuleTac implements ITactic {
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
//			else return "Rule "+this.rule.getDisplayName()+" is not applicable";
//			
//		}
//	}
	
	private static class ReasonerTac implements ITactic {
		
		private final IReasoner reasoner;
		private final IReasonerInput reasonerInput;
		
		public ReasonerTac(IReasoner reasoner,IReasonerInput reasonerInput)
		{
			this.reasoner = reasoner;
			this.reasonerInput = reasonerInput;
		}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm){
			if (!pt.isOpen()) return "Root already has children";
			IReasonerOutput reasonerOutput = 
				reasoner.apply(pt.getSequent(), reasonerInput, pm);
			if (reasonerOutput == null) return "! Plugin returned null !";
			if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
			ITactic temp = new ReuseTac((IProofRule)reasonerOutput);
			return temp.apply(pt, pm);
			
//			ProofRule reasonerStep = new ReasoningStep((ReasonerOutputSucc) reasonerOutput);
//			ITactic temp = new RuleTac(reasonerStep);
//			return temp.apply(pt);
		}
	}
	
	private static class ReuseTac implements ITactic {
		
		private final IProofRule reasonerOutput;
		
		public ReuseTac(IProofRule reasonerOutput)
		{
			this.reasonerOutput = reasonerOutput;
		}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm){
			if (!pt.isOpen()) return "Root already has children";
			if (pt.applyRule(reasonerOutput)) return null;
			else return "Rule "+reasonerOutput.getDisplayName()+" is not applicable";
			
//			ProofRule reasonerStep = new ReasoningStep(reasonerOutput);
//			ITactic temp = new RuleTac(reasonerStep);
//			return temp.apply(pt);
		}
	}
	
	private static class PasteTac implements ITactic {
		
		private final IProofTreeNode toPaste;
		
		public PasteTac(IProofTreeNode proofTreeNode)
		{
			this.toPaste = proofTreeNode;
		}

		// TODO improve implementation of apply that creates new tactics recursively!
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm){
			if (!pt.isOpen()) return "Root already has children";
			IProofRule rule = toPaste.getRule();
			if (rule == null) return null;
			Boolean successfull = pt.applyRule(rule);
			if (successfull)
			{
				IProofTreeNode[] ptChildren = pt.getChildNodes();
				IProofTreeNode[] toPasteChildren = toPaste.getChildNodes();
				if (ptChildren.length != toPasteChildren.length) 
					return "Paste unsuccessful";
				Object error = null;
				for (int i = 0; i < toPasteChildren.length; i++) {
					final Object pasteResult = 
						pasteTac(toPasteChildren[i]).apply(ptChildren[i], pm);
					if (pasteResult != null)
						error = "Paste unsuccessful";
				}
				return error;
			}
			else return "Paste unsuccessful";
		}
	}
		
	private static class OnAllPending implements ITactic {
		
		private ITactic t;
		
		public OnAllPending(ITactic t){
			this.t = t;
		}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm) {
			String applicable = "onAllPending unapplicable";
			IProofTreeNode[] subgoals = pt.getOpenDescendants();
			for(IProofTreeNode subgoal : subgoals){
				if (t.apply(subgoal, pm) == null) applicable = null;
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
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm) {
			IProofTreeNode[] subgoals = pt.getOpenDescendants();
			if (this.subgoalNo < 0 || this.subgoalNo >= subgoals.length) 
				return "Subgoal "+this.subgoalNo+" non-existent";
			IProofTreeNode subgoal = subgoals[this.subgoalNo];
			if (subgoal == null) return "Subgoal "+this.subgoalNo+" is null!";
			return this.t.apply(subgoal, pm);
		}
		
		
	}
		
	private static class Compose implements ITactic {

		private ITactic[] tactics;
		
		public Compose(ITactic ... tactics){
			this.tactics = tactics;
		}
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm) {
			boolean applicable = false;
			Object lastFailure = "compose unapplicable: no tactics";
			for (ITactic tactic : tactics){
				Object tacticApp = tactic.apply(pt, pm);
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
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm) {
			for (ITactic tactic : tactics){
				Object tacticApp = tactic.apply(pt, pm);
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
		
		public Object apply(IProofTreeNode pt, IProofMonitor pm) {
			boolean applicable = false;
			Object tacticApp = t.apply(pt, pm);
			while(tacticApp == null){
				applicable = true;
				tacticApp = t.apply(pt, pm);
			};
			return applicable ? null : tacticApp;
		}

	}
	
}
