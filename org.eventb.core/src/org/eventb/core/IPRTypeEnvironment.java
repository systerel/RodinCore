package org.eventb.core;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRTypeEnvironment extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prTypeEnv"; //$NON-NLS-1$
		
		public ITypeEnvironment getTypeEnvironment() throws RodinDBException;
		public FreeIdentifier[] getFreeIdentifiers() throws RodinDBException;
		
		public void setTypeEnvironment(ITypeEnvironment typeEnv) throws RodinDBException;
		public void setTypeEnvironment(FreeIdentifier[] freeIdents) throws RodinDBException;
}
