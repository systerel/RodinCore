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

/**
 * This class holds the information located at the root of a tree.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public final class RootInfo<T extends Source> {
	
	private Node inequalNode;
	private final Equality<T> equality;
	
	public RootInfo(Node inequalNode, Equality<T> equality) {
		this.inequalNode = inequalNode;
		this.equality = equality;
	}

	public Equality<T> getEquality() {
		return equality;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RootInfo) {
			RootInfo<?> temp = (RootInfo<?>) obj;
			return equality.equals(temp.equality);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return equality.hashCode();
	}
	
	@Override
	public String toString() {
		return equality.toString()/*+"{"+equality.getSource().toString()+"}"*/;
	}

}
