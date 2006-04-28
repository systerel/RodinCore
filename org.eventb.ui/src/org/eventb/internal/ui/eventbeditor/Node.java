package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;

import org.rodinp.core.IRodinElement;

public class Node extends Leaf {

	Collection<Leaf> children;
	boolean explored;
		
	public Node(IRodinElement element) {
		super(element);
		children = new ArrayList<Leaf>();
		explored = false;
	}
	
	public void addChildren(Leaf child) {children.add(child);}
	
	public Leaf [] getChildren() {
		return (Leaf []) children.toArray(new Leaf[children.size()]);
	}
	
	public boolean isExplored() {return explored;}
	
	public void setExplored() {explored = true;}
}
