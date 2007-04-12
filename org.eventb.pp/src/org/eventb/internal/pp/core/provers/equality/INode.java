package org.eventb.internal.pp.core.provers.equality;


// each constant has a corresponding node information 
// TODO -> each constant is same object
public interface INode {

	public int getIdentifier();

	public INode getParent();
	
	
}
