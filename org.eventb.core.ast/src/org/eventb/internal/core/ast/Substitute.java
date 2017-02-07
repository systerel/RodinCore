/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Make this class generic
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;

/**
 * Common abstraction for a substitute formula used in a substitution.
 * <p>
 * This class is not public API. Don't use it.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class Substitute<T extends Formula<T>> {

	/**
	 * Simple substitute where the formula doesn't contain any externally
	 * bound identifier.
	 */
	private static class SimpleSubstitute<T extends Formula<T>>
			extends Substitute<T> {

		T formula;

		public SimpleSubstitute(T formula) {
			this.formula = formula;
		}

		@Override
		public T getSubstitute(T original, int nbOfInternallyBound) {
			return formula;
		}

		@Override
		public int hashCode() {
			return formula.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			@SuppressWarnings("unchecked")
			final SimpleSubstitute<T> other = (SimpleSubstitute<T>) obj;
			return formula.equals(other.formula);
		}

		@Override
		public String toString() {
			return formula.toString();
		}

	}

	/**
	 * Susbtitute where the formula consists of a bound identifier.
	 * <p>
	 * This means that we must renumber the index of this identifier, taking
	 * into account the offset.
	 * </p>
	 * This class corresponds to an optimized simple version of
	 * {@link org.eventb.internal.core.ast.Substitute.ComplexSubstitute
	 * ComplexSubstitute}.
	 */
	private static class BoundIdentSubstitute extends Substitute<Expression> {

		/**
		 * Index of the bound variable, before correction.
		 */
		final int index;

		public BoundIdentSubstitute(int index) {
			this.index = index;
		}

		@Override
		public Expression getSubstitute(Expression original, int nbOfInternallyBound) {
			return original.getFactory().makeBoundIdentifier(
					index + nbOfInternallyBound, original.getSourceLocation(),
					original.getType());
		}

		@Override
		public int hashCode() {
			return index;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			final BoundIdentSubstitute other = (BoundIdentSubstitute) obj;
			return index == other.index;
		}

		@Override
		public String toString() {
			return "[[" + index + "]]";
		}

	}

	/**
	 * Complex substitute where the formula does contain some externally
	 * bound identifiers.
	 * <p>
	 * This means that we must renumber the index of these identifiers, taking
	 * into account the offset. To prevent doing that all the time, we use a
	 * small cache.
	 * </p>
	 */
	private static class ComplexSubstitute<T extends Formula<T>>
			extends Substitute<T> {

		/**
		 * Cache of substitute formulas, indexed by the offset applied to
		 * them. For instance, the first element of the list contains the
		 * original formula untouched.
		 */
		private Cache<T> cache;

		public ComplexSubstitute(T formula) {
			this.cache = new Cache<T>();
			this.cache.set(0, formula);
		}

		@Override
		public T getSubstitute(T original, int nbOfInternallyBound) {
			T result = cache.get(nbOfInternallyBound);
			if (result == null) {
				T formula = cache.get(0);
				result = formula.shiftBoundIdentifiers(nbOfInternallyBound);
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
			@SuppressWarnings("unchecked")
			final ComplexSubstitute<T> other = (ComplexSubstitute<T>) obj;
			return equalFormulas(other);
		}

		// Test extracted to a method for subclass comparison
		protected boolean equalFormulas(ComplexSubstitute<T> other) {
			return cache.get(0).equals(other.cache.get(0));
		}

		@Override
		public String toString() {
			return cache.get(0).toString();
		}

	}

	/**
	 * Complex substitute where the formula does contain some externally
	 * bound identifiers, and where an additional offset needs to be applied.
	 */
	private static class ComplexSubstituteWithOffset<T extends Formula<T>>
			extends ComplexSubstitute<T> {

		private final int offset;

		public ComplexSubstituteWithOffset(T formula, int offset) {
			super(formula);
			this.offset = offset;
		}

		@Override
		public T getSubstitute(T original, int nbOfInternallyBound) {
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
			@SuppressWarnings("unchecked")
			final ComplexSubstituteWithOffset<T> other = (ComplexSubstituteWithOffset<T>) obj;
			return equalFormulas(other) && offset == other.offset;
		}

		@Override
		public String toString() {
			return super.toString() + "with offset of " + offset;
		}

	}

	/**
	 * Factory method to create a substitute with an arbitrary formula.
	 * 
	 * @param formula
	 *            initial substitute formula
	 * @return the substitute object for that formula
	 */
	public static <T extends Formula<T>> Substitute<T> makeSubstitute(T formula) {
		if (formula.isWellFormed()) {
			return new SimpleSubstitute<T>(formula);
		}
		return new ComplexSubstitute<T>(formula);
	}

	/**
	 * Factory method to create a substitute with an arbitrary formula and
	 * applying a constant offset to its bound identifiers.
	 * 
	 * @param formula
	 *            initial substitute formula
	 * @param offset
	 *            offset to systematically apply
	 * @return the substitute object for that formula
	 */
	public static <T extends Formula<T>> Substitute<T> makeSubstitute(T formula,
			int offset) {
		return new ComplexSubstituteWithOffset<T>(formula, offset);
	}

	/**
	 * Factory method to create a substitute from a bound identifier.
	 * 
	 * @param index
	 *            initial index of the bound identifier
	 * @return the substitute object for that identifier
	 */
	public static Substitute<Expression> makeSubstitute(int index) {
		return new BoundIdentSubstitute(index);
	}

	/**
	 * Returns the substitute formula where bound identifier occurrences have
	 * been renumbered using the given offset.
	 * 
	 * @param original
	 *            original formula that gets substituted
	 * @param nbOfInternallyBound
	 *            offset to use, that is the number of identifiers bound between
	 *            the point where the substitute formula was given and the
	 *            place where it is used
	 * 
	 * @return the actual substitute formula to use
	 */
	public abstract T getSubstitute(T original, int nbOfInternallyBound);

}
