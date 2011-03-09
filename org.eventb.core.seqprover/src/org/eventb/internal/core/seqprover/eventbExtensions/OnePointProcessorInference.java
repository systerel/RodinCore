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
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointInstantiator.instantiatePredicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Handles the processing for inference rules ONE_POINT_L and ONE_POINT_R. The
 * following operations are performed:
 * <ul>
 * <li>finding the first replacement for a quantified bound identifier,</li>
 * <li>deleting this replacement from the tree representing the predicate,</li>
 * <li>instantiating the bound identifier with its corresponding expression in
 * the predicate.</li>
 * </ul>
 */
public class OnePointProcessorInference extends OnePointProcessor<Predicate> {

	private boolean replacementFound;

	public OnePointProcessorInference(QuantifiedPredicate qPred,
			FormulaFactory ff) {
		super(ff);
		this.original = qPred;
		this.bids = qPred.getBoundIdentDecls();
		this.processing = qPred.getPredicate();
		this.replacements = new Expression[bids.length];
	}

	public static Predicate rewrite(Predicate predicate, FormulaFactory ff) {
		if (!(predicate instanceof QuantifiedPredicate)) {
			return predicate;
		}
		final OnePointProcessorInference opp = new OnePointProcessorInference(
				(QuantifiedPredicate) predicate, ff);
		opp.matchAndInstantiate();
		if (opp.wasSuccessfullyApplied()) {
			return opp.getProcessedResult();
		}
		return predicate;
	}

	@Override
	public void matchAndInstantiate() {
		successfullyApplied = false;
		replacementFound = false;

		final boolean polarity = original.getTag() == EXISTS;
		processing = matchAndSimplify(processing, polarity);

		if (!replacementFound) {
			return;
		}

		if (processing == null) {
			processing = ff.makeLiteralPredicate(polarity ? BTRUE : BFALSE,
					null);
		}

		processing = instantiate(processing, replacements);
		successfullyApplied = true;
	}

	/**
	 * Traverses the tree representing the given predicate, searching for
	 * replacements of the form <code>boundIdent = expression</code> or
	 * <code> maplet1 = maplet2</code>If a valid replacement is found, it is
	 * removed from the tree and the replacement data is kept.
	 * 
	 * @param polarity
	 *            <code>true</code> if equality shall be examined for
	 *            replacement, this also influences the kind of operator that is
	 *            allowed (and, or, implication)
	 * @return the predicate minus the replacement, or <code>null</code> if
	 *         nothing left after removing the replacement (in which case, this
	 *         means <code>⊤</code> if the polarity is true, <code>⊥</code>
	 *         otherwise)
	 */
	private Predicate matchAndSimplify(Predicate pred, boolean polarity) {

		if (replacementFound) {
			return pred;
		}

		switch (pred.getTag()) {
		case LAND:
			return processLand((AssociativePredicate) pred, polarity);
		case LOR:
			return processLor((AssociativePredicate) pred, polarity);
		case LIMP:
			return processLimp((BinaryPredicate) pred, polarity);
		case NOT:
			return processNot((UnaryPredicate) pred, polarity);
		case EQUAL:
			if (!polarity) {
				return pred;
			}
			final RelationalPredicate rPred = (RelationalPredicate) pred;
			if (isMapletEquality(rPred)) {
				final List<Predicate> conjuncts = breakMaplet(rPred);
				final Predicate land = ff.makeAssociativePredicate(LAND,
						conjuncts, null);
				return matchAndSimplify(land, polarity);
			} else {
				return processEqual(rPred, polarity);
			}
		default:
			return pred;
		}
	}

	@Override
	protected Predicate instantiate(Predicate predicate,
			Expression[] replacements) {
		final QuantifiedPredicate newQPred = ff.makeQuantifiedPredicate(
				original.getTag(), bids, predicate, null);
		return instantiatePredicate(newQPred, replacements, ff);
	}

	/**
	 * If a LAND operator is encountered in a negative context, then no valid
	 * replacement can be found in its children.
	 */
	private Predicate processLand(AssociativePredicate pred, boolean polarity) {
		if (!polarity) {
			return pred;
		}
		return processAssociative(pred, polarity, LAND);
	}

	/**
	 * If a LOR operator is encountered in a positive context, then no valid
	 * replacement can be found in its children.
	 */
	private Predicate processLor(AssociativePredicate pred, boolean polarity) {
		if (polarity) {
			return pred;
		}
		return processAssociative(pred, polarity, LOR);
	}

	private Predicate processLimp(BinaryPredicate pred, boolean polarity) {
		if (polarity) {
			return pred;
		}
		final Predicate left = matchAndSimplify(pred.getLeft(), !polarity);
		final Predicate right = matchAndSimplify(pred.getRight(), polarity);
		if (left == null) {
			return right;
		}
		if (right == null) {
			return negate(left);
		}
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}

	private Predicate processNot(UnaryPredicate pred, boolean polarity) {
		final Predicate newChild = matchAndSimplify(pred.getChild(), !polarity);
		if (newChild == null) {
			return null;
		}
		return negate(newChild);
	}

	private Predicate negate(Predicate pred) {
		if (pred.getTag() == NOT) {
			return ((UnaryPredicate) pred).getChild();
		}
		return ff.makeUnaryPredicate(NOT, pred, null);
	}

	private Predicate processEqual(RelationalPredicate pred, boolean polarity) {
		if (polarity && checkReplacement(pred)) {
			replacementFound = true;
			return null;
		}
		return pred;
	}

	private Predicate processAssociative(AssociativePredicate pred,
			boolean polarity, int tag) {
		final List<Predicate> newChildren = new ArrayList<Predicate>();
		for (Predicate child : pred.getChildren()) {
			final Predicate newChild = matchAndSimplify(child, polarity);
			if (newChild != null) {
				newChildren.add(newChild);
			}
		}

		final int length = newChildren.size();
		if (length == 0) {
			return null;
		}
		if (length == 1) {
			return newChildren.get(0);
		}
		return ff.makeAssociativePredicate(tag, newChildren, null);
	}

	public Expression getReplacement() {
		assert successfullyApplied;
		for (Expression replacement : replacements) {
			if (replacement != null) {
				return replacement;
			}
		}
		assert false;
		return null;
	}

}
