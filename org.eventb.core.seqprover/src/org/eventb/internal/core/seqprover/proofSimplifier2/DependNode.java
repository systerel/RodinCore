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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eventb.internal.core.seqprover.ProofTreeNode;

/**
 * A proof tree node type to use for dependence computation and manipulation.
 * <p>
 * Instances of this class are compared using == operator.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class DependNode {

	// children in the original proof tree
	// no dependence is assumed between parents and children
	private final DependNode[] children;

	private final DependSequent neededSequent;
	
	private final DependSequent[] producedSequents;
	
	// nodes on which this node depends
	private final Collection<DependNode> neededNodes = new ArrayList<DependNode>();

	// nodes that depend on this node
	private final Collection<DependNode> dependentNodes = new ArrayList<DependNode>();

	// the boolean is set to true when the node is deleted
	private boolean isDeleted = false;

	public DependNode(ProofTreeNode proofNode) {
		this.children = null;
		this.neededSequent = null;
		this.producedSequents = null;
	}

	public boolean isClosingNode() {
		return children.length == 0;
	}
	
	public boolean isDeleted() {
		return isDeleted;
	}
	
	public void dependentDeleted(DependNode node) {
		Assert.isTrue(!isClosingNode(), "A closing node cannot have dependent nodes");
		dependentNodes.remove(node);
		if (dependentNodes.isEmpty()) {
			delete();
		}
	}
	
	public void delete() {
		if (isDeleted) {
			return;
		}
		// mark deleted (before propagating)
		isDeleted = true;

		// propagate upwards
		for (DependNode needed : neededNodes) {
			needed.dependentDeleted(this);
		}
		// propagate downwards
		for (DependNode dependent : dependentNodes) {
			dependent.delete();
		}
	}
	
	
}
