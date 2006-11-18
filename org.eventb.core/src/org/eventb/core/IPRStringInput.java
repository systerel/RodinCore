package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IPRStringInput extends IInternalElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prStrInp"); //$NON-NLS-1$

	String getStrInp() throws RodinDBException;
	void setStrInp(String value,IProgressMonitor monitor) throws RodinDBException;		
}
