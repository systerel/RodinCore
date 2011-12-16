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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

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
		
		private final Set<Predicate> inferred = new HashSet<Predicate>();
		
		public HypCleaner(IProverSequent sequent) {
			this.sequent = sequent;
		}

		@Override
		public Predicate clean(Predicate original) {
			if (!sequent.containsHypothesis(original)
					&& !inferred.contains(original)) {
				return null;
			}
			return original;
		}

		public void addInferred(Collection<Predicate> hyps) {
			inferred.addAll(hyps);
		}
	}
	
	/**
	 * Abstract type for cleaners that clean an object of type T, composed of a
	 * list of E.
	 * 
	 * @param <T>
	 *            type of the cleaned object
	 * @param <E>
	 *            type of the elements of the list
	 */
	private static abstract class AbstractComposedCleaner<T, E> implements ICleaner<T> {
		protected final ICleaner<E> elemCleaner;
		private final ListCleaner<E> listCleaner;
		private final boolean disappearWhenEmpty;

		public AbstractComposedCleaner(ICleaner<E> elemCleaner, boolean disappearWhenEmpty) {
			this.elemCleaner = elemCleaner;
			this.listCleaner = new ListCleaner<E>(elemCleaner);
			this.disappearWhenEmpty = disappearWhenEmpty;
		}

		protected abstract List<E> getList(T object);
		
		protected abstract T makeNewInstance(T original, List<E> cleanList);
		
		public T clean(T original) {
			final List<E> origList = getList(original);
			final List<E> cleanList = listCleaner.clean(origList);
			
			if (disappearWhenEmpty && cleanList.isEmpty()) {
				return null;
			}
			if (cleanList != origList) {
				return makeNewInstance(original, cleanList);
			}
			return original;

		};
	}
	
	private static class HypActCleaner extends AbstractComposedCleaner<IHypAction, Predicate> {

		public HypActCleaner(IProverSequent sequent) {
			super(new HypCleaner(sequent), true);
		}

		@Override
		public IHypAction clean(IHypAction original) {
			final IHypAction clean = super.clean(original);
			
			if (clean instanceof IForwardInfHypAction) {
				final IForwardInfHypAction fwd = (IForwardInfHypAction) clean;
				((HypCleaner) elemCleaner).addInferred(fwd.getInferredHyps());
			}
			return clean;
		}
		
		@Override
		protected List<Predicate> getList(IHypAction hypAction) {
			return new ArrayList<Predicate>(
					((IInternalHypAction) hypAction).getHyps());
		}

		@Override
		protected IHypAction makeNewInstance(IHypAction original,
				List<Predicate> newHyps) {
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
	
	private static class AntecedentCleaner extends AbstractComposedCleaner<IAntecedent, IHypAction> {

		public AntecedentCleaner(IProverSequent sequent) {
			super(new HypActCleaner(sequent), false);
		}

		@Override
		protected List<IHypAction> getList(IAntecedent antecedent) {
			return antecedent.getHypActions();
		}

		@Override
		protected IAntecedent makeNewInstance(IAntecedent original,
				List<IHypAction> cleanList) {
			return makeAntecedent(original.getGoal(),
					original.getAddedHyps(),
					original.getUnselectedAddedHyps(),
					original.getAddedFreeIdents(), cleanList);
		}
		
	}
	
	private static class RuleCleaner extends AbstractComposedCleaner<IProofRule, IAntecedent> {
		
		public RuleCleaner(IProverSequent sequent) {
			super(new AntecedentCleaner(sequent), false);
		}
		
		@Override
		protected List<IAntecedent> getList(IProofRule rule) {
			return Arrays.asList(rule.getAntecedents());
		}

		@Override
		protected IProofRule makeNewInstance(IProofRule original,
				List<IAntecedent> cleanAntes) {
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
