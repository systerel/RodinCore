package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;

import org.rodinp.core.IRodinElement;

public class Node extends Leaf {

	Collection<Leaf> children;
		
	public Node(IRodinElement element) {
		super(element);
		children = new ArrayList<Leaf>();
	}
	
	public void addChildren(Leaf child) {children.add(child);}
	
	public void removeAllChildren() {
		children = new ArrayList<Leaf>();
	}
	
	public Leaf [] getChildren() {
		return (Leaf []) children.toArray(new Leaf[children.size()]);
	}
	
}
