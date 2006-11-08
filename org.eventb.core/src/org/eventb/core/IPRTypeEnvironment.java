package org.eventb.core;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRTypeEnvironment extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prTypeEnv"); //$NON-NLS-1$
		
	ITypeEnvironment getTypeEnvironment() throws RodinDBException;
	FreeIdentifier[] getFreeIdentifiers() throws RodinDBException;

	void setTypeEnvironment(ITypeEnvironment typeEnv) throws RodinDBException;
	void setTypeEnvironment(FreeIdentifier[] freeIdents) throws RodinDBException;
}
