package org.eventb.core.seqprover.tactics;

import java.util.Arrays;
import java.util.LinkedList;

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
 * These tactics are independent of Event-B extensions.
 * </p>
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
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
	
	//************************************************************************
	// Note : 	repeat(onAllPending(t)) is not the same as
	// 			onAllPending(repeat(t))
	//
	//	The first version applies t to leave nodes only.
	//	The second one may apply t to an internal node in case it adds some 
	//	nodes the first time around.
	//
	// Similarly : 	compose(onAllPending(t)) is not the same as
	//				onAllPending(compose(t))
	//
	// In general, tactics that need to be applied only to open nodes should be
	// wrapped by a onAllPending.
	//*************************************************************************
	

	/**
	 * Composes a sequence of tactics.
	 * 
	 * <p>
	 * Applying the resulting tactic applies ALL given tactics in the given order,
	 * irrespective of whether they succeed or fail.
	 * </p>
	 * <p>
	 * The resulting tactic succeeds iff at least one tactics succeeded.
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
	 * Composes a sequence of tactics that are intended to be applied on open proof tree nodes.
	 * 
	 * <p>
	 * The behaviour of the constructed tactic is identical to <code>compose(onAllPending(t1)... onAllPending(tn))</code>.
	 * </p>
	 * 
	 * <p>
	 * Applying the resulting tactic applies ALL given tactics on ALL pending proof tree nodes, in the given order,
	 * irrespective of whether they succeed or fail.
	 * </p>
	 * <p>
	 * The resulting tactic succeeds iff at least one tactic succeeded.
	 * </p>
	 * 
	 * @param tactics
	 * 			Array of tactics to compose
	 * @return
	 * 			The resulting tactic.
	 */
	public static ITactic composeOnAllPending(final ITactic ... tactics){
		return new ITactic(){
	
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				boolean applicable = false;
				Object lastFailure = "compose unapplicable: no tactics";
				for (ITactic tactic : tactics){
					Object tacticApp = onAllPending(tactic).apply(pt, pm);
					if (tacticApp == null) applicable = true; 
					else lastFailure = tacticApp;
				}
				return applicable ? null : lastFailure;
			}
		};
	}

	/**
	 * Loops a sequence of tactics that are intended to be applied on open proof tree nodes.
	 * 
	 * <p>
	 * The behaviour of the constructed tactic is identical to <code>repeat(onAllPending(composeUntilSuccess(t1..tn)))</code>
	 * but more efficient.
	 * </p>
	 * 
	 * <p>
	 * Applying the resulting tactic applies ALL given tactics on ALL pending proof tree nodes, in the given order,
	 * irrespective of whether they succeed or fail.
	 * </p>
	 * <p>
	 * The resulting tactic application succeeds iff at least one tactic application succeeded.
	 * </p>
	 * 
	 * @param tactics
	 * 			Array of tactics to compose
	 * @return
	 * 			The resulting tactic.
	 */
	public static ITactic loopOnAllPending(final ITactic ... tactics){
		// return repeat(onAllPending(composeUntilSuccess(tactics)));
		return new ITactic(){

			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				boolean modified = false;
				
				LinkedList<IProofTreeNode> nodes = new LinkedList<IProofTreeNode>(Arrays.asList(ptNode.getOpenDescendants()));
				
				while (! nodes.isEmpty()) {
					IProofTreeNode node = nodes.removeFirst();
					for (ITactic tactic : tactics) {
						tactic.apply(node, pm);
						if (! node.isOpen())
						{
							// tactic made some progress on node
							modified = true;
							nodes.addAll(Arrays.asList(node.getOpenDescendants()));
							break;
						}
					}
				}
				
				if (modified){
					return null;
				} else
				{
					return "loopOnAllPending: All tactics failed";
				}
			}
		};
		
	}
	
	/**
	 * Composition of a sequence of tactics till failure
	 * 
	 * <p>
	 * Applying the resulting tactic applies the given tactics in their given
	 * order on the first open descendant of the input node, until a tactic
	 * fails.
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
	public static ITactic composeUntilFailure(final ITactic ... tactics){
		return new ITactic(){
	
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				for (ITactic tactic : tactics){
					final IProofTreeNode open = pt.getFirstOpenDescendant();
					final Object tacticApp = tactic.apply(open, pm);
					if (tacticApp != null) return tacticApp; 
				}
				return null;
			}
		};
	}

	/**
	 * Composition of a sequence of tactics till success.
	 * 
	 * <p>
	 * Applying the resulting tactic applies the given tactics in their given order,
	 * until a tactic succeeds.
	 * </p>
	 * <p>
	 * The resulting tactic fails iff all tactics failed.
	 * </p>
	 * 
	 * @param tactics
	 * 			Array of tactics to compose
	 * @return
	 * 			The resulting tactic.
	 */
	public static ITactic composeUntilSuccess(final ITactic ... tactics){
		return new ITactic(){
	
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				for (ITactic tactic : tactics){
					Object tacticApp = tactic.apply(pt, pm);
					if (tacticApp == null) return null; 
				}
				return "All composed tactics failed";
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
	
	
	/**
	 * Tactic that takes a reference to a (source) proof tree node and tries to 
	 * reuse the proof from that node for the (target) node the tactic is applied to.
	 * 
	 * @param toPaste
	 * 		A reference to the (source) node to use.
	 * @return
	 * 		The resulting tactic
	 * 
	 * @deprecated
	 * A problem with using references to (source) proof nodes for reuse is that the
	 * proof trees rooted at them may change over time. This is not something one expects
	 * with the copy/paste procedure. Instead use {@link #reuseTac(IProofSkeleton)} after 
	 * first extracting a copy of the (source) proof tree node using the <code>copyProofSkeleton()</code>
	 * method in {@link IProofTreeNode}.
	 * 
	 */
	@Deprecated
	public static ITactic pasteTac(IProofTreeNode toPaste){
		return new PasteTac(toPaste);
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
	
}
