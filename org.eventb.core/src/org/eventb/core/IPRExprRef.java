package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IPRExprRef extends IInternalElement {

	IInternalElementType<IPRExprRef> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prExprRef"); //$NON-NLS-1$

	Expression[] getExpressions(IProofStoreReader store) throws RodinDBException;
	void setExpressions(Expression[] expr, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;		
}
