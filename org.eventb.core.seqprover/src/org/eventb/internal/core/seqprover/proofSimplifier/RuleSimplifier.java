/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier;

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

/**
 * @author Nicolas Beauger
 * 
 */
public class RuleSimplifier extends Simplifier<IProofRule> {

	private final Set<Predicate> neededPreds;

	public RuleSimplifier(Set<Predicate> neededPreds) {
		this.neededPreds = neededPreds;
	}

	public IProofRule simplify(IProofRule rule, IProofMonitor monitor)
			throws CancelException {
		final List<IAntecedent> newAntes = simplifyAntecedents(rule
				.getAntecedents(), monitor);
		checkCancel(monitor);
		neededPreds.addAll(rule.getNeededHyps());
		final IAntecedent[] newAntecedents = newAntes
				.toArray(new IAntecedent[newAntes.size()]);
		return makeProofRule(rule.generatedBy(), rule.generatedUsing(), rule
				.getGoal(), rule.getNeededHyps(), rule.getConfidence(), rule
				.getDisplayName(), newAntecedents);
	}

	public Set<Predicate> getNeededPreds() {
		return neededPreds;
	}

	private List<IAntecedent> simplifyAntecedents(IAntecedent[] antecedents,
			IProofMonitor monitor) throws CancelException {
		final List<IAntecedent> newAntes = new ArrayList<IAntecedent>();

		for (IAntecedent antecedent : antecedents) {
			final AntecedentSimplifier simplifier = new AntecedentSimplifier(neededPreds);
			final IAntecedent simplified = simplifier.simplify(antecedent, monitor);
			checkCancel(monitor);
			if (!hasNoEffect(simplified)) {
				newAntes.add(simplified);
				neededPreds.addAll(simplifier.getNeededPreds());
			}
		}
		return newAntes;
	}

	private static boolean hasNoEffect(IAntecedent antecedent) {
		return antecedent.getGoal() == null
				&& antecedent.getHypActions().isEmpty()
				&& antecedent.getAddedFreeIdents().length == 0
				&& antecedent.getAddedHyps().isEmpty();
	}

}
