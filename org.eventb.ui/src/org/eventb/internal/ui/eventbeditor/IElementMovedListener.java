package org.eventb.internal.ui.eventbeditor;

import java.util.HashMap;

import org.rodinp.core.IRodinElement;

public interface IElementMovedListener {

	public void elementMoved(HashMap<IRodinElement, IRodinElement> moved);
	
}
