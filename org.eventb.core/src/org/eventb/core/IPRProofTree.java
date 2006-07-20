package org.eventb.core;

import org.eventb.core.prover.IProofDependencies;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 * @author Farhad Mehta
 *
 */

public interface IPRProofTree extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prProofTree"; //$NON-NLS-1$
		
		
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
