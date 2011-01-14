/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.matchReplacement;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.splitMapletEquality;

import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.ReplacementUtil;

/**
 * Framework for handling one point rules.
 * 
 * This class is used for various processing regarding inference rules
 * ONE_POINT_L and ONE_POINT_R as well as rewriting rules SIMP_IN_COMPSET,
 * SIMP_IN_COMPSET_ONEPOINT and SIMP_COMPSET_EQUAL.
 * <ul>
 * <li>for inference rules ONE_POINT_L and ONE_POINT_R, the processing is done
 * in {@link OnePointProcessorInference}</li>
 * <li>for rewriting rules SIMP_IN_COMPSET and SIMP_IN_COMPSET_ONEPOINT, the
 * processing is done in {@link OnePointProcessorRewriting}</li>
 * <li>for rewriting rules SIMP_COMPSET_EQUAL, the processing is done in
 * {@link OnePointProcessorExpression}</li>
 * </ul>
 * <p>
 * These operations are initiated in the method matchAndInstantiate, which is
 * the entry point for external accesses.
 * </p>
 * 
 * @author Nicolas Beauger
 * @author Thomas Müller
 * @author Benoît Lucet
 */
public abstract class OnePointProcessor<T extends Formula<T>> {

	final protected FormulaFactory ff;

	protected T original;
	protected BoundIdentDecl[] bids;
	protected T processing;

	/*
	 * The replacements are held in a Expression array, where each index
	 * corresponds to a bound identifier's declaration index and the value
	 * corresponds to its replacement. This structure is adopted because it is
	 * more convenient for the instantiation.
	 */
	protected Expression[] replacements;

	protected boolean successfullyApplied;

	public OnePointProcessor(FormulaFactory ff) {
		this.ff = ff;
	}

	public abstract void matchAndInstantiate();

	/**
	 * Performs one or several instantiations, by calling
	 * {@link OnePointInstantiator2}.
	 */
	protected abstract T instantiate(T formula, Expression[] replacements);

	/**
	 * Given a predicate with tag EQUAL, returns whether or not this predicate
	 * is a valid replacement. If it is, then the replacement is stored for the
	 * forthcoming instantiation.
	 */
	protected boolean checkReplacement(RelationalPredicate pred) {
		ReplacementUtil ru = matchReplacement(pred);

		if (ru != null) {
			final BoundIdentifier bi = ru.getBiToReplace();
			final Expression expr = ru.getReplacementExpression();

			if (isValidReplacement(bi, expr)) {
				/*
				 * Given the predicate ∀x,y,z · P(x,y,z) as an example, the De
				 * Bruijn indexes for x,y,z are respectively 2,1,0 and the
				 * identifier declaration's indexes are respectively 0,1,2.
				 * Therefore, the inversion
				 * "replacementIndex = declaration.length - 1 - DeBruijnIndex"
				 * has to be made.
				 */
				final int index = bids.length - 1 - bi.getBoundIndex();
				/*
				 * When several replacements occur on the same bound identifier,
				 * only the first is memorized (may happen in replacements of
				 * the form a↦b = x↦x).
				 */
				if (replacements[index] == null) {
					replacements[index] = expr;
					return true;
				}
			}
		}
		return false;
	}

	private boolean isValidReplacement(BoundIdentifier boundIdent,
			Expression expression) {
		final int idx = boundIdent.getBoundIndex();
		if (idx >= bids.length) {
			// Bound outside this quantifier.
			return false;
		}
		for (final BoundIdentifier bi : expression.getBoundIdentifiers()) {
			if (bi.getBoundIndex() <= idx) {
				// BoundIdentifier in replacement expression is inner bound.
				return false;
			}
		}
		return true;
	}

	protected boolean isMapletEquality(RelationalPredicate pred) {
		return OnePointFilter.isMapletEquality(pred);
	}

	/**
	 * Breaks an equality of maplets into a list of equalities between the
	 * children of the maplets. For instance, a↦b = c↦d shall be rewritten in
	 * a=c ∧ b=d.
	 */
	protected List<Predicate> breakMaplet(RelationalPredicate pred) {
		return splitMapletEquality(pred, ff);
	}

	protected boolean availableReplacement() {
		for (Expression rep : replacements) {
			if (rep != null) {
				return true;
			}
		}
		return false;
	}

	public boolean wasSuccessfullyApplied() {
		return successfullyApplied;
	}

	public T getProcessedResult() {
		assert successfullyApplied;
		return processing;
	}
}
