package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofTreeNode extends IInternalElement, ICommentedElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofTreeNode"); //$NON-NLS-1$
	
	// TODO : Make this and ProofTreeNode resemble each other.

	IPRProofTreeNode[] getChildNodes() throws RodinDBException;

	IProofRule getRule(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException;

	IProofSkeleton getSkeleton(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException;

	void setProofTreeNode(IProofTreeNode proofTreeNode, IProgressMonitor monitor) throws RodinDBException;
}
