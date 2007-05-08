package org.eventb.internal.pp.core.provers.equality.unionfind;

public class RootInfo<T extends Source> {

	private Node inequalNode;
	private Equality<T> equality;
	
	
	public RootInfo(Node inequalNode, Equality<T> equality) {
		this.inequalNode = inequalNode;
		this.equality = equality;
	}
	
	private void update() {
		while (!inequalNode.isRoot()) {
			inequalNode = inequalNode.getParent();
		}
	}
	
	public Node updateAndGetInequalNode() {
		update();
		return inequalNode;
	}
	
	public Equality<T> getEquality() {
		return equality;
	}

	public T getSource() {
		return equality.getSource();
	}
	
}
