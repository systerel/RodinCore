package org.rodinp.core.index;

import org.rodinp.core.IRodinElement;

// TODO: decide whether to get rid of that interface
public interface IRodinLocation {

	IRodinElement getElement();
	
	String getAttributeId();
	
	int getCharStart();
	
	int getCharEnd();
	
	public static final int NULL_CHAR_POS = -1;
	
}
