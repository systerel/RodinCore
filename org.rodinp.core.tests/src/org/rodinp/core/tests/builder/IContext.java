package org.rodinp.core.tests.builder;

import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public interface IContext extends IRodinFile {

	ISCContext getCheckedVersion();

	IContext[] getUsedContexts() throws RodinDBException;

}