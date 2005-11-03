package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;

public class Guard extends InternalElement {
	
	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".guard";

	public Guard(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
