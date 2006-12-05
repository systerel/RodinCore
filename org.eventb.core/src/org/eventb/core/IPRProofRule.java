package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofRule extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prRule"); //$NON-NLS-1$

	IProofSkeleton getProofSkeleton(IProofStoreReader store, String comment) throws RodinDBException;

	void setProofRule(IProofSkeleton rule, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns a handle to the antecedent child with the given name.
	 * <p>
	 * This is a handle-only method. The antecedent element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param name
	 *            name of the child
	 * 
	 * @return a handle to the child antecedent with the given name
	 * @see #getAntecedents()
	 */
	IPRRuleAntecedent getAntecedent(String name);

	/**
	 * Returns all children antecedent elements.
	 * 
	 * @return an array of all chidren element of type antecedent
	 * @throws RodinDBException
	 * @see #getAntecedent(String)
	 */
	IPRRuleAntecedent[] getAntecedents() throws RodinDBException;

}
