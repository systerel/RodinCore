/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.equality.unionfind;

// for facts inequalities and queries only
public final class Equality<T extends Source> {
	
	private final T source;
	private final Node left,right;
	
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
	
	@Override
	public String toString() {
		return "<"+left+","+right+">";
	}
	
}
