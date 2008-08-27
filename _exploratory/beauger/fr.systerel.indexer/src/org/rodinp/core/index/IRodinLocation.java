package org.rodinp.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

// TODO: decide whether to get rid of that interface
public interface IRodinLocation {

	int NULL_CHAR_POS = -1;
	
	IRodinElement getElement();
	
	IAttributeType getAttributeType();
	
	int getCharStart();
	
	int getCharEnd();
	
}
