package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Expression;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IPRExprRef extends IInternalElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prExprRef"); //$NON-NLS-1$

	Expression getExpression(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException;
	void setExpression(Expression expr, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;		
}
