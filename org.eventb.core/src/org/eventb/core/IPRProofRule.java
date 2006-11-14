package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProofRule;
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
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".proofRule"); //$NON-NLS-1$
		
	//@Deprecated
	// public String getRuleID() throws RodinDBException;

	IProofRule getProofRule(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException;

	void setProofRule(IProofRule rule, IProgressMonitor monitor) throws RodinDBException;	
}
