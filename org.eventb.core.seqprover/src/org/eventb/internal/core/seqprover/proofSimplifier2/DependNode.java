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
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * A proof tree node type to use for dependence computation and manipulation.
 * <p>
 * Instances of this class are compared using == operator.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class DependNode {

	private final IProofRule rule;

	private final RequiredSequent requiredSequent;

	private final ProducedSequent[] producedSequents;

	// the boolean is set to true when the node is deleted
	private boolean deleted = false;

	public DependNode(IProofRule rule) {
		this.rule = rule;
		this.requiredSequent = new RequiredSequent(rule, this);

		final IAntecedent[] antecedents = rule.getAntecedents();
		this.producedSequents = new ProducedSequent[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {
			producedSequents[i] = new ProducedSequent(antecedents[i], this);
		}
	}

	public RequiredSequent getRequiredSequent() {
		return requiredSequent;
	}

	public ProducedSequent[] getProducedSequents() {
		return producedSequents;
	}
	
	// delete this node if one of the produced sequents has no dependents
	public void deleteIfUnneeded() {
		for (ProducedSequent produced : producedSequents) {
			produced.deleteNodeIfNoDependents();
			if (deleted) {
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
		requiredSequent.delete();

		// propagate downwards
		for (ProducedSequent produced : producedSequents) {
			produced.delete();
		}
	}

	public IProofRule getRule() {
		return rule;
	}
}
