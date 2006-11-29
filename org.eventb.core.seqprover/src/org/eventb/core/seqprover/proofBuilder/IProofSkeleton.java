package org.eventb.core.seqprover.proofBuilder;

import org.eventb.core.seqprover.IProofRule;

/**
 * Common protocol for a proof skeleton.
 * 
 *
 * <p>
 * This interface is intended to be implemented by clients that wish to persist
 * or copy proofs or subproofs.
 * </p>
 * 
 * @author Farhad Mehta
 */
public interface IProofSkeleton {

	/**
	 * Returns the children of this node.
	 * <p>
	 * This method always returns an array, even if this node is a leaf node
	 * (that is with no rule applied to it). It never returns <code>null</code>.
	 * </p>
	 * 
	 * @return an array of the children of this node
	 */
	IProofSkeleton[] getChildNodes();

	/**
	 * Returns the rule applied to this node.
	 * 
	 * @return the rule applied to this node or <code>null</code> is this node
	 *         is a leaf node
	 */
	IProofRule getRule();

	/**
	 * Returns the comment field of this node.
	 * 
	 * @return the comment associated to this node
	 */
	String getComment();
	
}