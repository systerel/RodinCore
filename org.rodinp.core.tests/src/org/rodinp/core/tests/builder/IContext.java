package org.rodinp.core.tests.builder;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public interface IContext extends IRodinFile {

	public static final IFileElementType ELEMENT_TYPE = 
		RodinCore.getFileElementType("org.rodinp.core.tests.context");
	
	ISCContext getCheckedVersion();

	IContext[] getUsedContexts() throws RodinDBException;

}