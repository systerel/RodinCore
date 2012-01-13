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

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException.checkCancel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.IInternalHypAction;
import org.eventb.internal.core.seqprover.SelectionHypAction;
import org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException;

/**
 * @author Nicolas Beauger
 * 
 */
public class DependRule {

	private static abstract class AHypAction {

		protected final Set<Predicate> hyps;

		public AHypAction(IInternalHypAction hypAction) {
			this.hyps = new LinkedHashSet<Predicate>(hypAction.getHyps());
		}

		public abstract IHypAction toHypAction();

		public void init(IProverSequent sequent, Set<Predicate> producedHyps) {
			final Iterator<Predicate> iter = hyps.iterator();
			while (iter.hasNext()) {
				final Predicate hyp = iter.next();
				if (!sequent.containsHypothesis(hyp)
						&& !producedHyps.contains(hyp)) {
					iter.remove();
				}
			}
		}

		public boolean isEmpty() {
			return hyps.isEmpty();
		}

		public void addRequired(Set<Predicate> requiredHyps) {
			requiredHyps.addAll(hyps);
		}

		public abstract void addProduced(Set<Predicate> producedHyps);

		public abstract void compress(ProducedSequent sequent,
				Set<Predicate> producedHyps, Set<Predicate> removedHyps);
	}

	private static class Fwd extends AHypAction {
		private final Set<Predicate> inferredHyps;
		final FreeIdentifier[] addedFreeIdents;

		public Fwd(IForwardInfHypAction hypAction) {
			super((IInternalHypAction) hypAction);
			this.inferredHyps = new LinkedHashSet<Predicate>(
					hypAction.getInferredHyps());
			this.addedFreeIdents = hypAction.getAddedFreeIdents();
		}

		@Override
		public IHypAction toHypAction() {
			return makeForwardInfHypAction(hyps, addedFreeIdents, inferredHyps);
		}

		@Override
		public void addProduced(Set<Predicate> producedHyps) {
			producedHyps.addAll(inferredHyps);
		}

		@Override
		public void compress(ProducedSequent sequent,
				Set<Predicate> producedHyps, Set<Predicate> removedHyps) {
			// if at least one inferred hyp is used, all are kept
			final Set<Predicate> usedPreds = sequent.getUsedPredicates();
			for (Predicate hyp : inferredHyps) {
				if (usedPreds.contains(hyp)) {
					producedHyps.addAll(inferredHyps);
					return;
				}
			}
			// no need to hide source hyps or select inferred hyps
			removedHyps.addAll(hyps);
			removedHyps.addAll(inferredHyps);
			// none is used, make empty
			hyps.clear();
			inferredHyps.clear();
		}

	}

	private static class Select extends AHypAction {

		private final String actionType;

		public Select(ISelectionHypAction hypAction) {
			super((IInternalHypAction) hypAction);
			this.actionType = hypAction.getActionType();
		}

		@Override
		public IHypAction toHypAction() {
			return new SelectionHypAction(actionType, hyps);
		}

		@Override
		public void addProduced(Set<Predicate> producedHyps) {
			// nothing is produced
		}

		@Override
		public void compress(ProducedSequent sequent,
				Set<Predicate> producedHyps, Set<Predicate> removedHyps) {
			if (actionType.equals(ISelectionHypAction.SELECT_ACTION_TYPE)
					|| actionType.equals(ISelectionHypAction.SHOW_ACTION_TYPE)) {
				final Set<Predicate> usedPredicates = sequent
						.getUsedPredicates();
				final Iterator<Predicate> iter = hyps.iterator();
				while (iter.hasNext()) {
					final Predicate hyp = iter.next();
					if (!usedPredicates.contains(hyp)
							&& !producedHyps.contains(hyp)) {
						iter.remove();
					}
				}
			} else {
				// HIDE or DESELECT
				final Iterator<Predicate> iter = hyps.iterator();
				while (iter.hasNext()) {
					final Predicate hyp = iter.next();
					// FIXME removes a single hide, i.e all non produced
					// hypActions
					if (removedHyps.contains(hyp)) {
						iter.remove();
					}
				}

			}
		}

	}

	private static class DependAntecedent {
		private final List<AHypAction> hypActions = new ArrayList<AHypAction>();
		private final IAntecedent original;
		private final Set<Predicate> producedHyps = new LinkedHashSet<Predicate>();

		public DependAntecedent(IAntecedent antecedent) {
			this.original = antecedent;
			for (IHypAction hypAction : antecedent.getHypActions()) {
				if (hypAction instanceof IForwardInfHypAction) {
					hypActions.add(new Fwd((IForwardInfHypAction) hypAction));
				} else if (hypAction instanceof ISelectionHypAction) {
					hypActions.add(new Select((ISelectionHypAction) hypAction));
				} else {
					// unknown hyp action type
					assert false;
				}
			}
		}

		// remove hyp actions for hyps not in the given sequent,
		// contribute required hypotheses
		// must be called after constructor before any other method
		public void init(IProverSequent sequent, Set<Predicate> requiredHyps) {
			producedHyps.addAll(original.getAddedHyps());

			final Iterator<AHypAction> iter = hypActions.iterator();
			while (iter.hasNext()) {
				final AHypAction hypAction = iter.next();
				hypAction.init(sequent, producedHyps);
				if (hypAction.isEmpty()) {
					iter.remove();
					continue;
				}
				hypAction.addRequired(requiredHyps);
				hypAction.addProduced(producedHyps);
			}
			requiredHyps.removeAll(producedHyps);
		}

		public ProducedSequent makeProducedSequent(DependNode node) {
			final Predicate prodGoal = original.getGoal();
			return new ProducedSequent(producedHyps, prodGoal, node);
		}

		public void compressHypActions(ProducedSequent producedSequent,
				IProofMonitor monitor) throws CancelException {
			final Set<Predicate> prodHyps = new HashSet<Predicate>();
			// keep track of removed hyps, to remove associated actions
			final Set<Predicate> removedHyps = new HashSet<Predicate>();
			prodHyps.addAll(original.getAddedHyps());
			final Iterator<AHypAction> iter = hypActions.iterator();
			while (iter.hasNext()) {
				checkCancel(monitor);
				final AHypAction hypAction = iter.next();
				hypAction.compress(producedSequent, prodHyps, removedHyps);
				if (hypAction.isEmpty()) {
					iter.remove();
				}
			}
		}

		public IAntecedent toAntecedent() {
			final List<IHypAction> newActions = new ArrayList<IHypAction>(
					hypActions.size());
			for (AHypAction hypAction : hypActions) {
				newActions.add(hypAction.toHypAction());
			}
			return makeAntecedent(original.getGoal(), original.getAddedHyps(),
					original.getUnselectedAddedHyps(),
					original.getAddedFreeIdents(), newActions);
		}

	}

	private final IProofRule original;
	private final List<DependAntecedent> antecedents = new ArrayList<DependRule.DependAntecedent>();
	private final Set<Predicate> requiredHyps = new LinkedHashSet<Predicate>();

	public DependRule(IProofRule rule) {
		this.original = rule;
		for (IAntecedent antecedent : rule.getAntecedents()) {
			antecedents.add(new DependAntecedent(antecedent));
		}
	}

	// initialize the rule and remove void hyp actions
	// whose source hyp is not in the given sequent
	public void init(IProverSequent sequent) {
		requiredHyps.addAll(original.getNeededHyps());

		for (DependAntecedent ante : antecedents) {
			ante.init(sequent, requiredHyps);
		}
	}

	public RequiredSequent makeRequiredSequent(DependNode node) {
		final Predicate reqGoal = original.getGoal();

		return new RequiredSequent(requiredHyps, reqGoal, node);
	}

	public ProducedSequent[] makeProducedSequents(DependNode node) {
		final int size = antecedents.size();
		final ProducedSequent[] prodSeqs = new ProducedSequent[size];
		for (int i = 0; i < size; i++) {
			prodSeqs[i] = antecedents.get(i).makeProducedSequent(node);
		}
		return prodSeqs;
	}

	public void compressHypActions(ProducedSequent[] producedSequents,
			IProofMonitor monitor) throws CancelException {
		assert producedSequents.length == antecedents.size();

		for (int i = 0; i < producedSequents.length; i++) {
			checkCancel(monitor);
			antecedents.get(i).compressHypActions(producedSequents[i], monitor);
		}
	}

	public IProofRule toProofRule() {
		final IAntecedent[] anteArray = new IAntecedent[antecedents.size()];
		for (int i = 0; i < antecedents.size(); i++) {
			anteArray[i] = antecedents.get(i).toAntecedent();
		}

		return makeProofRule(original.getReasonerDesc(),
				original.generatedUsing(), original.getGoal(),
				original.getNeededHyps(), original.getConfidence(),
				original.getDisplayName(), anteArray);
	}
}
