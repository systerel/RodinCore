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
import org.eventb.core.IPRProofTreeNode;
import org.eventb.core.IProof;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class Proof extends InternalElement implements IProof {

	public Proof(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public Status getStatus() throws RodinDBException {
		if (getContents().compareToIgnoreCase("PENDING") == 0) return Status.PENDING;
		if (getContents().compareToIgnoreCase("DISCHARGED") == 0) return Status.DISCHARGED;
		return null;
	}

	private void setStatus(Status status) throws RodinDBException {
		switch (status) {
		case PENDING:
			setContents("PENDING");
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

	public Predicate getGoal() throws RodinDBException {
		IRodinElement[] goal = getChildrenOfType(IPRPredicate.ELEMENT_TYPE);
		if (goal.length != 1) return null;
		assert goal.length == 1;
		assert goal[0].getElementName().equals("goal");
		return ((IPRPredicate)goal[0]).getPredicate();
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
		return true;
	}
	
}
