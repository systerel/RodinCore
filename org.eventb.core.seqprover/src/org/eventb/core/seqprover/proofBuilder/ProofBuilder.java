package org.eventb.core.seqprover.proofBuilder;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;

public class ProofBuilder {

	private ProofBuilder() {
	}

	// TODO:
	// Return value true if there may be a change in the proof tree node from the
	public static boolean rebuild(IProofTreeNode node,IProofSkeleton skeleton, ReplayHints replayHints) {

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
					// BasicTactics.reasonerTac(replayProofRule).apply(node);
				}
				
				// BasicTactics.reasonerTac(reasoner,reasonerInput).apply(node);
			}	
			
			// Check if rebuild for this node was succesfull
			if (!(reuseSuccessfull || replaySuccessfull)) return false;
			// if (! node.hasChildren()) return;
			// System.out.println("rebuild successful! ");
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
					newReplayHints.addHints(reuseProofRule.getAnticidents()[i],replayProofRule.getAnticidents()[i]);
				}
				rebuild(children[i],prChildren[i],newReplayHints);
			}
		}
		return false;
	}
	
}
