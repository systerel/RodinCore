/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - type-checking is now done with class TypeChecker
 *     Systerel - added check about predicate variables
 *     Systerel - added unselected added hypotheses
 *     Systerel - added origin
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;

/**
 * This class is the default implementation of the {@link IProverSequent} and 
 * {@link IInternalProverSequent} interfaces.
 * 
 *  <p>
 *  Prover sequents are implemented to be immutable data structures and take advantage of 
 *  this to share their instance variables in case they do not change.
 *  </p>
 *  <p>
 *  In this implementation, the set of all hypotheses is the union of the global, and the local
 *  hypotheses. The global hypotheses are shared by all sequents constructed incrementally using this
 *  sequent.
 *  </p>
 * 
 * 
 * @author Farhad Mehta
 *
 */
public final class ProverSequent implements IInternalProverSequent{
	
	/**
	 * Debug flag for <code>PROVER_SEQUENT_TRACE</code>
	 */
	public static boolean DEBUG;

	/**
	 * Prints the given message to the console in case the debug flag is
	 * switched on. Optional given objects are appended to the message, calling
	 * their <code>toString()</code> method only if debug mode is enabled.
	 * 
	 * @param message
	 *            The message to print out to the console
	 */
	private void traceCreation() {
		if (DEBUG) {
			System.out.println("Constructed new sequent " + this);
		}
	}

	// TODO : Profiling : It may be that caching visible hyps may improve performance.
	
	private final ISealedTypeEnvironment typeEnvironment;
	
	
	/**
	 * Instance fields
	 * 
	 * Chosen to be LinkedHashSets to preserve their order.
	 */
	@ProverRule("DBL_HYP")
	private final LinkedHashSet<Predicate> globalHypotheses;
	private final LinkedHashSet<Predicate> localHypotheses;
	
	private final LinkedHashSet<Predicate> hiddenHypotheses;
	private final LinkedHashSet<Predicate> selectedHypotheses;
	
	private final Predicate goal;

	private final Object origin;
	
	/**
	 * Static immutable variables.
	 */
	private static final LinkedHashSet<Predicate> NO_HYPS =
		new LinkedHashSet<Predicate>();
	
	@Override
	public ISealedTypeEnvironment typeEnvironment() {
		return this.typeEnvironment;
	}
		
	@Override
	public Predicate goal() {
		return this.goal;
	}
	
	/**
	 * Constructs a new Prover Sequent incrementally, from an old one, selectively replacing the
	 * provided fields, or <code>null</code> in case this field should not be overridden.
	 * 
	 * <p>
	 * This should always remain a private constructor. All methods using this constructor must ensure that
	 * the parameters passed into it have been cloned prior to calling this constructor. 
	 * </p>
	 * 
	 * @param seq
	 * 			The sequent to base the new sequent on. Should never be <code>null</code>.
	 * @param typeEnvironment
	 * @param globalHypotheses
	 * @param localHypotheses
	 * @param hiddenHypotheses
	 * @param selectedHypotheses
	 * @param goal
	 */
	private ProverSequent(ProverSequent seq, ITypeEnvironment typeEnvironment, LinkedHashSet<Predicate> globalHypotheses,
			LinkedHashSet<Predicate> localHypotheses, LinkedHashSet<Predicate> hiddenHypotheses, LinkedHashSet<Predicate> selectedHypotheses,
			Predicate goal){
		
		assert (seq != null) | (typeEnvironment != null & globalHypotheses != null & localHypotheses != null & 
				hiddenHypotheses != null & selectedHypotheses != null & goal != null);
		
		if (typeEnvironment == null) this.typeEnvironment = seq.typeEnvironment;
		else this.typeEnvironment = typeEnvironment.makeSnapshot();
		
		if (globalHypotheses == null) this.globalHypotheses = seq.globalHypotheses;
		else this.globalHypotheses = globalHypotheses;
		
		if (localHypotheses == null) this.localHypotheses = seq.localHypotheses;
		else this.localHypotheses = localHypotheses;
		
		if (hiddenHypotheses == null) this.hiddenHypotheses = seq.hiddenHypotheses;
		else this.hiddenHypotheses = hiddenHypotheses;
		
		if (selectedHypotheses == null) this.selectedHypotheses = seq.selectedHypotheses;
		else this.selectedHypotheses = selectedHypotheses;
		
		if (goal == null) this.goal = seq.goal;
		else this.goal = goal;

		this.origin = seq.origin;

		traceCreation();
	}
	
	/**
	 * Constructs a new sequent with the given parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li>The formulaFactory must be able to parse all predicates of this
	 * sequent.
	 * <li>All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li>The type environment provided should contain all free identifiers and
	 * carrier sets appearing in the predicates of the sequent and can be used
	 * to successfully type check them.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param formulaFactory 
	 * 				The formula factory to be used
	 * @param typeEnv
	 *            The type environment for the sequent. This parameter must
	 *            not be <code>null</code>. It should be ensured that all 
	 *            predicates can be type checked using this type environment
	 * @param globalHypotheses
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty
	 * @param hiddenHypSet
	 *            The set of hypotheses to hide
	 * @param selectedHypSet
	 *            The set of hypotheses to select. The set of hypotheses to
	 *            select should be contained in the set of initial hypotheses
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>
	 */
	public ProverSequent(ITypeEnvironment typeEnv, Collection<Predicate> globalHypSet,
			Collection<Predicate> hiddenHypSet, Collection<Predicate> selectedHypSet,
			Predicate goal, Object origin) {
		this.typeEnvironment = typeEnv.makeSnapshot();
		this.globalHypotheses = globalHypSet == null ? NO_HYPS : new LinkedHashSet<Predicate>(globalHypSet);
		this.localHypotheses = NO_HYPS;
		this.hiddenHypotheses = hiddenHypSet== null ? NO_HYPS : new LinkedHashSet<Predicate>(hiddenHypSet);;
		this.selectedHypotheses = selectedHypSet== null ? NO_HYPS : new LinkedHashSet<Predicate>(selectedHypSet);
		this.goal = goal;
		this.origin = origin;
		traceCreation();
	}

	@Override
	public IInternalProverSequent modify(FreeIdentifier[] freshFreeIdents,
			Collection<Predicate> addhyps,
			Collection<Predicate> unselAddedHyps, Predicate newGoal) {
		boolean modified = false;
		final ITypeEnvironment newTypeEnv;
		LinkedHashSet<Predicate> newLocalHypotheses = null;
		LinkedHashSet<Predicate> newSelectedHypotheses = null;
		LinkedHashSet<Predicate> newHiddenHypotheses = null;

		final TypeChecker checker = new TypeChecker(
				typeEnvironment);
		checker.addIdents(freshFreeIdents);
		checker.checkFormulas(addhyps);
		checker.checkFormulaMaybeNull(newGoal);
		if (checker.hasTypeCheckError())
			return null;
		if (!checker.areAddedIdentsFresh())
			return null;
		if (!ProverChecks.checkNoPredicateVariable(addhyps))
			return null;
		if (!ProverChecks.checkNoPredicateVariable(newGoal))
			return null;
		if (unselAddedHyps != null) {
			if (addhyps == null || !addhyps.containsAll(unselAddedHyps))
				return null;
		}
		newTypeEnv = checker.getTypeEnvironment();
		modified |= checker.hasNewTypeEnvironment();

		if (addhyps != null && addhyps.size() != 0) {
			if (unselAddedHyps == null)
				unselAddedHyps = Collections.emptySet();
			newLocalHypotheses = new LinkedHashSet<Predicate>(localHypotheses);
			newSelectedHypotheses = new LinkedHashSet<Predicate>(selectedHypotheses);
			newHiddenHypotheses = new LinkedHashSet<Predicate>(hiddenHypotheses);
			for (Predicate hyp : addhyps) {
				// if (! typeCheckClosed(hyp,newTypeEnv)) return null;
				if (! this.containsHypothesis(hyp)){
					newLocalHypotheses.add(hyp);
					modified = true;
				}
				if (!unselAddedHyps.contains(hyp)) {
					modified |= newSelectedHypotheses.add(hyp);
				}
				modified |= newHiddenHypotheses.remove(hyp);
			}
		}
		if (newGoal != null && ! newGoal.equals(goal)) {
			modified = true;
		}
		
		if (modified) {
			return new ProverSequent(this, newTypeEnv, null,
					newLocalHypotheses, newHiddenHypotheses,
					newSelectedHypotheses, newGoal);
		}
		return this;
	}
		
	
	@Override
	public ProverSequent selectHypotheses(Collection<Predicate> toSelect){
		if (toSelect == null) return this;
		boolean modified = false;
		
		LinkedHashSet<Predicate> newSelectedHypotheses = new LinkedHashSet<Predicate>(this.selectedHypotheses);
		LinkedHashSet<Predicate> newHiddenHypotheses = new LinkedHashSet<Predicate>(this.hiddenHypotheses);
		
		for (Predicate hyp:toSelect){
			if (containsHypothesis(hyp)){
				modified |= newSelectedHypotheses.add(hyp);
				modified |= newHiddenHypotheses.remove(hyp);
			}
		}
		if (modified) return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
		return this;
	}
	
	@Override
	public ProverSequent deselectHypotheses(Collection<Predicate> toDeselect){
		if (toDeselect == null) return this;
		LinkedHashSet<Predicate> newSelectedHypotheses = new LinkedHashSet<Predicate>(this.selectedHypotheses);
		boolean modified = newSelectedHypotheses.removeAll(toDeselect);
		if (modified) return new ProverSequent(this,null,null,null,null,newSelectedHypotheses,null);
		return this;
	}
	
	@Override
	public ProverSequent hideHypotheses(Collection<Predicate> toHide){
		if (toHide == null) return this;
		boolean modified = false;
		
		LinkedHashSet<Predicate> newSelectedHypotheses = new LinkedHashSet<Predicate>(this.selectedHypotheses);
		LinkedHashSet<Predicate> newHiddenHypotheses = new LinkedHashSet<Predicate>(this.hiddenHypotheses);
		
		for (Predicate hyp:toHide){
			if (containsHypothesis(hyp)){
				modified |= newHiddenHypotheses.add(hyp);
				modified |= newSelectedHypotheses.remove(hyp);
			}
		}
		if (modified) return new ProverSequent(this,null,null,null,newHiddenHypotheses,newSelectedHypotheses,null);
		return this;
	}
	
	@Override
	public ProverSequent showHypotheses(Collection<Predicate> toShow){
		if (toShow == null)
			return this;
		LinkedHashSet<Predicate> newHiddenHypotheses = new LinkedHashSet<Predicate>(
				this.hiddenHypotheses);
		boolean modified = newHiddenHypotheses.removeAll(toShow);
		if (modified)
			return new ProverSequent(this, null, null, null,
					newHiddenHypotheses, null, null);
		return this;
	}

	private boolean isFwdInfApplicable(TypeChecker checker,
			Collection<Predicate> hyps, FreeIdentifier[] addedIdents,
			Collection<Predicate> infHyps) {
		checker.checkFormulas(hyps);
		checker.addIdents(addedIdents);
		checker.checkFormulas(infHyps);
		if (checker.hasTypeCheckError())
			return false;
		if (!checker.areAddedIdentsFresh())
			return false;
		if (!ProverChecks.checkNoPredicateVariable(infHyps))
			return false;

		if (hyps != null && !this.containsHypotheses(hyps))
			return false;

		return true;
	}

	private IInternalProverSequent applyFwdInf(final TypeChecker checker,
			Collection<Predicate> hyps, Collection<Predicate> infHyps) {
		boolean modified = false;

		final ITypeEnvironment newTypeEnv = checker.getTypeEnvironment();
		modified |= checker.hasNewTypeEnvironment();

		boolean selectInfHyps = true;
		boolean hideInfHyps = false;

		if (hyps != null) {
			selectInfHyps = !Collections.disjoint(hyps, selectedHypotheses);
			hideInfHyps = selectInfHyps ? false : hiddenHypotheses
					.containsAll(hyps);
		}

		LinkedHashSet<Predicate> newLocalHypotheses = null;
		LinkedHashSet<Predicate> newSelectedHypotheses = null;
		LinkedHashSet<Predicate> newHiddenHypotheses = null;

		if (infHyps != null) {
			newLocalHypotheses = new LinkedHashSet<Predicate>(localHypotheses);
			newSelectedHypotheses = new LinkedHashSet<Predicate>(
					selectedHypotheses);
			newHiddenHypotheses = new LinkedHashSet<Predicate>(hiddenHypotheses);
			for (Predicate infHyp : infHyps) {
				// if (! typeCheckClosed(infHyp,newTypeEnv)) return this;
				if (!this.containsHypothesis(infHyp)) {
					newLocalHypotheses.add(infHyp);
					if (selectInfHyps)
						newSelectedHypotheses.add(infHyp);
					if (hideInfHyps)
						newHiddenHypotheses.add(infHyp);
					modified = true;
				}
			}
		}
		if (modified)
			return new ProverSequent(this, newTypeEnv, null,
					newLocalHypotheses, newHiddenHypotheses,
					newSelectedHypotheses, null);
		return this;
	}

	@Override
	public IInternalProverSequent performfwdInf(Collection<Predicate> hyps,
			FreeIdentifier[] addedIdents, Collection<Predicate> infHyps) {

		final TypeChecker checker = new TypeChecker(typeEnvironment);
		if (!isFwdInfApplicable(checker, hyps, addedIdents, infHyps)) {
			return this;
		}

		return applyFwdInf(checker, hyps, infHyps);
	}

	@Override
	public IInternalProverSequent performRewrite(Collection<Predicate> hyps,
			FreeIdentifier[] addedIdents, Collection<Predicate> infHyps,
			Collection<Predicate> toHide) {

		final TypeChecker checker = new TypeChecker(typeEnvironment);
		if (!isFwdInfApplicable(checker, hyps, addedIdents, infHyps)) {
			return this;
		}

		return applyFwdInf(checker, hyps, infHyps).hideHypotheses(toHide);
	}

	@Override
	public String toString(){
		return (
				typeEnvironment.toString() +
				iterablePredToString(hiddenHypIterable()) +
				iterablePredToString(visibleMinusSelectedIterable()) +
				iterablePredToString(selectedHypIterable()) + " |- " +
				goal.toString());
	}
	
	private static String iterablePredToString(Iterable<Predicate> iterable){
		StringBuilder str = new StringBuilder("[");
		Iterator<Predicate> iterator = iterable.iterator();
		while (iterator.hasNext()){
			str.append((iterator.next()).toString());
			if (iterator.hasNext()){
				str.append(", ");
			}
		}
		str.append("]");
		return str.toString();
	}

	@Override
	public boolean containsHypothesis(Predicate pred) {
		if (localHypotheses.contains(pred)) return true;
		if (globalHypotheses.contains(pred)) return true;
		return false;
	}

	@Override
	public boolean containsHypotheses(Collection<Predicate> preds) {
		for (Predicate pred : preds) {
			if (! containsHypothesis(pred)) return false;
		}
		return true;
	}

	@Override
	public Iterable<Predicate> hypIterable() {
		return new Iterable<Predicate>(){

			@Override
			public Iterator<Predicate> iterator() {
				return new CompositeIterator<Predicate>(
						globalHypotheses.iterator(),
						localHypotheses.iterator());
			}
		};
	}
	
	@Override
	public Iterable<Predicate> hiddenHypIterable() {
		return new Iterable<Predicate>(){

			@Override
			public Iterator<Predicate> iterator() {
				return new ImmutableIterator<Predicate>(hiddenHypotheses);
			}
		};
	}


	@Override
	public Iterable<Predicate> selectedHypIterable() {
		return new Iterable<Predicate>(){

			@Override
			public Iterator<Predicate> iterator() {
				return new ImmutableIterator<Predicate>(selectedHypotheses);
			}
		};
	}

	@Override
	public boolean isHidden(Predicate hyp) {
		return hiddenHypotheses.contains(hyp);
	}

	@Override
	public boolean isSelected(Predicate hyp) {
		return selectedHypotheses.contains(hyp);
	}

	@Override
	public Iterable<Predicate> visibleHypIterable() {
		return new Iterable<Predicate>(){

			@Override
			public Iterator<Predicate> iterator() {
				return new DifferenceIterator<Predicate>(
						new CompositeIterator<Predicate>(
								globalHypotheses.iterator(),localHypotheses.iterator()),
						hiddenHypotheses);
			}
		};
	}

	public Iterable<Predicate> visibleMinusSelectedIterable() {
		return new Iterable<Predicate>(){

			@Override
			public Iterator<Predicate> iterator() {
				return new DifferenceIterator<Predicate>(
						visibleHypIterable().iterator(),
						selectedHypotheses);
			}
		};
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return typeEnvironment.getFormulaFactory();
	}
	
	@Override
	public Object getOrigin() {
		return origin;
	}
	
	/**
	 * An implementation for an iterator that is the combination of
	 * two (a first and a second) iterators.
	 * 
	 * <p>
	 * This iterator first returns the elements contained in the first iterator, followed
	 * by the elements contained in the second iterator.
	 * </p>
	 * <p>
	 * Removal of elements is unsupported for this iterator.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 * @param <T> 
	 * 		The base type for the elements returnded by the iterator.
	 */
	static class CompositeIterator<T> implements Iterator<T>{

		private Iterator<T> fst;
		private Iterator<T> snd;

		public CompositeIterator(Iterator<T> fst, Iterator<T> snd) {
			this.fst = fst;
			this.snd = snd;
		}

		@Override
		public boolean hasNext() {
			return fst.hasNext() || snd.hasNext();
		}

		@Override
		public T next() {
			if (fst.hasNext()) return fst.next();
			if (snd.hasNext()) return snd.next();
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}	
	}

	/**
	 * An implementation for an iterator that returns the elements of an 'original'
	 * iterator, without the elements in a 'removed' collection.
	 * 
	 * <p>
	 * This iterator is implemented by doing a pre-emptive lookup to check if the resulting
	 * iterator still has more elements.
	 * </p>
	 * <p>
	 * Removal of elements is unsupported for this iterator.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 * @param <T> 
	 * 		The base type for the elements returnded by the iterator.
	 */
	static class DifferenceIterator<T> implements Iterator<T>{

		private Iterator<T> iterator;
		private Collection<T> removed;

		/**
		 * This local variable contains the result of the pre-emptive lookup.
		 * If it is null, the iterator has no further elements, otherwise its
		 * value is the next next element for this iterator. 
		 */
		private T nextNext;


		public DifferenceIterator(Iterator<T> iterator, Collection<T> removed) {
			this.iterator = iterator;
			this.removed = removed;
			this.nextNext = nextNextLookup();
		}

		private T nextNextLookup(){
			while (iterator.hasNext()) {
				T next = (T) iterator.next();
				if (! removed.contains(next)) return next;
			}
			return null; 
		}

		@Override
		public boolean hasNext() {
			return (nextNext != null);
		}

		@Override
		public T next() {
			if (nextNext != null) {
				T next = nextNext;
				nextNext = nextNextLookup();
				return next;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}	
	}

	/**
	 * An implementation for an iterator that provides an immutable version of a 
	 * given iterator. 
	 * <p>
	 * Removal of elements is unsupported for this iterator.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 * @param <T> 
	 * 		The base type for the elements returnded by the iterator.
	 */
	static class ImmutableIterator<T> implements Iterator<T>{

		private Iterator<T> iterator;	

		public ImmutableIterator(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		public ImmutableIterator(Iterable<T> iterable) {
			this.iterator = iterable.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}		
	}
	
	private static LinkedHashSet<Predicate> translatePreds(
			Set<Predicate> preds, FormulaFactory factory) {
		final LinkedHashSet<Predicate> trPreds = new LinkedHashSet<Predicate>(
				preds.size());
		for (Predicate pred : preds) {
			trPreds.add(pred.translate(factory));
		}
		return trPreds;
	}

	@Override
	public IProverSequent translate(FormulaFactory factory) {
		final ITypeEnvironment trTypeEnv = typeEnvironment.translate(factory);

		final LinkedHashSet<Predicate> trGlobalHypotheses = translatePreds(
				globalHypotheses, factory);
		final LinkedHashSet<Predicate> trLocalHypotheses = translatePreds(
				localHypotheses, factory);

		final LinkedHashSet<Predicate> trHiddenHypotheses = translatePreds(
				hiddenHypotheses, factory);
		final LinkedHashSet<Predicate> trSelectedHypotheses = translatePreds(
				selectedHypotheses, factory);

		final Predicate trGoal = goal.translate(factory);

		return new ProverSequent(this, trTypeEnv, trGlobalHypotheses,
				trLocalHypotheses, trHiddenHypotheses, trSelectedHypotheses,
				trGoal);
	}
}
