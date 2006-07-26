package org.eventb.core;

import org.eventb.core.prover.IProofDependencies;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Proof Trees stored in the RODIN Database.
 * 
 * <p>
 * The class implementing this interface is responsable for serializing and 
 * de-serializing proof trees used inside the sequent prover.
 * </p>
 * <p>
 * This interface tries to mimic ({@link org.eventb.core.prover.IProofTree}) 
 * as much as possible.
 * </p>
 * 
 * 
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @author Farhad Mehta
 *
 */

public interface IPRProofTree extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prProofTree"; //$NON-NLS-1$
		
		
		/**
		 * Returns the PR sequent associated this proof tree.
		 * <p>
		 * It is also possible for a proof tree to not be associated to a PR sequent.
		 * </p>
		 * 
		 * @return the PR sequent associated with this proof tree, or <code>null</code>
		 * if no such PR sequent present
		 */
		IPRSequent getSequent();
		
		/**
		 * Returns whether or not this proof tree is closed.
		 * <p>
		 * This is a shortcut for <code>getConfidence() != IConfidence.PENDING</code>.
		 * </p>
		 * 
		 * @return <code>true</code> iff this proof tree is closed
		 * @throws RodinDBException 
		 */
		boolean isClosed() throws RodinDBException;
		
		/**
		 * Returns the root node of this proof tree.
		 * 
		 * @return the root node of this proof tree
		 */
		public IPRProofTreeNode getRoot() throws RodinDBException;
		
		
		public void initialize() throws RodinDBException;

		public boolean proofAttempted() throws RodinDBException;

		/**
		 * Returns the dependency information for this proof tree.
		 * (see {@link IProofDependencies})
		 * 
		 * @return the dependency information for this proof tree.
		 * 
		 * @throws RodinDBException
		 */
		IProofDependencies getProofDependencies() throws RodinDBException;
		
		/**
		 * Returns the confidence of this proof tree.
		 * 
		 * @return the confidence of this proof tree 
		 * 			(see {@link org.eventb.core.prover.IConfidence})
		 * @throws RodinDBException 
		 */
		int getConfidence() throws RodinDBException;
		
		
}
