/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import static org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException.checkCancel;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException;

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
		return new SawyerNode(proofNode, children, proofNode.getComment());
	}

	// children in the original proof tree
	// no dependence is assumed between parents and children
	private final SawyerNode[] children;

	private final String comment;

	private SawyerNode(IProofTreeNode node, SawyerNode[] children,
			String comment) {
		super(node);
		this.children = children;
		this.comment = comment;
	}

	// original children
	public SawyerNode[] getChildren() {
		return children;
	}

	/**
	 * Recursively stick parents to children together for the subtree rooted at
	 * this node, avoiding deleted nodes.
	 * 
	 * @param monitor
	 * 
	 * @return <ul>
	 *         <li><code>null</code> if the subtree rooted at this node is
	 *         entirely deleted,
	 *         <li>a sticked child if this node is deleted
	 *         <li>or this node with sticked children
	 *         </ul>
	 * @throws CancelException
	 *             when the given monitor is cancelled
	 */
	public SawyerNode stickTogether(IProofMonitor monitor)
			throws CancelException {
		checkCancel(monitor);
		for (int i = 0; i < children.length; i++) {
			children[i] = children[i].stickTogether(monitor);
		}
		if (isDeleted()) {
			return getShortestChild(monitor);
		} else {
			return this;
		}
	}

	private SawyerNode getShortestChild(IProofMonitor monitor)
			throws CancelException {
		switch (children.length) {
		case 0:
			return null;
		case 1:
			return children[0];
		}
		int smallestSize = Integer.MAX_VALUE;
		SawyerNode smallest = null;
		for (SawyerNode child : children) {
			checkCancel(monitor);
			if (child == null) {
				continue;
			}
			final int childSize = child.getSize();
			if (childSize < smallestSize) {
				smallestSize = childSize;
				smallest = child;
			}
		}
		// null iff the whole subtree is deleted
		return smallest;
	}

	public int getSize() {
		int length = 1; // this node
		for (SawyerNode child : children) {
			length += child.getSize();
		}
		return length;
	}

	// simplified children
	@Override
	public IProofSkeleton[] getChildNodes() {
		return children;
	}

	@Override
	public String getComment() {
		return comment;
	}
}
