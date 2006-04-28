package org.eventb.internal.ui.eventbeditor;

import org.rodinp.core.IRodinElement;

public class Leaf {

	private IRodinElement element;
	
	public Leaf(IRodinElement element) {
		this.element = element;
	}
	
	public IRodinElement getElement() {return element;}
	
	public void setElement(IRodinElement element) {
		this.element = element;
	}
}
