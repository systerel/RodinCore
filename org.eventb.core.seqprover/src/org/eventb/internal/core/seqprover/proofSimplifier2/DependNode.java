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

	private final RequiredSequent requiredSeqs;
	
	private final ProducedSequent[] producedSeqs;
	
	// the boolean is set to true when the node is deleted
	private boolean deleted = false;


	public DependNode(IProofRule rule) {
		this.rule = rule;
		this.requiredSeqs = new RequiredSequent(rule, this);

		final IAntecedent[] antecedents = rule.getAntecedents();
		this.producedSeqs = new ProducedSequent[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {
			producedSeqs[i] = new ProducedSequent(antecedents[i], this);
		}
	}
	
	// delete this node if one of the produced sequents has no dependents
	public void deleteIfUnneeded() {
		for (ProducedSequent produced : producedSeqs) {
			produced.deleteNodeIfNoDependents();
			if (deleted) {
				return;
			}
		}
	}
	
	public void delete() {
		if (deleted) {
			return;
		}
		// mark deleted (before propagating)
		deleted = true;

		// propagate upwards
		requiredSeqs.delete();

		// propagate downwards
		for (ProducedSequent produced : producedSeqs) {
			produced.delete();
		}
	}

	public IProofRule getRule() {
		return rule;
	}
}
