package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
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


	public void initialize(IProofMonitor monitor) throws RodinDBException;

	public void setProofTree(IProofTree proofTree, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the confidence of this proof tree.
	 * @param monitor TODO
	 * 
	 * @return the confidence of this proof tree 
	 * 			(see {@link org.eventb.core.seqprover.IConfidence})
	 * @throws RodinDBException 
	 */
	int getConfidence(IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the dependency information for this proof tree.
	 * (see {@link IProofDependencies})
	 * 
	 * @return the dependency information for this proof tree.
	 * 
	 * @throws RodinDBException
	 */
	IProofDependencies getProofDependencies() throws RodinDBException;

	IProofSkeleton getSkeleton(IProgressMonitor monitor) throws RodinDBException;

}
