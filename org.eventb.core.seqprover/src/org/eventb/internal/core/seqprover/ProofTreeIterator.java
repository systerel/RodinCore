/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.Iterator;

import org.eventb.core.seqprover.IProofTreeNode;

public class ProofTreeIterator implements Iterator<IProofTreeNode> {

	private IProofTreeNode original;
	
	private IProofTreeNode currentNode;
	
	private IProofTreeNode nextNode; 
	
	public ProofTreeIterator(IProofTreeNode original, boolean rootIncluded) {
		this.original = original;
		currentNode = null;
		if (rootIncluded)
			nextNode = original;
		else {
			nextNode = getNextNode(original);
		}
	}
	
	private IProofTreeNode getNextNode(IProofTreeNode node) {
		if (node.hasChildren())
			return node.getChildNodes()[0]; // Return the first child

		return getNextSibling(node);
	}

	private IProofTreeNode getNextSibling(IProofTreeNode node) {
		IProofTreeNode parent = node.getParent();
		if (parent != null) {
			IProofTreeNode[] childNodes = parent.getChildNodes();
			if (childNodes[childNodes.length-1].equals(node)) {
				return getNextSibling(parent);
			}
			else {
				boolean found = false;
				IProofTreeNode result = null;
				for (IProofTreeNode childNode : childNodes) {
					if (found) {
						result = childNode;
						break;
					}
					else if (childNode.equals(node)) {
						found = true;
					}
				}
				return result;
			}
		}

		else { // node is the root of the proof tree
			return node;
		}
	}

	public boolean hasNext() {
		if (nextNode == null)
			return false;
		return (currentNode == null || !nextNode.equals(original));
	}

	public IProofTreeNode next() {
		currentNode = nextNode;
		nextNode = getNextNode(nextNode);
		return currentNode;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
