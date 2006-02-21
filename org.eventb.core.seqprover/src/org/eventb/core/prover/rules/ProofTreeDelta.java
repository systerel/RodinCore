/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.prover.rules;

import java.util.ArrayList;

import org.eventb.core.prover.IProofTreeDelta;
import org.eventb.core.prover.IProofTreeNode;

/**
 * Implementation of proof tree deltas.
 * 
 * @author Laurent Voisin
 */
public class ProofTreeDelta implements IProofTreeDelta {

	private ArrayList<ProofTreeDelta> children;

	private int flags;

	private final IProofTreeNode node;
	
	/**
	 * Creates a delta for the given node.
	 * @param node
	 *            node of a proof tree which has changed.
	 */
	public ProofTreeDelta(IProofTreeNode node) {
		this.node = node;
		this.flags = 0;
		this.children = new ArrayList<ProofTreeDelta>();
	}
	
	public ProofTreeDelta addChild(IProofTreeNode childNode) {
		assert childNode.getParent() == node;

		// Don't add this child if children have changed.
		if ((flags & CHILDREN) != 0)
			return null;
		
		// Do we already have a child delta for this node?
		for (ProofTreeDelta childDelta : children) {
			if (childDelta.node == childNode)
				return childDelta;
		}

		// Not found, so create a new delta.
		ProofTreeDelta childDelta = new ProofTreeDelta(childNode);
		children.add(childDelta);
		return childDelta;
	}
	
	public IProofTreeDelta[] getChildren() {
		IProofTreeDelta[] result = new IProofTreeDelta[children.size()];
		return children.toArray(result);
	}

	public int getFlags() {
		return flags;
	}

	public IProofTreeNode getProofTreeNode() {
		return node;
	}

	public void setChildrenChanged() {
		flags |= CHILDREN;
		children.clear();
	}

	public void setStatusChanged() {
		flags |= STATUS;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		toString(builder, "");
		return builder.toString();
	}

	private void toString(StringBuilder builder, String indent) {
		builder.append(indent);
		builder.append(node.getSequent().goal());
		builder.append(" [");
		boolean sep = toStringFlag(builder, STATUS, "STATUS", false);
		toStringFlag(builder, CHILDREN, "CHILDREN", sep);
		builder.append("]");
		String childIndent = indent + "  ";
		for (ProofTreeDelta child: children) {
			builder.append("\n");
			child.toString(builder, childIndent);
		}
	}

	private boolean toStringFlag(StringBuilder builder, int flagToTest,
			String flagName, boolean sep) {
		
		if ((flags & flagToTest) != 0) {
			if (sep) builder.append('|');
			builder.append(flagName);
			return true;
		}
		return false;
	}
	
}
