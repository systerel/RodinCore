/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.ast;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;

/**
 * Common abstraction for a substitute expression used in a substitution.
 * <p>
 * This class is not public API. Don't use it.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class Substitute {

	protected final FormulaFactory ff;

	/**
	 * Simple substitute where the expression doesn't contain any externally
	 * bound identifier.
	 */
	private static class SimpleSubstitute extends Substitute {

		Expression expr;

		public SimpleSubstitute(Expression expr, FormulaFactory ff) {
			super(ff);
			this.expr = expr;
		}

		@Override
		public Expression getSubstitute(Expression original, int nbOfInternallyBound) {
			return expr;
		}

		@Override
		public String toString() {
			return expr.toString();
		}

	}

	/**
	 * Susbtitute where the expression consists of a bound identifier.
	 * <p>
	 * This means that we must renumber the index of this identifier, taking
	 * into account the offset.
	 * </p>
	 * This class corresponds to an optimized simple version of
	 * {@link ComplexSubstitute}.
	 */
	private static class BoundIdentSubstitute extends Substitute {

		/**
		 * Index of the bound variable, before correction.
		 */
		final int index;

		public BoundIdentSubstitute(int index, FormulaFactory ff) {
			super(ff);
			this.index = index;
		}

		@Override
		public Expression getSubstitute(Expression original, int nbOfInternallyBound) {
			return ff.makeBoundIdentifier(
					index + nbOfInternallyBound,
					original.getSourceLocation(),
					original.getType());
		}

		@Override
		public String toString() {
			return "[[" + index + "]]";
		}

	}

	/**
	 * Complex substitute where the expression does contain some externally
	 * bound identifiers.
	 * <p>
	 * This means that we must renumber the index of these identifiers, taking
	 * into account the offset. To prevent doing that all the time, we use a
	 * small cache.
	 * </p>
	 */
	private static class ComplexSubstitute extends Substitute {

		/**
		 * Cache of substitute expressions, indexed by the offset applied to
		 * them. For instance, the first element of the list contains the
		 * original expression untouched.
		 */
		private Cache<Expression> cache;

		public ComplexSubstitute(Expression expr, FormulaFactory ff) {
			super(ff);
			this.cache = new Cache<Expression>();
			this.cache.set(0, expr);
		}

		@Override
		public Expression getSubstitute(Expression original, int nbOfInternallyBound) {
			Expression result = cache.get(nbOfInternallyBound);
			if (result == null) {
				Expression expr = cache.get(0);
				result = expr.shiftBoundIdentifiers(nbOfInternallyBound, ff);
				cache.set(nbOfInternallyBound, result);
			}
			return result;
		}

		@Override
		public String toString() {
			return cache.get(0).toString();
		}

	}

	/**
	 * Complex substitute where the expression does contain some externally
	 * bound identifiers, and where an additional offset needs to be applied.
	 */
	private static class ComplexSubstituteWithOffset extends ComplexSubstitute {

		private final int offset;

		public ComplexSubstituteWithOffset(Expression expr, int offset, FormulaFactory ff) {
			super(expr, ff);
			this.offset = offset;
		}

		@Override
		public Expression getSubstitute(Expression original, int nbOfInternallyBound) {
			return super.getSubstitute(original, nbOfInternallyBound + offset);
		}

		@Override
		public String toString() {
			return super.toString() + "with offset of " + offset;
		}

	}

	/**
	 * Factory method to create a substitute with an arbitrary expression.
	 * 
	 * @param expr
	 *            initial substitute expression
	 * @param ff
	 *            factory to used when building the actual substitute expression
	 * @return the substitute object for that expression
	 */
	public static Substitute makeSubstitute(Expression expr, FormulaFactory ff) {
		if (expr.isWellFormed()) {
			return new SimpleSubstitute(expr, ff);
		}
		return new ComplexSubstitute(expr, ff);
	}

	/**
	 * Factory method to create a substitute with an arbitrary expression and
	 * applying a constant offset to its bound identifiers.
	 * 
	 * @param expr
	 *            initial substitute expression
	 * @param offset
	 *            offset to systematically apply
	 * @param ff
	 *            factory to used when building the actual substitute expression
	 * @return the substitute object for that expression
	 */
	public static Substitute makeSubstitute(Expression expr, int offset, FormulaFactory ff) {
		return new ComplexSubstituteWithOffset(expr, offset, ff);
	}

	/**
	 * Factory method to create a substitute from a bound identifier.
	 * 
	 * @param index
	 *            initial index of the bound identifier
	 * @param ff
	 *            factory to use when building the actual substitute expression
	 * @return the substitute object for that identifier
	 */
	public static Substitute makeSubstitute(int index, FormulaFactory ff) {
		return new BoundIdentSubstitute(index, ff);
	}

	protected Substitute(FormulaFactory ff) {
		this.ff = ff;
	}

	/**
	 * Returns the substitute expression where bound identifier occurrences have
	 * been renumbered using the given offset.
	 * 
	 * @param original
	 *            original expression that gets substituted
	 * @param nbOfInternallyBound
	 *            offset to use, that is the number of identifiers bound between
	 *            the point where the substitute expression was given and the
	 *            place where it is used
	 * 
	 * @return the actual substitute expression to use
	 */
	public abstract Expression getSubstitute(Expression original, int nbOfInternallyBound);

}
