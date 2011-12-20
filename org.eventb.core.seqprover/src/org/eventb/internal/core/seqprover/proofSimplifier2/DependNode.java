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
import org.eventb.core.seqprover.IProofTreeNode;

/**
 * A proof tree node type to use for dependence computation and manipulation.
 * <p>
 * Instances of this class are compared using == operator.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class DependNode {


	private final DependRule rule;

	private final RequiredSequent requiredSequent;

	private final ProducedSequent[] producedSequents;

	// the boolean is set to true when the node is deleted
	private boolean deleted = false;

	public DependNode(IProofTreeNode node) {
		this.rule = new DependRule(node.getRule());
		this.rule.init(node.getSequent());
		this.requiredSequent = this.rule.makeRequiredSequent(this);
		this.producedSequents = this.rule.makeProducedSequents(this);
	}

	public RequiredSequent getRequiredSequent() {
		return requiredSequent;
	}

	public ProducedSequent[] getProducedSequents() {
		return producedSequents;
	}

	// delete this node if one of the produced sequents has no dependents
	// leaf nodes are considered useful and are not deleted
	// a leaf node gets deleted only when a required ancestor is deleted
	public void deleteIfUnneeded() {
		for (ProducedSequent produced : producedSequents) {
			if (!produced.hasDependents()) {
				delete();
				return;
			}
		}
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void delete() {
		if (deleted) {
			return;
		}
		// mark deleted (before propagating)
		deleted = true;

		// propagate upwards
		requiredSequent.propagateDelete();

		// propagate downwards
		for (ProducedSequent produced : producedSequents) {
			produced.propagateDelete();
		}
	}

	public IProofRule getRule() {
		return rule.toProofRule();
	}
	
	public void compressRule() {
		rule.compressHypActions(producedSequents);
	}
}
