package org.eventb.core;

import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Proof Trees stored in the RODIN Database.
 * 
 * <p>
 * The class implementing this interface is responsable for serializing and 
 * de-serializing proof trees used inside the sequent prover.
 * </p>
 * <p>
 * This interface tries to mimic ({@link org.eventb.core.seqprover.IProofTree}) 
 * as much as possible.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Farhad Mehta
 *
 */

public interface IPRProofTree extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofTree"); //$NON-NLS-1$

	/**
	 * Returns the confidence of this proof tree.
	 * 
	 * @return the confidence of this proof tree 
	 * 			(see {@link org.eventb.core.seqprover.IConfidence})
	 * @throws RodinDBException 
	 */
	int getConfidence() throws RodinDBException;

	/**
	 * Returns whether or not this proof tree is closed.
	 * <p>
	 * This is a shortcut for <code>getConfidence() <= IConfidence.PENDING</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this proof tree is closed
	 * @throws RodinDBException 
	 */
	boolean isClosed() throws RodinDBException;

	/**
	 * Returns whether or not this proof tree is attempted.
	 * <p>
	 * This is a shortcut for <code>getConfidence() != IConfidence.UNATTEMPTED</code>.
	 * </p>
	 * 
	 * @return <code>true</code> iff this proof has been attempted
	 * @throws RodinDBException 
	 */
	public boolean proofAttempted() throws RodinDBException;

	
	/**
	 * Returns the root node of this proof tree.
	 * 
	 * @return the root node of this proof tree
	 */
	public IPRProofTreeNode getRoot() throws RodinDBException;


	public void initialize() throws RodinDBException;

	public void setProofTree(IProofTree proofTree) throws RodinDBException;


	/**
	 * Returns the dependency information for this proof tree.
	 * (see {@link IProofDependencies})
	 * 
	 * @return the dependency information for this proof tree.
	 * 
	 * @throws RodinDBException
	 */
	IProofDependencies getProofDependencies() throws RodinDBException;


	boolean isAutomaticallyGenerated() throws RodinDBException;

	void setAutomaticallyGenerated() throws RodinDBException;

}
