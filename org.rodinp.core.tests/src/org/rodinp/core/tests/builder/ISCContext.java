package org.rodinp.core.tests.builder;


public interface ISCContext extends ISCProvable {

	public static final String ELEMENT_TYPE = "org.rodinp.core.tests.scContext";
	
	IContext getUncheckedVersion();

}