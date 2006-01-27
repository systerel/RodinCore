package org.rodinp.core.tests;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.InternalElement;

public class NamedElement extends InternalElement {
	
	public static final String ELEMENT_TYPE = "org.rodinp.core.tests.namedElement";

	public NamedElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
