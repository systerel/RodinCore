package org.rodinp.core.tests.builder.basis;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.UnnamedInternalElement;
import org.rodinp.core.tests.builder.IData;

public class Data extends UnnamedInternalElement implements IData {
	
	public Data(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

}
