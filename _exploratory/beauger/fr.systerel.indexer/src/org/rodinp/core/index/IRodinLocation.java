package org.rodinp.core.index;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public interface IRodinLocation {

	int NULL_CHAR_POS = -1;
	
	IRodinFile getRodinFile();
	
	IRodinElement getElement();
	
	IAttributeType getAttributeType();
	
	int getCharStart();
	
	int getCharEnd();
	
}
