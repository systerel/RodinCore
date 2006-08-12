package org.rodinp.core.tests.builder.basis;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.builder.IData;

public class Data extends InternalElement implements IData {
	
	public Data(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
