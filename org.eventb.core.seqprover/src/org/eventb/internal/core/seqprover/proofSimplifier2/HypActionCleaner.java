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

import org.eclipse.core.runtime.Assert;
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

		protected void preClean(T element) {
			// override to add processing before given element gets cleaned
		}
		
		protected void postClean(T element) {
			// override to add processing after given element has been cleaned
		}
		
		@Override
		public List<T> clean(List<T> original) {
			final List<T> clean = new ArrayList<T>(original);
			final ListIterator<T> iter = clean.listIterator();
			while (iter.hasNext()) {
				final T element = iter.next();
				preClean(element);
				final T cleanElement = elementCleaner.clean(element);
				if (cleanElement == null) {
					iter.remove();
				} else if (cleanElement != element) {
					iter.set(cleanElement);
				}
				postClean(cleanElement);
			}
			if (!clean.equals(original)) {
				return clean;
			}
			return original;
		}
	}
	
	private static abstract class AbstractHypCleaner implements ICleaner<Predicate> {

		protected abstract boolean isUseful(Predicate hyp);
		
		@Override
		public Predicate clean(Predicate original) {
			if (!isUseful(original)) {
				return null;
			}
			return original;
		}
	}
	
	private static class HypCleaner extends AbstractHypCleaner {
		
		private final IProverSequent sequent;
		
		private final Set<Predicate> inferred = new HashSet<Predicate>();
		
		public HypCleaner(IProverSequent sequent) {
			this.sequent = sequent;
		}

		public void addInferred(Collection<Predicate> hyps) {
			inferred.addAll(hyps);
		}

		@Override
		protected boolean isUseful(Predicate hyp) {
			return sequent.containsHypothesis(hyp) || inferred.contains(hyp);
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
		private final ListCleaner<E> listCleaner;
		private final boolean disappearWhenEmpty;

		public AbstractComposedCleaner(ListCleaner<E> listCleaner, boolean disappearWhenEmpty) {
			this.listCleaner = listCleaner;
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
	
	private static abstract class AbstractHypActCleaner extends AbstractComposedCleaner<IHypAction, Predicate> {

		public AbstractHypActCleaner(ListCleaner<Predicate> predListCleaner) {
			super(predListCleaner, true);
		}
		
		@Override
		protected List<Predicate> getList(IHypAction hypAction) {
			return new ArrayList<Predicate>(
					((IInternalHypAction) hypAction).getHyps());
		}

		@Override
		protected IHypAction makeNewInstance(IHypAction original,
				List<Predicate> newHyps) {
			return makeHypAction(original, newHyps);
		}

		
	}

	public static IHypAction makeHypAction(IHypAction original,
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
	
	private static class HypActCleaner extends AbstractHypActCleaner {

		private final HypCleaner hypCleaner;

		public HypActCleaner(HypCleaner hypCleaner) {
			super(new ListCleaner<Predicate>(hypCleaner));
			this.hypCleaner = hypCleaner;
		}

		@Override
		public IHypAction clean(IHypAction original) {
			final IHypAction clean = super.clean(original);
			// enable hyp actions on forward inferred hypotheses
			if (clean instanceof IForwardInfHypAction) {
				final IForwardInfHypAction fwd = (IForwardInfHypAction) clean;
				hypCleaner.addInferred(fwd.getInferredHyps());
			}
			return clean;
		}

	}
	
	private static class AntecedentCleaner extends AbstractComposedCleaner<IAntecedent, IHypAction> {

		public AntecedentCleaner(AbstractHypActCleaner hypActCleaner) {
			super(new ListCleaner<IHypAction>(hypActCleaner), false);
		}

		@Override
		protected List<IHypAction> getList(IAntecedent antecedent) {
			return antecedent.getHypActions();
		}

		@Override
		protected IAntecedent makeNewInstance(IAntecedent original,
				List<IHypAction> cleanList) {
			return makeAnte(original, cleanList);
		}

	}
	
	public static IAntecedent makeAnte(IAntecedent original,
			List<IHypAction> cleanList) {
		return makeAntecedent(original.getGoal(),
				original.getAddedHyps(),
				original.getUnselectedAddedHyps(),
				original.getAddedFreeIdents(), cleanList);
	}
	
	private static class RuleCleaner extends AbstractComposedCleaner<IProofRule, IAntecedent> {
		
		public RuleCleaner(AntecedentCleaner anteCleaner) {
			super(new ListCleaner<IAntecedent>(anteCleaner), false);
		}
		
		@Override
		protected List<IAntecedent> getList(IProofRule rule) {
			return Arrays.asList(rule.getAntecedents());
		}

		@Override
		protected IProofRule makeNewInstance(IProofRule original,
				List<IAntecedent> cleanAntes) {
			return makeRule(original, cleanAntes);
		}

	}
	
	public static IProofRule makeRule(IProofRule original,
			final List<IAntecedent> newAntes) {
		final IAntecedent[] anteArray = newAntes
				.toArray(new IAntecedent[newAntes.size()]);
		return makeProofRule(original.getReasonerDesc(),
				original.generatedUsing(), original.getGoal(),
				original.getNeededHyps(), original.getConfidence(),
				original.getDisplayName(), anteArray);
	}
	
	/**
	 * Returns a rule with void hypothesis actions removed, or the given
	 * rule if no change occurred.
	 * 
	 * @param node
	 *            a proof tree node with a rule
	 * @return a new DependRule
	 */
	public static IProofRule cleanHypActions(IProofTreeNode node) {
		final IProverSequent sequent = node.getSequent();
		final RuleCleaner ruleCleaner = new RuleCleaner(new AntecedentCleaner(
				new HypActCleaner(new HypCleaner(sequent))));
		return ruleCleaner.clean(node.getRule());
	}

	public static IProofRule cleanHypActions(DependNode node) {
		// TODO put this into a class, factorize with RuleCleaner,
		// FIXME don't modify hyp action list references directly
		final IProofRule rule = node.getRule();
		
		final IAntecedent[] antecedents = rule.getAntecedents();
		final ProducedSequent[] sequents = node.getProducedSequents();
		Assert.isTrue(sequents.length == antecedents.length);
		
		for (int i = 0; i < sequents.length; i++) {
			final ProducedSequent sequent = sequents[i];
			final Collection<Predicate> usedPredicates = sequent.getUsedPredicates();
			final IAntecedent antecedent = antecedents[i];
			final List<IHypAction> hypActions = antecedent.getHypActions();//FIXME no modify
			final Set<Predicate> skipped = new HashSet<Predicate>(); 
			final ListIterator<IHypAction> iter = hypActions.listIterator();
			while(iter.hasNext()) {
				final IHypAction hypAction = iter.next();
				if (hypAction instanceof IForwardInfHypAction) {
					final IForwardInfHypAction fwd = (IForwardInfHypAction) hypAction;
					final Collection<Predicate> inferredHyps = fwd
							.getInferredHyps();
					final List<Predicate> usefulInf = new ArrayList<Predicate>(
							inferredHyps);
					usefulInf.retainAll(usedPredicates);
					if (usefulInf.isEmpty()) {
						iter.remove();
						skipped.addAll(fwd.getHyps());
						skipped.addAll(fwd.getInferredHyps());
					} else if (usefulInf.size() < inferredHyps.size()) {
						iter.set(new HypActCleaner(null).makeNewInstance(fwd,
								usefulInf));
					}
				} else if (hypAction instanceof ISelectionHypAction) {
					final ISelectionHypAction select = (ISelectionHypAction) hypAction;
					final Collection<Predicate> hyps = new ArrayList<Predicate>(select.getHyps());
					hyps.removeAll(skipped);
					if(hyps.isEmpty()) {
						iter.remove();
					}
				}
			}
			
			antecedents[i] = new AntecedentCleaner(null).makeNewInstance(antecedent, hypActions);
		}
		return new RuleCleaner(null).makeNewInstance(rule, Arrays.asList(antecedents));
	}
}
