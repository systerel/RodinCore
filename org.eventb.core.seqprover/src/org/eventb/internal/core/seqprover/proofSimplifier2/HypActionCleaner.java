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

import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.IInternalHypAction;
import org.eventb.internal.core.seqprover.SelectionHypAction;

/**
 * @author Nicolas Beauger
 * 
 */
public class HypActionCleaner {

	private static interface ICleaner<T> {
		
		/**
		 * Cleans the given object.
		 * <p>
		 * Returns:
		 * <ul>
		 * <li><code>null</code> if the original object must disappear
		 * <li>a new clean instance iff the original object must change
		 * <li>the original object iff it is already clean
		 * </ul>
		 * 
		 * @param original
		 *            an object to clean
		 * @return a clean object or <code>null</code>
		 */
		T clean(T original);
	}
	
	private static class ListCleaner<T> implements ICleaner<List<T>> {

		private final ICleaner<T> elementCleaner;
		
		public ListCleaner(ICleaner<T> elementCleaner) {
			this.elementCleaner = elementCleaner;
		}

		@Override
		public List<T> clean(List<T> original) {
			final List<T> clean = new ArrayList<T>(original);
			final ListIterator<T> iter = clean.listIterator();
			// must test hypotheses individually, using containsHypotheses(original)
			// would remove useful predicates
			while (iter.hasNext()) {
				final T element = iter.next();
				final T cleanElement = elementCleaner.clean(element);
				if (cleanElement == null) {
					iter.remove();
				} else if (cleanElement != element) {
					iter.set(cleanElement);
				}
			}
			if (!clean.equals(original)) {
				return clean;
			}
			return original;
		}
	}
	
	private static class HypCleaner implements ICleaner<Predicate> {

		private final IProverSequent sequent;
		
		public HypCleaner(IProverSequent sequent) {
			this.sequent = sequent;
		}

		@Override
		public Predicate clean(Predicate original) {
			if (!sequent.containsHypothesis(original)) {
				return null;
			}
			return original;
		}
		
	}
	
	
	private static class HypActCleaner implements ICleaner<IHypAction> {

		private final ListCleaner<Predicate> hypsCleaner;

		public HypActCleaner(IProverSequent sequent) {
			final HypCleaner hypCleaner = new HypCleaner(sequent);
			this.hypsCleaner = new ListCleaner<Predicate>(hypCleaner);
		}

		@Override
		public IHypAction clean(IHypAction original) {
			final List<Predicate> origHyps = new ArrayList<Predicate>(
					((IInternalHypAction) original).getHyps());
			final List<Predicate> cleanHyps = hypsCleaner.clean(origHyps);
			
			if (cleanHyps.isEmpty()) {
				return null;
			}
			if (cleanHyps != origHyps) {
				return makeHypAction(original, cleanHyps);
			}
			return original;
		}
		
		private static IHypAction makeHypAction(IHypAction original, Collection<Predicate> newHyps) {
			if (original instanceof IForwardInfHypAction) {
				final IForwardInfHypAction fwd = (IForwardInfHypAction) original;
				return makeForwardInfHypAction(newHyps, fwd.getAddedFreeIdents(),
						fwd.getInferredHyps());
			} else if (original instanceof ISelectionHypAction) {
				return new SelectionHypAction(original.getActionType(), newHyps);
			} else { // unknown hyp action type
				assert false;
				return null;
			}
		}

	}
	
	private static class AntecedentCleaner implements ICleaner<IAntecedent> {

		private final ListCleaner<IHypAction> hypActsCleaner;
		
		public AntecedentCleaner(IProverSequent sequent) {
			final HypActCleaner hypActCleaner = new HypActCleaner(sequent);
			this.hypActsCleaner = new ListCleaner<IHypAction>(hypActCleaner);
		}
		
		@Override
		public IAntecedent clean(IAntecedent original) {
			final List<IHypAction> origHypActs = original.getHypActions();
			
			final List<IHypAction> cleanHypActs = hypActsCleaner.clean(origHypActs);
			
			if (cleanHypActs != origHypActs) {
				return makeAnte(original, cleanHypActs);
			}
			return original;
		}

		private static IAntecedent makeAnte(IAntecedent original,
				List<IHypAction> cleanHypActs) {
			return makeAntecedent(original.getGoal(),
					original.getAddedHyps(),
					original.getUnselectedAddedHyps(),
					original.getAddedFreeIdents(), cleanHypActs);
		}
		
	}
	
	private static class RuleCleaner implements ICleaner<IProofRule> {
		
		private final ListCleaner<IAntecedent> antesCleaner;
		
		public RuleCleaner(IProverSequent sequent) {
			final AntecedentCleaner anteCleaner = new AntecedentCleaner(sequent);
			this.antesCleaner = new ListCleaner<IAntecedent>(anteCleaner);
		}
		
		@Override
		public IProofRule clean(IProofRule original) {
			
			final List<IAntecedent> origAntes = Arrays.asList(original
					.getAntecedents());
			final List<IAntecedent> cleanAntes = antesCleaner.clean(origAntes);
			
			if (cleanAntes != origAntes) {
				return makeRule(original, cleanAntes);

			}
			return original;
		}

		private static IProofRule makeRule(IProofRule original,
				final List<IAntecedent> cleanAntes) {
			final IAntecedent[] newAntes = cleanAntes
					.toArray(new IAntecedent[cleanAntes.size()]);
			return makeProofRule(original.getReasonerDesc(),
					original.generatedUsing(), original.getGoal(),
					original.getNeededHyps(), original.getConfidence(),
					original.getDisplayName(), newAntes);
		}
		
	}
	
	/**
	 * Returns a rule with unused hypothesis actions removed, or the given
	 * rule if no change occurred.
	 * 
	 * @param node
	 *            a proof tree node with a rule
	 * @return a new DependRule
	 */
	public static IProofRule cleanHypActions(IProofTreeNode node) {
		return new RuleCleaner(node.getSequent()).clean(node.getRule());
	}

}
