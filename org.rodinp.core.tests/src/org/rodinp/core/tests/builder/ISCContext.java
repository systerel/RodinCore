package org.rodinp.core.tests.builder;

import org.rodinp.core.IFileElementType;
import org.rodinp.core.RodinCore;


public interface ISCContext extends ISCProvable {

	public static final IFileElementType<ISCContext> ELEMENT_TYPE = 
		RodinCore.getFileElementType("org.rodinp.core.tests.scContext");
	
	IContext getUncheckedVersion();

}