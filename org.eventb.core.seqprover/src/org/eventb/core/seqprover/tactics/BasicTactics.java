package org.eventb.core.seqprover.tactics;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;

/**
 * This class contains static methods that return basic tactics.
 * 
 * <p>
 * The tactics returned by the methods of this class are either:
 * <ul>
 * <li> Primitive Tactics : Simple tactics that, for instance, correspond to calling a
 * reasoner, applying a single proof rule, or pruning a proof tree.
 * <li> Tactic Constructors : Used to construct more complex tactics by
 * combining other tactics (i.e. repetition, sequential composition, etc).
 * <li> Proof Reconstruction Tactics : Encapsulate proof reconstruction methods.
 * </ul>
 * </p>
 * 
 * <p>
 * These tactics are independant of Event-B extensions.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class BasicTactics {
	
	/**
	 * Primitive Tactics
	 */

	
	/**
	 * Returns a tactic that prunes the proof tree node where it is applied.
	 * 
	 * @return A tactic that prunes the proof tree node where it is applied.
	 */
	public static ITactic prune(){
		return new ITactic(){
			public Object apply(IProofTreeNode pt, IProofMonitor pm){
				if (pt.isOpen()) return "Root is already open";
				pt.pruneChildren();
				return null;
			}	
		};
	}

	
	/**
	 * Encapsulates a reasoner call into a tactic.
	 * 
	 * @param reasoner
	 * 			The reasoner to call
	 * @param reasonerInput
	 * 			The reasoner input to use
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic reasonerTac(final IReasoner reasoner,
			final IReasonerInput reasonerInput) {
		return new ITactic(){
	
			public Object apply(IProofTreeNode pt, IProofMonitor pm){
				if (!pt.isOpen()) return "Root already has children";
				IReasonerOutput reasonerOutput = 
					reasoner.apply(pt.getSequent(), reasonerInput, pm);
				if (reasonerOutput == null) return "! Plugin returned null !";
				if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
				IProofRule rule = (IProofRule)reasonerOutput;
				if (pt.applyRule(rule)) return null;
				else return "Rule "+rule.getDisplayName()+" is not applicable";
			}
		};
	}
	
	/**
	 * Encapsulates the application of a proof rule into a tactic.
	 * 
	 * @param rule
	 * 			The proof rule
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic ruleTac(final IProofRule rule){
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt, IProofMonitor pm){
				if (!pt.isOpen()) return "Root already has children";
				if (pt.applyRule(rule)) return null;
				else return "Rule "+rule.getDisplayName()+" is not applicable";
			}
		};
	}
	
	/**
	 * Returns a tactic that always fails on application with the given
	 * message.
	 * 
	 * @param message
	 * 			The message to use
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic failTac(final String message){
		return new ITactic(){
			public Object apply(IProofTreeNode pt, IProofMonitor pm){
				return message;
			}
		};
	}
	
	/**
	 * Tactic Constructors
	 */

	/**
	 * Returns a tactic, when applied, applies the given tactic on all pending nodes
	 * of a proof tree.
	 * 
	 * @param tactic
	 * 			The given tactic
	 * @return
	 * 			A tactic, when applied, applies the given tactic on all pending nodes
	 * 			of a proof tree.
	 */
	public static ITactic onAllPending(final ITactic tactic){
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				String applicable = "onAllPending unapplicable";
				IProofTreeNode[] subgoals = pt.getOpenDescendants();
				for(IProofTreeNode subgoal : subgoals){
					if (tactic.apply(subgoal, pm) == null) applicable = null;
				}
				return applicable;
			}
		};
	}
	

	/**
	 * Returns a tactic, when applied, applies the given tactic on the specified pending nodes
	 * of a proof tree.
	 * 
	 * @param pendingIndex
	 * 			The index of the pending node. The first pending subgoal has index 0.
	 * @param tactic
	 * 			The given tactic
	 * @return
	 * 			A tactic, when applied, applies the given tactic on the given pending nodes
	 * 			of a proof tree.
	 */
	public static ITactic onPending(final int pendingIndex,final ITactic tactic){
		return new ITactic(){

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				IProofTreeNode[] subgoals = pt.getOpenDescendants();
				if (pendingIndex < 0 || pendingIndex >= subgoals.length) 
					return "Subgoal "+ pendingIndex +" non-existent";
				IProofTreeNode subgoal = subgoals[pendingIndex];
				if (subgoal == null) return "Subgoal "+ pendingIndex +" is null!";
				return tactic.apply(subgoal, pm);
			}
		};
	}
	
	
	/**
	 * Returns a tactic, when applied, applies the given tactic repeatedly 
	 * (i.e. until it is no longer applicable) on a proof tree.
	 * 
	 * @param tactic
	 * 			The given tactic
	 * @return
	 * 			The resulting repeated tactic.
	 */
	public static ITactic repeat(final ITactic tactic){
		return new ITactic(){
			
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				boolean applicable = false;
				Object tacticApp = tactic.apply(pt, pm);
				while(tacticApp == null){
					applicable = true;
					tacticApp = tactic.apply(pt, pm);
				};
				return applicable ? null : tacticApp;
			}
		};
	}

	/**
	 * Composes a sequence of tactics.
	 * 
	 * <p>
	 * Applying the resulting tactic applies ALL given tactics in the given order,
	 * irrespective of whether they succeed or fail.
	 * </p>
	 * <p>
	 * The resulting tactic succeeds iff all tactics succeed.
	 * </p>
	 * 
	 * @param tactics
	 * 			Array of tactics to compose
	 * @return
	 * 			The resulting tactic.
	 */
	public static ITactic compose(final ITactic ... tactics){
		return new ITactic(){
	
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
		};
	}
	
	/**
	 * Strict composition of a sequence of tactics.
	 * 
	 * <p>
	 * Applying the resulting tactic applies the given tactics in their given order,
	 * until a tactic fails.
	 * </p>
	 * <p>
	 * The resulting tactic succeeds iff all tactics succeed.
	 * </p>
	 * 
	 * @param tactics
	 * 			Array of tactics to compose
	 * @return
	 * 			The resulting tactic.
	 */
	public static ITactic composeStrict(final ITactic ... tactics){
		return new ITactic(){
	
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				for (ITactic tactic : tactics){
					Object tacticApp = tactic.apply(pt, pm);
					if (tacticApp != null) return tacticApp; 
				}
				return null;
			}
		};
	}
	

	/**
	 * Proof Reconstruction Tactics
	 * 
	 */
	
	
	/**
	 * Encapsulates the proof reuse method into a tactic.
	 * 
	 * @param proofSkeleton
	 * 			The proof skeleton to use
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic reuseTac(final IProofSkeleton proofSkeleton){
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				if (!pt.isOpen()) return "Root already has children";
				boolean success = ProofBuilder.reuse(pt,proofSkeleton, pm);
				if (success) return null;
				return "Reuse unsuccessful";
			}
		};
	}

	/**
	 * Encapsulates the proof replay method into a tactic.
	 * 
	 * @param proofSkeleton
	 * 			The proof skeleton to use
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic replayTac(final IProofSkeleton proofSkeleton){
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				if (!pt.isOpen()) return "Root already has children";
				boolean success = ProofBuilder.replay(pt,proofSkeleton, pm);
				if (success) return null;
				return "Replay unsuccessful";
			}
		};
	}

	/**
	 * Encapsulates the proof rebuild method into a tactic.
	 * 
	 * @param proofSkeleton
	 * 			The proof skeleton to use
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic rebuildTac(final IProofSkeleton proofSkeleton){
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				if (!pt.isOpen()) return "Root already has children";
				boolean success = ProofBuilder.rebuild(pt,proofSkeleton, pm);
				if (success) return null;
				return "Rebuild unsuccessful";
			}
		};
	}
	
	
	@Deprecated
	public static ITactic pasteTac(IProofTreeNode toPaste){
		return new PasteTac(toPaste);
	}	
	

		
	@Deprecated
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
	
}
