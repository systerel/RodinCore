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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElementType;
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
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	// TODO : cleanup
	public IProofRule getRule(FormulaFactory factory, ITypeEnvironment typEnv, IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] rules =  this.getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
		if (rules.length == 0) return null;
		assert rules.length == 1;
		return ((IPRProofRule) rules[0]).getProofRule(factory, typEnv, monitor);
	}
	
	//	TODO : cleanup
	public IPRProofTreeNode[] getChildNodes() throws RodinDBException {
		IRodinElement[] rodinElements =  this.getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		IPRProofTreeNode[] proofTreeNodes = new IPRProofTreeNode[rodinElements.length];
		// Do the cast
		for (int i = 0; i < proofTreeNodes.length; i++) {
			proofTreeNodes[i] = (IPRProofTreeNode) rodinElements[i];
		}
		return proofTreeNodes;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#setComment(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		CommonAttributesUtil.setComment(this, comment, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.ICommentedElement#getComment(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getComment(IProgressMonitor monitor) throws RodinDBException {
		return CommonAttributesUtil.getComment(this, monitor);
	}
	
	public IProofSkeleton getSkeleton(FormulaFactory factory, ITypeEnvironment typEnv, final IProgressMonitor monitor) throws RodinDBException {
		final String comment = getComment(null);
		final IProofRule proofRule = getRule(factory, typEnv, monitor);
		IAntecedent[] antecedents = proofRule.getAntecedents();
		final IPRProofTreeNode[] prChildNodes = getChildNodes();
		final IProofSkeleton[] childNodes = new IProofSkeleton[prChildNodes.length];
		for (int i = 0; i < childNodes.length; i++) {
			FreeIdentifier[] addedIdents = antecedents[i].getAddedFreeIdents();
			// TODO : the following may be optimised if typeenvs allow overwritting 
			if (addedIdents.length == 0)
				childNodes[i] = prChildNodes[i].getSkeleton(factory, typEnv, monitor);
			else
			{
				final ITypeEnvironment newTypEnv = typEnv.clone();
				newTypEnv.addAll(addedIdents);
				childNodes[i] = prChildNodes[i].getSkeleton(factory, newTypEnv, monitor);
			}
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

	public void setProofTreeNode(IProofTreeNode proofTreeNode, IProgressMonitor monitor) throws RodinDBException {
		
		IPRProofTreeNode prProofTreeNode = this;
		
		prProofTreeNode .setComment(proofTreeNode.getComment(),null);
		
		if (proofTreeNode.isOpen()) return;
				
		IPRProofRule prRule = (IPRProofRule)
		createInternalElement(
				IPRProofRule.ELEMENT_TYPE,
				proofTreeNode.getRule().generatedBy().getReasonerID(),
				null,null);
		
		prRule.setProofRule(proofTreeNode.getRule(), monitor);
		
		IProofTreeNode[] proofTreeNodeChildren = proofTreeNode.getChildNodes();
		for (int i = 0; i < proofTreeNodeChildren.length; i++) {
			IPRProofTreeNode child = (IPRProofTreeNode)
			createInternalElement(IPRProofTreeNode.ELEMENT_TYPE,Integer.toString(i),null,null);
			child.setProofTreeNode(proofTreeNodeChildren[i], monitor);
		}
	}
	
}
