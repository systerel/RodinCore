package org.eventb.internal.pp.core.provers.equality.unionfind;

// for facts inequalities and queries only
public class Equality<T extends Source> {
	
	private T source;
	private Node left,right;
	
	public Equality(Node left, Node right, T source) {
		this.right = right;
		this.left = left;
		this.source = source;
	}
	
	public T getSource() {
		return source;
	}

	// node on which it is attached
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
}
