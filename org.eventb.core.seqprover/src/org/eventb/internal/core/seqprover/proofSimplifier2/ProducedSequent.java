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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProducedSequent extends NodeSequent {

	private static Set<Predicate> getNewHyps(IAntecedent antecedent) {
		final Set<Predicate> newPreds = new LinkedHashSet<Predicate>();
		newPreds.addAll(antecedent.getAddedHyps());
		final List<IHypAction> hypActions = antecedent.getHypActions();
		for (IHypAction hypAction : hypActions) {
			if (hypAction instanceof IForwardInfHypAction) {
				final IForwardInfHypAction fwd = (IForwardInfHypAction) hypAction;
				newPreds.addAll(fwd.getInferredHyps());
			}
		}
		return newPreds;
	}
	
	private final List<RequiredSequent> dependents = new ArrayList<RequiredSequent>();
	
	public ProducedSequent(IAntecedent antecedent, DependNode node) {
		super(getNewHyps(antecedent), antecedent.getGoal(), node);
	}

	public void addDependentSequent(RequiredSequent dependent) {
		if (!dependents.contains(dependent)) {
			dependents.add(dependent);
		}
	}
	
	@Override
	protected void propagateDelete() {
		for (RequiredSequent dependent : dependents) {
			dependent.getNode().delete();
		}
		dependents.clear();
	}
	
	public void deleteDependent(RequiredSequent dependent) {
		if (this.getNode().isDeleted()) {
			// this.getNode() is being deleted:
			// avoid concurrent modification
			return;
		}
		dependents.remove(dependent);
		deleteNodeIfNoDependents();
	}
	
	public void deleteNodeIfNoDependents() {
		if (dependents.isEmpty()) {
			getNode().delete();
		}
	}
}
