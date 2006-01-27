package org.rodinp.core.tests.builder.basis;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.tests.builder.IDependency;

public class Dependency extends InternalElement implements IDependency {
	
	public Dependency(String name, IRodinElement parent) {
		super(name, parent);
	}

	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
