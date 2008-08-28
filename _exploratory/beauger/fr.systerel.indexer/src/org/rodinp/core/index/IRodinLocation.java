package org.rodinp.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

public interface IRodinLocation {

	int NULL_CHAR_POS = -1;
	
	IRodinElement getElement();
	
	IAttributeType getAttributeType();
	
	int getCharStart();
	
	int getCharEnd();
	
}
