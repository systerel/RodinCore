/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
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
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * This class implements the algorithm for instantiating a bound identifier in a
 * quantified predicate.
 */
public class OnePointInstantiator {

	private static final BoundIdentDecl[] NO_BOUND_IDENT_DECLS = new BoundIdentDecl[0];

	private final QuantifiedPredicate inputPredicate;
	private final BoundIdentifier boundIdent;
	private final Expression replacement;
	private final FormulaFactory ff;

	private final int tag;
	private final BoundIdentDecl[] allDecls;

	// allDecls will be split into these two arrays.
	private BoundIdentDecl[] outerDecls;
	private BoundIdentDecl[] innerDecls;

	/**
	 * Initiates the instantiation of the given bound identifier by the given
	 * expression in the given quantified predicate.
	 * <p>
	 * The given expression must not contain any identifier bound more inward
	 * than the one to instantiate.
	 * </p>
	 * 
	 * @param pred
	 *            the predicate where the instantiation shall take place
	 * @param boundId
	 *            the bound identifier to instantiate. Must be quantified at the
	 *            root of the given predicate
	 * @param replacement
	 *            the expression to instantiate with
	 * @param ff
	 *            a formula factory for building the result
	 */
	public OnePointInstantiator(QuantifiedPredicate pred, BoundIdentifier boundId,
			Expression replacement, FormulaFactory ff) {
		this.inputPredicate = pred;
		this.tag = pred.getTag();
		this.allDecls = pred.getBoundIdentDecls();
		this.boundIdent = boundId;
		this.replacement = replacement;
		this.ff = ff;

		assert boundIdent.getBoundIndex() < allDecls.length;
	}

	/**
	 * This method uses the instantiate method on Quantified predicates defined
	 * in {@link org.eventb.core.ast.QuantifiedPredicate}
	 * <p>
	 * In case of an instantiation of one BoundIdentifier by another
	 * boundIdentifier, we have to calculate a special shift value, in order to
	 * preserve the correctness in the BoundIdentifier index values.
	 * </p>
	 * We first split the declaration of BoundIdentifiers in two sublists. The
	 * bound identifier that will be instantiated is the leftmost in the list of
	 * inner identifiers. The inner predicate is instantiated with the inner
	 * BoundIdentifiers list. After this calculation, we rebuild a predicate
	 * which corresponds to the instantiation of the inputPredicate with the
	 * replacement.
	 * 
	 * @return the instantiated predicate
	 */
	public Predicate instantiate() {
		splitIdentDecls();

		final QuantifiedPredicate innerPred = ff.makeQuantifiedPredicate(tag,
				innerDecls, inputPredicate.getPredicate(), null);
		final Predicate newInnerPred = innerPred.instantiate(getReplacements(),
				ff);
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

	/**
	 * Splits the bound identifier declarations into two sub-arrays (outerDecls
	 * and innerDecls), such that the bound identifier to replace is the
	 * leftmost declaration in innerDecls.
	 */
	private void splitIdentDecls() {
		final int innerDeclsLength = boundIdent.getBoundIndex() + 1;
		final int outerDeclsLength = allDecls.length - innerDeclsLength;
		outerDecls = new BoundIdentDecl[outerDeclsLength];
		innerDecls = new BoundIdentDecl[innerDeclsLength];
		System.arraycopy(allDecls, 0, outerDecls, 0, outerDeclsLength);
		System.arraycopy(allDecls, outerDeclsLength, innerDecls, 0,
				innerDeclsLength);
	}

	/**
	 * @return the replacement array for instantiating the predicate quantifying
	 *         the inner declarations only.
	 */
	private Expression[] getReplacements() {
		final int offset = innerDecls.length;
		final Expression[] replacements = new Expression[offset];
		replacements[0] = replacement.shiftBoundIdentifiers(-offset, ff);
		return replacements;
	}

	private List<BoundIdentDecl> mergeIdentDecls(BoundIdentDecl[] outerDecls,
			BoundIdentDecl[] innerDecls) {
		final List<BoundIdentDecl> result = new ArrayList<BoundIdentDecl>();
		result.addAll(Arrays.asList(outerDecls));
		result.addAll(Arrays.asList(innerDecls));
		return result;
	}

}
