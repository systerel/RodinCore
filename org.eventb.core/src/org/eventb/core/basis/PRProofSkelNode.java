/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRProofSkelNode;
import org.eventb.core.IProofStoreCollector;
import org.eventb.core.IProofStoreReader;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofSkelNode extends EventBProofElement implements IPRProofSkelNode {

	public PRProofSkelNode(String name,IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IProofSkeleton getSkeleton(IProofStoreReader store, SubProgressMonitor monitor) throws RodinDBException {
		final String comment = getCommentChecked(null);
		final IProofRule proofRule = getRule(store, monitor);
		final IRodinElement[] prChildNodes = getChildrenOfType(IPRProofSkelNode.ELEMENT_TYPE);
		final IProofSkeleton[] childNodes = new IProofSkeleton[prChildNodes.length];
		for (int i = 0; i < childNodes.length; i++) {
			childNodes[i] = ((IPRProofSkelNode) prChildNodes[i]).getSkeleton(store, monitor);
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


	private IProofRule getRule(IProofStoreReader store, SubProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] rules =  getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
		if (rules.length == 0) return null;
		return ((IPRProofRule) rules[0]).getProofRule(store, monitor);
	}

	public void setSkeleton(IProofSkeleton skel, IProofStoreCollector store,IProgressMonitor monitor) throws RodinDBException {
		
		setCommentChecked(skel.getComment(),null);
		
		if (skel.getRule() == null) return;
				
		IPRProofRule prRule = (IPRProofRule)
		createInternalElement(
				IPRProofRule.ELEMENT_TYPE,
				skel.getRule().generatedBy().getReasonerID(),
				null,null);
		
		prRule.setProofRule(skel.getRule(), store, monitor);
		
		IProofSkeleton[] skelChildren = skel.getChildNodes();
		for (int i = 0; i < skelChildren.length; i++) {
			IPRProofSkelNode child = (IPRProofSkelNode)
			createInternalElement(IPRProofSkelNode.ELEMENT_TYPE,Integer.toString(i),null,null);
			child.setSkeleton(skelChildren[i], store, monitor);
		}
	}
	
	public void setCommentChecked(String comment, IProgressMonitor monitor)
	throws RodinDBException {
		if (comment.length() != 0)
		setAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, comment, monitor);
	}
	
	public String getCommentChecked(IProgressMonitor monitor) throws RodinDBException {
		if (hasComment(monitor))
			return getAttributeValue(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
		else
			return "";
	}
	
}
