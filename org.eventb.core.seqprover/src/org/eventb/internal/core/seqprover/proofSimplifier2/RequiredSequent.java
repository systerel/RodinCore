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
import java.util.List;

import org.eventb.core.seqprover.IProofRule;

/**
 * @author Nicolas Beauger
 * 
 */
public class RequiredSequent extends NodeSequent {

	private final List<ProducedSequent> neededSequents = new ArrayList<ProducedSequent>();
	
	public RequiredSequent(IProofRule rule, DependNode node) {
		super(rule.getNeededHyps(), rule.getGoal(), node);
	}

	public void addNeededSequent(ProducedSequent sequent) {
		if (!neededSequents.contains(sequent)) {
			neededSequents.add(sequent);
		}
	}
	
	public List<ProducedSequent> getNeededSequents() {
		return neededSequents;
	}

	@Override
	protected void propagateDelete() {
		for(ProducedSequent needed: neededSequents) {
			needed.deleteDependent(this);
		}
	}
	
}
