/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Predicate;

/**
 * Common abstraction for a substitute predicate used in a substitution.
 * <p>
 * This class is not public API. Don't use it.
 * </p>
 * <p>
 * This is adapted from {@link Substitute} for predicates.
 * </p>
 * 
 * @author htson
 */
public abstract class PredicateSubstitute {

	/**
	 * Simple substitute where the predicate doesn't contain any externally
	 * bound identifier.
	 */
	private static class SimpleSubstitute extends PredicateSubstitute {

		Predicate pred;

		public SimpleSubstitute(Predicate pred) {
			this.pred = pred;
		}

		@Override
		public Predicate getSubstitute(Predicate original, int nbOfInternallyBound) {
			return pred;
		}

		@Override
		public int hashCode() {
			return pred.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			final SimpleSubstitute other = (SimpleSubstitute) obj;
			return pred.equals(other.pred);
		}

		@Override
		public String toString() {
			return pred.toString();
		}

	}


	/**
	 * Complex substitute where the predicate does contain some externally
	 * bound identifiers.
	 * <p>
	 * This means that we must re-number the index of these identifiers, taking
	 * into account the offset. To prevent doing that all the time, we use a
	 * small cache.
	 * </p>
	 */
	private static class ComplexSubstitute extends PredicateSubstitute {

		/**
		 * Cache of substitute predicates, indexed by the offset applied to
		 * them. For instance, the first element of the list contains the
		 * original predicate untouched.
		 */
		private Cache<Predicate> cache;

		public ComplexSubstitute(Predicate pred) {
			this.cache = new Cache<Predicate>();
			this.cache.set(0, pred);
		}

		@Override
		public Predicate getSubstitute(Predicate original, int nbOfInternallyBound) {
			Predicate result = cache.get(nbOfInternallyBound);
			if (result == null) {
				Predicate pred = cache.get(0);
				result = pred.shiftBoundIdentifiers(nbOfInternallyBound);
				cache.set(nbOfInternallyBound, result);
			}
			return result;
		}

		@Override
		public int hashCode() {
			return cache.get(0).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			return equalPredicates((ComplexSubstitute) obj);
		}

		// Test extracted to a method for subclass comparison
		protected boolean equalPredicates(ComplexSubstitute other) {
			return cache.get(0).equals(other.cache.get(0));
		}

		@Override
		public String toString() {
			return cache.get(0).toString();
		}

	}

	/**
	 * Complex substitute where the predicate does contain some externally
	 * bound identifiers, and where an additional offset needs to be applied.
	 */
	private static class ComplexSubstituteWithOffset extends ComplexSubstitute {

		private final int offset;

		public ComplexSubstituteWithOffset(Predicate pred, int offset) {
			super(pred);
			this.offset = offset;
		}

		@Override
		public Predicate getSubstitute(Predicate original, int nbOfInternallyBound) {
			return super.getSubstitute(original, nbOfInternallyBound + offset);
		}

		@Override
		public int hashCode() {
			return super.hashCode() + 31 * offset;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			final ComplexSubstituteWithOffset other = (ComplexSubstituteWithOffset) obj;
			return equalPredicates(other) && offset == other.offset;
		}

		@Override
		public String toString() {
			return super.toString() + "with offset of " + offset;
		}

	}

	/**
	 * Factory method to create a substitute with an arbitrary predicate.
	 * 
	 * @param pred
	 *            initial substitute predicate
	 * @return the substitute object for that predicate
	 */
	public static PredicateSubstitute makeSubstitute(Predicate pred) {
		if (pred.isWellFormed()) {
			return new SimpleSubstitute(pred);
		}
		return new ComplexSubstitute(pred);
	}

	/**
	 * Factory method to create a substitute with an arbitrary predicate and
	 * applying a constant offset to its bound identifiers.
	 * 
	 * @param pred
	 *            initial substitute predicate
	 * @param offset
	 *            offset to systematically apply
	 * @return the substitute object for that predicate
	 */
	public static PredicateSubstitute makeSubstitute(Predicate pred, int offset) {
		return new ComplexSubstituteWithOffset(pred, offset);
	}

	/**
	 * Returns the substitute predicate where bound identifier occurrences have
	 * been renumbered using the given offset.
	 * 
	 * @param original
	 *            original predicate that gets substituted
	 * @param nbOfInternallyBound
	 *            offset to use, that is the number of identifiers bound between
	 *            the point where the substitute predicate was given and the
	 *            place where it is used
	 * 
	 * @return the actual substitute predicate to use
	 */
	public abstract Predicate getSubstitute(Predicate original, int nbOfInternallyBound);

}
