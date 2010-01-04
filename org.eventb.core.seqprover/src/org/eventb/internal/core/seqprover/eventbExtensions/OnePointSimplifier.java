/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - refactored to fix instantiation of the One Point Rule
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilterUtils.Replacement;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilterUtils.ToProcessStruct;

public class OnePointSimplifier {

	private final Predicate predicate;
	private final Predicate replacementPredicate;
	private final FormulaFactory ff;
	private Replacement replacement;
	private Predicate processedPredicate;
	private boolean successfullyApplied = false;

	/**
	 * @param predicate
	 *            the predicate to apply one point rule to
	 * @param ff
	 *            a formula factory
	 */
	public OnePointSimplifier(Predicate predicate, FormulaFactory ff) {
		this(predicate, null, ff);
	}

	/**
	 * @param predicate
	 *            the predicate to apply one point rule to
	 * @param replacementPredicate
	 *            a replacement predicate to use when applying one point rule;
	 *            must be of the form "x = E" where x is a bound identifier and
	 *            must be found in the predicate
	 * @param ff
	 *            a formula factory
	 */
	public OnePointSimplifier(Predicate predicate,
			Predicate replacementPredicate, FormulaFactory ff) {
		this.predicate = predicate;
		this.replacementPredicate = replacementPredicate;
		this.ff = ff;
	}

	public boolean wasSuccessfullyApplied() {
		return successfullyApplied;
	}

	/**
	 * Returns the replacement expression found.
	 * <p>
	 * The One Point Rule must have been successfully applied.
	 * </p>
	 * 
	 * @return the replacement expression
	 */
	public Expression getReplacement() {
		assert successfullyApplied;
		return replacement.getReplacement();
	}

	/**
	 * Get the predicate resulting from One Point Rule application.
	 * <p>
	 * The One Point Rule Smust have been successfully applied.
	 * </p>
	 * 
	 * @return the resulting predicate after One Point Simplification
	 */
	public Predicate getProcessedPredicate() {
		assert successfullyApplied;
		return processedPredicate;
	}

	/**
	 * Matches and applies the One Point Rule simplification.
	 */	
	public void matchAndApply() {
		successfullyApplied = false;
		final ToProcessStruct struct = OnePointFilterUtils.match(predicate);
		if (struct == null) {
			return;
		}
		replacement = OnePointUtils.findReplacement(struct.conjuncts, struct.identDecls,
				replacementPredicate);
		if (replacement == null) {
			return;
		}
		processedPredicate = OnePointUtils.processReplacement(predicate
				.getTag(), struct, replacement, ff);
		successfullyApplied = true;
	}

}