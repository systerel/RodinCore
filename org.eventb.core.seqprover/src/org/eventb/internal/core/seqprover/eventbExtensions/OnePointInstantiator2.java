/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * This class implements the algorithm for instantiating one or several bound
 * identifier in a quantified predicate.
 */
public abstract class OnePointInstantiator2<T extends Formula<T>> {

	public static Predicate instantiatePredicate(QuantifiedPredicate predicate,
			Expression[] replacements, FormulaFactory ff) {
		return new OnePointInstantiatorPredicate(predicate, replacements, ff)
				.instantiate();
	}

	private static final BoundIdentDecl[] NO_BOUND_IDENT_DECLS = new BoundIdentDecl[0];
	protected final FormulaFactory ff;

	protected final T input;
	protected final int tag;

	private final Expression[] replacements;
	protected BoundIdentDecl[] allDecls;

	// allDecls will be split into these two arrays.
	protected BoundIdentDecl[] outerDecls;
	protected BoundIdentDecl[] innerDecls;

	/**
	 * Initiates the instantiation of the given bound identifier by the given
	 * expression in the given quantified predicate or quantified expression.
	 * <p>
	 * The given expression must not contain any identifier bound more inward
	 * than the one to instantiate.
	 * </p>
	 * 
	 * @param input
	 *            the formula where the instantiation shall take place
	 * @param replacement
	 *            the expressions to instantiate the bound identifiers with
	 * @param ff
	 *            a formula factory for building the result
	 */
	public OnePointInstantiator2(T input, Expression[] replacements,
			FormulaFactory ff) {
		this.ff = ff;
		this.input = input;
		this.tag = input.getTag();
		this.replacements = replacements;
	}

	/**
	 * This method uses the instantiation methods defined in
	 * {@link QuantifiedExpression} and {@link QuantifiedPredicate}.
	 * <p>
	 * In case of an instantiation of one bound identifier by another bound
	 * identifier, we have to calculate a special shift value, in order to
	 * preserve the correctness in the bound identifier index values.
	 * </p>
	 * We first split the declaration of BoundIdentifiers in two sublists. The
	 * bound identifier that will be instantiated is the leftmost in the list of
	 * inner identifiers.
	 * 
	 * @return the instantiated predicate
	 */
	protected abstract T instantiate();

	/**
	 * Splits the bound identifier declarations into two sub-arrays (outerDecls
	 * and innerDecls), such that the bound identifier to replace is the
	 * leftmost declaration in innerDecls.
	 */
	protected void splitIdentDecls() {
		final int innerDeclsLength = allDecls.length
				- indexOfFirstReplacement();
		final int outerDeclsLength = allDecls.length - innerDeclsLength;
		outerDecls = new BoundIdentDecl[outerDeclsLength];
		innerDecls = new BoundIdentDecl[innerDeclsLength];
		System.arraycopy(allDecls, 0, outerDecls, 0, outerDeclsLength);
		System.arraycopy(allDecls, outerDeclsLength, innerDecls, 0,
				innerDeclsLength);
	}

	private int indexOfFirstReplacement() {
		for (int i = 0; i < replacements.length; i++) {
			if (replacements[i] != null) {
				return i;
			}
		}
		assert false;
		return -1;
	}

	/**
	 * @return the replacement array for instantiating the predicate quantifying
	 *         the inner declarations only.
	 */
	protected Expression[] getReplacements() {
		final int offset = innerDecls.length;
		final int firstRepIndex = indexOfFirstReplacement();
		final Expression[] replacements = new Expression[offset];
		for (int i = 0; i < replacements.length; i++) {
			Expression replacement = this.replacements[i + firstRepIndex];
			if (replacement == null) {
				replacements[i] = null;
			} else {
				replacements[i] = replacement
						.shiftBoundIdentifiers(-offset, ff);
			}
		}
		return replacements;
	}

	protected List<BoundIdentDecl> mergeIdentDecls(BoundIdentDecl[] outerDecls,
			BoundIdentDecl[] innerDecls) {
		final List<BoundIdentDecl> result = new ArrayList<BoundIdentDecl>();
		result.addAll(Arrays.asList(outerDecls));
		result.addAll(Arrays.asList(innerDecls));
		return result;
	}

	private static class OnePointInstantiatorPredicate extends
			OnePointInstantiator2<Predicate> {

		public OnePointInstantiatorPredicate(QuantifiedPredicate input,
				Expression[] replacements, FormulaFactory ff) {
			super(input, replacements, ff);
			this.allDecls = input.getBoundIdentDecls();
		}

		/**
		 * The inner predicate is instantiated with the inner BoundIdentifier
		 * list. After this calculation, we rebuild a predicate which
		 * corresponds to the instantiation of the inputPredicate with the
		 * replacement.
		 */
		@Override
		protected Predicate instantiate() {
			splitIdentDecls();
			final QuantifiedPredicate innerPred = ff.makeQuantifiedPredicate(
					tag, innerDecls,
					((QuantifiedPredicate) input).getPredicate(), null);
			final Predicate newInnerPred = innerPred.instantiate(
					getReplacements(), ff);
			if (outerDecls.length == 0) {
				return newInnerPred;
			}

			// Merge back the split declarations
			final Predicate newBasePred;
			final BoundIdentDecl[] newInnerDecls;
			if (newInnerPred instanceof QuantifiedPredicate) {
				final QuantifiedPredicate qPred = (QuantifiedPredicate) newInnerPred;
				newInnerDecls = qPred.getBoundIdentDecls();
				newBasePred = qPred.getPredicate();
			} else {
				newInnerDecls = NO_BOUND_IDENT_DECLS;
				newBasePred = newInnerPred;
			}
			final List<BoundIdentDecl> newDecls = mergeIdentDecls(outerDecls,
					newInnerDecls);
			return ff.makeQuantifiedPredicate(tag, newDecls, newBasePred, null);
		}

	}

}
