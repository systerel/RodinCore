package org.rodinp.core.tests.builder;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public interface IContext extends IRodinFile {

	public static final String ELEMENT_TYPE = "org.rodinp.core.tests.context";
	
	ISCContext getCheckedVersion();

	IContext[] getUsedContexts() throws RodinDBException;

}