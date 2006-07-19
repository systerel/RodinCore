/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.Set;

import org.eventb.core.IPRPredicate;
import org.eventb.core.IPRPredicateSet;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IPRTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 * TODO : remane to PRProofTree, resembel ProofTree
 *
 */
public class PRProofTree extends InternalElement implements IPRProofTree {

	public PRProofTree(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public Status getStatus() throws RodinDBException {
		if (getContents().compareToIgnoreCase("PENDING") == 0) return Status.PENDING;
		if (getContents().compareToIgnoreCase("REVIEWED") == 0) return Status.REVIEWED;
		if (getContents().compareToIgnoreCase("DISCHARGED") == 0) return Status.DISCHARGED;
		return null;
	}

	private void setStatus(Status status) throws RodinDBException {
		switch (status) {
		case PENDING:
			setContents("PENDING");
			break;
		case REVIEWED:
			setContents("REVIEWED");
			break;	
		case DISCHARGED:
			setContents("DISCHARGED");
			break;
		default:
			break;
		}
	}

	public PRProofTreeNode getRootProofTreeNode() throws RodinDBException {
		IRodinElement[] proofTreeNodes = getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		if (proofTreeNodes.length != 1) return null;
		return (PRProofTreeNode) proofTreeNodes[0];
	}

	public Set<Predicate> getUsedHypotheses() throws RodinDBException {
		IRodinElement[] usedHypotheses = getChildrenOfType(IPRPredicateSet.ELEMENT_TYPE);
		if (usedHypotheses.length != 1) return null;
		assert usedHypotheses.length == 1;
		assert usedHypotheses[0].getElementName().equals("usedHypotheses");
		return ((IPRPredicateSet)usedHypotheses[0]).getPredicateSet();
	}

//	public FreeIdentifier[] getUsedFreeIdents() throws RodinDBException {
//		IRodinElement[] usedFreeIdents = getChildrenOfType(IPRTypeEnvironment.ELEMENT_TYPE);
//		if (usedFreeIdents.length != 1) return null;
//		assert usedFreeIdents.length == 1;
//		assert usedFreeIdents[0].getElementName().equals("usedFreeIdentifiers");
//		return ((IPRTypeEnvironment)usedFreeIdents[0]).getFreeIdentifiers();
//	}

	public ITypeEnvironment getUsedTypeEnvironment() throws RodinDBException {
		InternalElement usedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"usedFreeIdentifiers");
		assert usedFreeIdents != null;
		if (! usedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)usedFreeIdents).getTypeEnvironment();
	}
	
	public ITypeEnvironment getIntroducedTypeEnvironment() throws RodinDBException {
		InternalElement introducedFreeIdents = getInternalElement(IPRTypeEnvironment.ELEMENT_TYPE,"introducedFreeIdentifiers");
		assert introducedFreeIdents != null;
		if (! introducedFreeIdents.exists()) return null;
		return ((IPRTypeEnvironment)introducedFreeIdents).getTypeEnvironment();
	}
	
	public Predicate getGoal() throws RodinDBException {
		InternalElement goal = getInternalElement(IPRPredicate.ELEMENT_TYPE,"goal");
		assert goal != null;
		if (! goal.exists()) return null;
		return ((IPRPredicate)goal).getPredicate();
	}
	
	
	public void initialize() throws RodinDBException {
		//delete previous children, if any.
		if (this.getChildren().length != 0)
			this.getRodinDB().delete(this.getChildren(),true,null);
		setStatus(Status.PENDING);
	}

	public boolean proofAttempted() throws RodinDBException {
		IRodinElement[] proofTreeNodes = getChildrenOfType(IPRProofTreeNode.ELEMENT_TYPE);
		if (proofTreeNodes.length == 0) return false;
		
		PRProofTreeNode rootProofTreeNode = getRootProofTreeNode();
		return !(rootProofTreeNode.getRule()==null && rootProofTreeNode.getComment().length() == 0);
	}
	
}
