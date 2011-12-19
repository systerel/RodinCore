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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.core.seqprover.IInternalHypAction;

/**
 * A proof tree node type to use for dependence computation and manipulation.
 * <p>
 * Instances of this class are compared using == operator.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public class DependNode {


	private IProofRule rule;

	private final RequiredSequent requiredSequent;

	private final ProducedSequent[] producedSequents;

	// the boolean is set to true when the node is deleted
	private boolean deleted = false;

	public DependNode(IProofTreeNode node) {
		final Set<Predicate> reqHyps = new LinkedHashSet<Predicate>();
		final List<ProducedSequent> prodSeqs = new ArrayList<ProducedSequent>();
		this.rule = populateSimplify(node, reqHyps, prodSeqs, this);
		this.producedSequents = prodSeqs.toArray(new ProducedSequent[prodSeqs.size()]);
		final Predicate reqGoal = node.getRule().getGoal();
		this.requiredSequent = new RequiredSequent(reqHyps, reqGoal, this);
	}

	private static IProofRule populateSimplify(IProofTreeNode pNode,
			Set<Predicate> reqHyps, List<ProducedSequent> prodSeqs,
			DependNode node) {
		final IProofRule rule = pNode.getRule();
		reqHyps.addAll(rule.getNeededHyps());
		final List<IAntecedent> antecedents = new ArrayList<IAntecedent>(Arrays.asList(rule.getAntecedents()));
		final ListIterator<IAntecedent> iterAnte = antecedents.listIterator();
		while(iterAnte.hasNext()) {
			final IAntecedent antecedent = iterAnte.next();
			final Set<Predicate> prodHyps = new LinkedHashSet<Predicate>();
			prodHyps.addAll(antecedent.getAddedHyps());
			final List<IHypAction> hypActions = new ArrayList<IHypAction>(antecedent.getHypActions());
			final ListIterator<IHypAction> iterAction = hypActions.listIterator();
			while (iterAction.hasNext()) {
				final IInternalHypAction act = (IInternalHypAction) iterAction
						.next();
				final List<Predicate> actHyps = new ArrayList<Predicate>(
						act.getHyps());
				actHyps.removeAll(prodHyps);
				final Iterator<Predicate> iterHyp = actHyps.iterator();
				while (iterHyp.hasNext()) {
					final Predicate hyp = iterHyp.next();
					if (!pNode.getSequent().containsHypothesis(hyp)) {
						iterHyp.remove();
					}
				}
				if (actHyps.isEmpty()) {
					iterAction.remove();
					continue;
				} else {
					iterAction.set(HypActionCleaner.makeHypAction(act, actHyps));
				}
				reqHyps.addAll(actHyps);
				if (act instanceof IForwardInfHypAction) {
					final IForwardInfHypAction fwd = (IForwardInfHypAction) act;
					prodHyps.addAll(fwd.getInferredHyps());
				}
			}
			iterAnte.set(HypActionCleaner.makeAnte(antecedent, hypActions));
			final Predicate prodGoal = antecedent.getGoal();
			prodSeqs.add(new ProducedSequent(prodHyps, prodGoal, node));
		}
		return HypActionCleaner.makeRule(rule, antecedents);
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
		return rule;
	}
	
	public void compressRule() {
		
		final IAntecedent[] antecedents = rule.getAntecedents();
		final ProducedSequent[] sequents = this.getProducedSequents();
		Assert.isTrue(sequents.length == antecedents.length);
		
		for (int i = 0; i < sequents.length; i++) {
			final ProducedSequent sequent = sequents[i];
			final Collection<Predicate> usedPredicates = sequent.getUsedPredicates();
			final IAntecedent antecedent = antecedents[i];
			final List<IHypAction> hypActions = antecedent.getHypActions();//FIXME no modify
			final Set<Predicate> skipped = new HashSet<Predicate>(); 
			final ListIterator<IHypAction> iterAction = hypActions.listIterator();
			while(iterAction.hasNext()) {
				final IHypAction hypAction = iterAction.next();
				if (hypAction instanceof IForwardInfHypAction) {
					final IForwardInfHypAction fwd = (IForwardInfHypAction) hypAction;
					final Collection<Predicate> inferredHyps = fwd
							.getInferredHyps();
					final List<Predicate> usefulInf = new ArrayList<Predicate>(
							inferredHyps);
					usefulInf.retainAll(usedPredicates);
					if (usefulInf.isEmpty()) {
						iterAction.remove();
						skipped.addAll(fwd.getHyps());
						skipped.addAll(fwd.getInferredHyps());
					} else if (usefulInf.size() < inferredHyps.size()) {
						iterAction.set(HypActionCleaner.makeHypAction(fwd,
								usefulInf));
					}
				} else if (hypAction instanceof ISelectionHypAction) {
					final ISelectionHypAction select = (ISelectionHypAction) hypAction;
					final Collection<Predicate> hyps = new ArrayList<Predicate>(select.getHyps());
					hyps.removeAll(skipped);
					if(hyps.isEmpty()) {
						iterAction.remove();
					}
				}
			}
			
			antecedents[i] = HypActionCleaner.makeAnte(antecedent, hypActions);
		}
		rule = HypActionCleaner.makeRule(rule, Arrays.asList(antecedents));
	}
}
