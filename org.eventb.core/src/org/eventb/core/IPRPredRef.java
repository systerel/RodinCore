package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IPRPredRef extends IInternalElement {

	IInternalElementType<IPRPredRef> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prPredRef"); //$NON-NLS-1$

	Predicate[] getPredicates(IProofStoreReader store) throws RodinDBException;
	void setPredicates(Predicate[] preds, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;		
}
