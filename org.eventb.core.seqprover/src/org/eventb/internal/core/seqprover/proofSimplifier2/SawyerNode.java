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

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * @author Nicolas Beauger
 *
 */
public class SawyerNode extends DependNode<DependRule> implements IProofSkeleton {

	public static SawyerNode fromTreeNode(IProofTreeNode proofNode) {
		final IProofTreeNode[] childNodes = proofNode.getChildNodes();
		final SawyerNode[] children = new SawyerNode[childNodes.length];

		for (int i = 0; i < childNodes.length; i++) {
			children[i] = fromTreeNode(childNodes[i]);
		}
		
		return new SawyerNode(proofNode.getRule(), children,
				proofNode.getComment());
	}

	// the rule of this node
	private final IProofRule rule;
	
	// children in the original proof tree
	// no dependence is assumed between parents and children
	private final SawyerNode[] children;

	// parent in the original proof tree
	// only one of children or parent can be final
	private SawyerNode parent = null;

	private final String comment;

	
	public SawyerNode(IProofRule rule, SawyerNode[] children, String comment) {
		this.rule = rule;
		this.children = children;
		this.comment = comment;
		setChildrenParent();
	}
	
	// not intended to be called outside constructor
	private void setChildrenParent() {
		for (SawyerNode child : children) {
			child.parent = this;
		}
	}

	public boolean isClosingNode() {
		return children.length == 0;
	}

	public SawyerNode getParent() {
		return parent;
	}
	
	public SawyerNode[] getChildren() {
		return children;
	}
	
	@Override
	public IProofSkeleton[] getChildNodes() {
		return children;
	}

	@Override
	public IProofRule getRule() {
		return rule;
	}

	@Override
	public String getComment() {
		return comment;
	}
}
