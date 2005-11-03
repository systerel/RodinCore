package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;

public class Axiom extends InternalElement {
	
	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".axiom";

	public Axiom(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
