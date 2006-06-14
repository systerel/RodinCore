/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRProofRule;
import org.eventb.core.IPRProofTreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRProofTreeNode extends UnnamedInternalElement implements IPRProofTreeNode {

	public PRProofTreeNode(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}
	
	public IPRProofRule getRule() throws RodinDBException {
		IRodinElement[] rules =  this.getChildrenOfType(IPRProofRule.ELEMENT_TYPE);
		if (rules.length == 0) return null;
		assert rules.length == 1;
		return (IPRProofRule) rules[0];
	}

	public IPRProofTreeNode[] getChildProofTreeNodes() throws RodinDBException {
		if (this.getRule() == null) return null;
		IRodinElement[] rodinElements =  this.getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		IPRProofTreeNode[] proofTreeNodes = new IPRProofTreeNode[rodinElements.length];
		// Do the cast
		for (int i = 0; i < proofTreeNodes.length; i++) {
			proofTreeNodes[i] = (IPRProofTreeNode) rodinElements[i];
		}
		return proofTreeNodes;
	}

}
