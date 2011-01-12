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
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointInstantiator2.instantiatePredicate;

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

	@Override
	public void matchAndInstantiate() {
		successfullyApplied = false;
		replacementFound = false;

		processing = matchAndSimplify(processing, true, true);

		if (!replacementFound) {
			return;
		}

		if (processing == null) {
			processing = ff.makeLiteralPredicate(BTRUE, null);
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
	 *            this parameter tells whether the current subtree was preceded
	 *            by a NOT operator
	 * @param rootLevel
	 *            this parameter tells whether the current tree is the root of
	 *            the predicate or not
	 * 
	 */
	private Predicate matchAndSimplify(Predicate pred, boolean polarity,
			boolean rootLevel) {

		if (replacementFound) {
			return pred;
		}

		switch (pred.getTag()) {
		case LAND:
			return processLand((AssociativePredicate) pred, polarity, rootLevel);
		case LOR:
			return processLor((AssociativePredicate) pred, polarity);
		case LIMP:
			return processLimp((BinaryPredicate) pred, polarity);
		case NOT:
			return processNot((UnaryPredicate) pred, polarity);
		case EQUAL:
			final RelationalPredicate rPred = (RelationalPredicate) pred;
			if (isMapletEquality(rPred)) {
				// case of maplet equality
				final AssociativePredicate conjuncts = breakMaplet(rPred);
				return matchAndSimplify(conjuncts, polarity, false);
			} else {
				// case of standard equality
				return processEqual((RelationalPredicate) pred, polarity);
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
	private Predicate processLand(AssociativePredicate pred, boolean polarity,
			boolean rootLevel) {
		if (rootLevel && original.getTag() == FORALL) {
			return pred;
		}
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
		} else {
			return processAssociative(pred, polarity, LOR);
		}
	}

	private Predicate processLimp(BinaryPredicate pred, boolean polarity) {
		if (original.getTag() != EXISTS) {
			final Predicate left = matchAndSimplify(pred.getLeft(), polarity,
					false);
			final Predicate right = matchAndSimplify(pred.getRight(),
					!polarity, false);
			return processBinary(left, right, LIMP);
		}
		return pred;
	}

	private Predicate processNot(UnaryPredicate pred, boolean polarity) {
		final Predicate newChild = matchAndSimplify(pred.getChild(), !polarity,
				false);
		if (newChild != null) {
			return ff.makeUnaryPredicate(NOT, newChild, null);
		}
		return ff.makeLiteralPredicate(BFALSE, null);
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
			final Predicate newChild = matchAndSimplify(child, polarity, false);
			if (newChild != null) {
				newChildren.add(newChild);
			}
		}

		if (newChildren.size() == 0) {
			return null;
		} else if (newChildren.size() == 1) {
			return newChildren.get(0);
		} else {
			return ff.makeAssociativePredicate(tag, newChildren, null);
		}
	}

	private Predicate processBinary(Predicate left, Predicate right, int tag) {
		if (left == null) {
			return right;
		} else if (right == null) {
			return left;
		} else {
			return ff.makeBinaryPredicate(tag, left, right, null);
		}
	}

	public Expression getReplacement() {
		assert successfullyApplied;
		for (int i = 0; i < replacements.length; i++) {
			if (replacements[i] != null) {
				return replacements[i];
			}
		}
		assert false;
		return null;
	}

}
