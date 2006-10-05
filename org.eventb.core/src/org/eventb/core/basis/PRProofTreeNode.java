/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofTreeNode extends InternalElement implements IPRProofTreeNode {

	public PRProofTreeNode(String name,IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
//	public IPRProofRule getPRRule() throws RodinDBException {
//		IRodinElement[] rules =  this.getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
//		if (rules.length == 0) return null;
//		assert rules.length == 1;
//		return (IPRProofRule) rules[0];
//	}
	
	public IProofRule getRule() throws RodinDBException {
		IRodinElement[] rules =  this.getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
		if (rules.length == 0) return null;
		assert rules.length == 1;
		return ((IPRProofRule) rules[0]).getProofRule();
	}

	public IPRProofTreeNode[] getChildNodes() throws RodinDBException {
		IRodinElement[] rodinElements =  this.getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		IPRProofTreeNode[] proofTreeNodes = new IPRProofTreeNode[rodinElements.length];
		// Do the cast
		for (int i = 0; i < proofTreeNodes.length; i++) {
			proofTreeNodes[i] = (IPRProofTreeNode) rodinElements[i];
		}
		return proofTreeNodes;
	}
	
	public IPRProofTreeNode[] getChildNodesRR() throws RodinDBException {
		return null;
	}
	
	public String getComment() throws RodinDBException {
		return getContents();
	}
	
	public String getComment(IProgressMonitor monitor) throws RodinDBException {
		return getContents(monitor);
	}
	
	public void setComment(String comment) throws RodinDBException {
		setContents(comment);
	}
	
	public IProofSkeleton getSkeleton(final IProgressMonitor monitor) throws RodinDBException {
		final String comment = getComment();
		final IProofRule proofRule = getRule();
		final IPRProofTreeNode[] prChildNodes = getChildNodes();
		final IProofSkeleton[] childNodes = new IProofSkeleton[prChildNodes.length];
		for (int i = 0; i < childNodes.length; i++) {
			childNodes[i] = prChildNodes[i].getSkeleton(monitor);
		}
		
		// if (monitor.isCanceled()) throw new OperationCanceledException();
		
		IProofSkeleton skeleton =
			new IProofSkeleton(){

				public IProofSkeleton[] getChildNodes() {
					return childNodes;
				}

				public IProofRule getRule() {
					return proofRule;
				}

				public String getComment() {
					return comment;
				}
			
		};
		return skeleton;
	}

	public void setProofTreeNode(IProofTreeNode proofTreeNode) throws RodinDBException {
		
		IPRProofTreeNode prProofTreeNode = this;
		
		prProofTreeNode .setComment(proofTreeNode.getComment());
		
		if (proofTreeNode.isOpen()) return;
		
		// writeOutRule(proofTreeNode.getRule(),prProofTreeNode);
		
		IPRProofRule prRule = (IPRProofRule)
		createInternalElement(
				IPRProofRule.ELEMENT_TYPE,
				proofTreeNode.getRule().generatedBy().getReasonerID(),
				null,null);
		
		prRule.setProofRule(proofTreeNode.getRule());
		
		IProofTreeNode[] proofTreeNodeChildren = proofTreeNode.getChildNodes();
		for (int i = 0; i < proofTreeNodeChildren.length; i++) {
			IPRProofTreeNode child = (IPRProofTreeNode)
			createInternalElement(IPRProofTreeNode.ELEMENT_TYPE,Integer.toString(i),null,null);
			child.setProofTreeNode(proofTreeNodeChildren[i]);
		}
	}
	
}
