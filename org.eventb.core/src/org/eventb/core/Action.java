package org.eventb.core;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;

public class Action extends InternalElement {
	
	public final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".action";

	public Action(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

}
