package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B Proof elements stored in the RODIN Database.
 * 
 * <p>
 * This is intended (at least for the moment) to be the top-most interface
 * for serializing and deserializing proof data structures. This means that
 * it is not intended that database elements beyond this point be seen or
 * manipulated. Note also that this interface provides a more abstract view
 * of the database by hiding the internal database representation.
 * </p>
 * <p>
 * This interface is meant to be used as follows. An instance of IProofTree
 * may be serialised into the database using the {@link #setProofTree(IProofTree, IProgressMonitor)}
 * method. This serializes the proof skeleton ({@link IProofSkeleton}) and proof 
 * dependencies {@link IProofDependencies} of this proof tree which may later be
 * deserialized using the {@link #getSkeleton(FormulaFactory, IProgressMonitor)} and
 * {@link #getProofDependencies(FormulaFactory, IProgressMonitor)} methods. 
 * </p>
 * <p>
 * The proof dependencies are used to check if the proof is applicable to any proof 
 * obligation. The proof skeleton can be used to rebuild the broof tree.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients under any 
 * circumstances.
 * </p>
 *
 * @see IProofTree
 * @see IProofDependencies
 * @see IProofSkeleton
 * @see ProofBuilder
 *
 * @author Farhad Mehta
 *
 */
public interface IPRProof extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProof"); //$NON-NLS-1$

	/**
	 * Sets the proof tree of this proof element by serializing the given proof tree
	 * into the database.
	 * 
	 * @param proofTree
	 * 			The proof tree to set
	 * @param monitor
	 * 			  a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 */
	public void setProofTree(IProofTree proofTree, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the confidence of proof tree stored in this proof element.
	 * <p>
	 * Note that this method returns <code>IConfidence.UNATTEMPTED</code>
	 * if no proof is currently stored in this proof element. 
	 * </p>
	 *
	 * 
	 * @return the confidence of this proof tree 
	 * 
	 * @throws RodinDBException 
	 */
	int getConfidence() throws RodinDBException;
	
	
	/**
	 * Returns the proof dependencies for proof tree stored in this proof element.
	 * 
	 * <p>
	 * In case no proof tree is stored in this proof element, this method returns the
	 * broadest result (i.e. with no dependencies)
	 * </p>
	 * 
	 * @param factory
	 * 				The formula factory to be used
	 * @param monitor TODO
	 * @return
	 * 		The proof dependencies for proof tree stored in this proof element
	 * @throws RodinDBException
	 */
	IProofDependencies getProofDependencies(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException;

	
	/**
	 * Returns the proof skeleton of the proof tree stored in this proof element.
	 * 
	 * @param factory
	 * 			The formula factory to be used
	 * @param monitor
	 * 			  a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return
	 * 			the stored proof skeleton
	 * 
	 * @throws RodinDBException
	 */
	IProofSkeleton getSkeleton(FormulaFactory factory, IProgressMonitor monitor) throws RodinDBException;

}
