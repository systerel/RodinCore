package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;

public class Theorem extends InternalElement {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".theorem";

	public Theorem(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
