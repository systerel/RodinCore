/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * @author Nicolas Beauger
 * 
 */
public class SawyerNode extends DependNode implements IProofSkeleton {

	public static SawyerNode fromTreeNode(IProofTreeNode proofNode) {
		final IProofTreeNode[] childNodes = proofNode.getChildNodes();
		final SawyerNode[] children = new SawyerNode[childNodes.length];

		for (int i = 0; i < childNodes.length; i++) {
			children[i] = fromTreeNode(childNodes[i]);
		}

		return new SawyerNode(proofNode.getRule(), children,
				proofNode.getComment());
	}

	// children in the original proof tree
	// no dependence is assumed between parents and children
	private final SawyerNode[] children;

	// parent in the original proof tree
	// only one of children or parent can be final
	private SawyerNode parent = null;

	private final String comment;

	private SawyerNode(IProofRule rule, SawyerNode[] children, String comment) {
		super(rule);
		this.children = children;
		this.comment = comment;
		for (SawyerNode child : this.children) {
			child.parent = this;
		}
	}

	public boolean isClosingNode() {
		return children.length == 0;
	}

	public SawyerNode getParent() {
		return parent;
	}

	// original children
	public SawyerNode[] getChildren() {
		return children;
	}

	public SawyerNode saw() {
		for (int i = 0; i < children.length; i++) {
			children[i] = children[i].saw();
		}
		if (isDeleted()) {
			return getShortestChild();
		} else {
			return this;
		}
	}

	private SawyerNode getShortestChild() {
		switch (children.length) {
		case 0:
			return null;
		case 1:
			return children[0];
		}
		int shortestLength = Integer.MAX_VALUE;
		SawyerNode shortest = null;
		for (SawyerNode child : children) {
			if (child == null) {
				continue;
			}
			int childLength = child.getSize();
			if (childLength < shortestLength) {
				shortestLength = childLength;
				shortest = child;
			}
		}
		Assert.isNotNull(shortest, "one child at least should be not null");
		return shortest;
	}

	public int getSize() {
		int length = isDeleted() ? 0 : 1; // this node
		for (SawyerNode child : children) {
			length += child.getSize();
		}
		return length;
	}

	// simplified children
	@Override
	public IProofSkeleton[] getChildNodes() {
		// FIXME non deleted children
		return children;
	}

	@Override
	public String getComment() {
		return comment;
	}
}
