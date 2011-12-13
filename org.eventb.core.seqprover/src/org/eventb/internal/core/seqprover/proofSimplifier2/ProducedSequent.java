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

import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProducedSequent extends NodeSequent {

	private final List<RequiredSequent> dependents = new ArrayList<RequiredSequent>();
	
	public ProducedSequent(IAntecedent antecedent, DependNode node) {
		super(antecedent.getAddedHyps(), antecedent.getGoal(), node);
	}

	public void addDependentSequent(RequiredSequent dependent) {
		if (!dependents.contains(dependent)) {
			dependents.add(dependent);
		}
	}
	
	@Override
	protected void propagateDelete() {
		for (RequiredSequent dependent : dependents) {
			dependent.delete();
		}
	}
	
	public void deleteDependent(RequiredSequent dependent) {
		dependents.remove(dependent);
		deleteNodeIfNoDependents();
	}
	
	public void deleteNodeIfNoDependents() {
		if (dependents.isEmpty()) {
			getNode().delete();
		}
	}
}
