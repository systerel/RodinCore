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

import static org.eventb.internal.core.seqprover.proofSimplifier2.DependSequent.fromAntecedent;
import static org.eventb.internal.core.seqprover.proofSimplifier2.DependSequent.fromRule;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;

/**
 * A proof rule type to use for dependence computation and manipulation.
 * 
 * @author Nicolas Beauger
 */
public class DependRule {

	// the proof rule of this node
	private final IProofRule rule;

	private final DependSequent neededSequent;

	private final DependSequent[] producedSequents;

	public DependRule(IProofRule rule) {
		this.rule = rule;
		this.neededSequent = fromRule(rule);

		final IAntecedent[] antecedents = rule.getAntecedents();
		this.producedSequents = new DependSequent[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {
			producedSequents[i] = fromAntecedent(antecedents[i]);
		}
	}

	public IProofRule getRule() {
		return rule;
	}

	public DependSequent getNeededSequent() {
		return neededSequent;
	}

	public DependSequent[] getProducedSequents() {
		return producedSequents;
	}

}
