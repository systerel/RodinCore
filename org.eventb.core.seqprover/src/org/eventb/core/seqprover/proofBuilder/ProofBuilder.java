package org.eventb.core.seqprover.proofBuilder;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.internal.core.seqprover.ProofTreeNode;

/**
 * This class contains static methods that can be used to build (or re-build)
 * a proof tree (actually proof tree nodes) from a proof skeleton.
 * 
 * @see IProofSkeleton
 * @see ProofTreeNode
 * 
 * @author Farhad Mehta
 *
 */
public class ProofBuilder {

	/**
	 *  Singeleton class; Should not be instantiated.
	 */
	private ProofBuilder() {
	}

	/**
	 * A method that recursively rebuilds proof tree nodes using a proof skeleton and replay hints.
	 * 
	 * <p>
	 * If no replay hints are present, this method first tries to reuse the proof rules in the proof
	 * skeleton to rebuild proof nodes. If this fails, it resorts to replaying the reasoner to generate a new proof rule that to use.
	 * </p>
	 * <p>
	 * In case there are some replay hints, these hints are applied to the reasoner input and the reasoners are replayed.  
	 * </p>
	 * <p>
	 * If this proof tree node was successfully rebuilt, new replay hints are generated and this method is recursively called.
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where rebiulding should start
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param replayHints
	 * 			The replay hints to use
	 * @return
	 */
	public static boolean rebuild(IProofTreeNode node,IProofSkeleton skeleton, ReplayHints replayHints) {
		// TODO:
		// Return value true if there may be a change in the proof tree node from the

		node.setComment(skeleton.getComment());

		IProofRule reuseProofRule = skeleton.getRule();
		
		// Check if this is an open node
		if (reuseProofRule == null) return true;
		
		// Try to replay the rule
		if (true){
			
			IReasoner reasoner = reuseProofRule.generatedBy();
			// uninstalled reasoner
			assert reasoner != null;
			
			IReasonerInput reasonerInput = reuseProofRule.generatedUsing();
			
			// choose between reuse and replay
			boolean reuseSuccessfull = false;
			boolean replaySuccessfull = false;
			// if there are replay hints do not even try a reuse
			if (replayHints.isEmpty())
			{
				// see if reuse works
//				Object error = BasicTactics.reasonerTac(proofRule).apply(node);
//				reuseSuccessfull = (error == null);
				reuseSuccessfull = node.applyRule(reuseProofRule);
				
			}
			
			IProofRule replayProofRule = null;
			
			if (! reuseSuccessfull)
			{	// reuse failed
				// try replay
				replayHints.applyHints(reasonerInput);
				IReasonerOutput replayReasonerOutput = reasoner.apply(node.getSequent(),reasonerInput, null);
				if ((replayReasonerOutput != null) && 
						((replayReasonerOutput instanceof IProofRule))){
					// reasoner successfully generated something
					replayProofRule = (IProofRule) replayReasonerOutput;
					replaySuccessfull = node.applyRule(replayProofRule);
				}
			}	
			
			// Check if rebuild for this node was succesfull
			if (!(reuseSuccessfull || replaySuccessfull)) return false;
			IProofSkeleton[] prChildren = skeleton.getChildNodes();
			assert prChildren != null;
			IProofTreeNode[] children = node.getChildNodes();
			
			// Maybe check if the node has the same number of children as the prNode
			// it may be smart to replay anyway, but generate a warning.
			if (children.length != prChildren.length) return false;
			
			// run recursively for each child
			for (int i = 0; i < children.length; i++) {
				ReplayHints newReplayHints = replayHints;
				if (replayProofRule != null)
				{
					// generate hints for continuing the proof
					newReplayHints = replayHints.clone();
					newReplayHints.addHints(reuseProofRule.getAntecedents()[i],replayProofRule.getAntecedents()[i]);
				}
				rebuild(children[i],prChildren[i],newReplayHints);
			}
		}
		return false;
	}

	/**
	 * A method that recursively rebuilds proof tree nodes using a proof skeleton and no replay hints.
	 *
	 * <p>
	 * This method calls the more general <code>rebuild()</code> method with initially empty replay hints.
	 * </p>
	 * 
	 * @param node
	 * 			The open proof tree node where rebiulding should start
	 * @param skeleton
	 * 			The proof skeleton to use
	 * @param replayHints
	 * 			The replay hints to use
	 * @return
	 */
	public static boolean rebuild(IProofTreeNode node, IProofSkeleton skeleton) {
		return rebuild(node,skeleton,new ReplayHints());
	}
	
}
