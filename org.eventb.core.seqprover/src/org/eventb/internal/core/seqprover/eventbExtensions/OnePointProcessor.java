/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
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
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.getMapletEqualities;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.match;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.matchAndDissociate;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.matchReplacement;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.MapletUtil;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.NormalFormUtil;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.ReplacementUtil;

/**
 * Framework for handling one point rules.
 * 
 * This class is used for various processing regarding inference rules
 * ONE_POINT_L and ONE_POINT_R as well as rewriting rules SIMP_IN_COMPSET and
 * SIMP_IN_COMPSET_ONEPOINT. It performs the following operations:
 * <ul>
 * <li>transforming the input predicate into a suitable form, if needed</li>
 * <li>finding the first replacement for a quantified bound identifier</li>
 * <li>deleting this replacement from the tree representing the predicate</li>
 * <li>instantiating the bound identifier with its corresponding expression in
 * the predicate</li>
 * </ul>
 * <p>
 * These operations are initiated in the method matchAndInstantiate, which is
 * the entry point for external accesses.</br>Two additional classes are used
 * here: {@link OnePointFilter} to take advantage of tom's matching abilities
 * and {@link OnePointInstantiator} for instantiation operations.
 * </p>
 * 
 * @author Nicolas Beauger
 * @author Thomas Müller
 * @author Benoît Lucet
 */
public class OnePointProcessor {

	final private FormulaFactory ff;

	final private QuantifiedPredicate qPred;
	final private BoundIdentDecl[] bids;

	private Predicate processingPredicate;

	private boolean replacementFound;
	private BoundIdentifier biReplaced;
	private Expression replacementExpression;

	private boolean successfullyApplied;

	public OnePointProcessor(QuantifiedPredicate predicate, FormulaFactory ff) {
		this.ff = ff;
		this.qPred = predicate;
		this.bids = qPred.getBoundIdentDecls();
		this.processingPredicate = qPred.getPredicate();
	}

	/**
	 * This constructor is used to transform the input predicate into a suitable
	 * form, that will be handled correctly by the method matchAndInstantiate.
	 * It will be most likely used when the input predicate matches the form
	 * required for rewriting rules SIMP_IN_COMPSET and
	 * SIMP_IN_COMPSET_ONEPOINT.
	 */
	public OnePointProcessor(RelationalPredicate predicate, FormulaFactory ff) {
		this.ff = ff;
		this.qPred = toNormalForm(predicate);
		this.bids = qPred.getBoundIdentDecls();
		this.processingPredicate = qPred.getPredicate();
	}

	public void matchAndInstantiate() {
		successfullyApplied = false;
		replacementFound = false;

		processingPredicate = matchAndSimplify(processingPredicate, true, true);

		if (!replacementFound) {
			return;
		}
		if (processingPredicate == null) {
			processingPredicate = ff.makeLiteralPredicate(BTRUE, null);
		}

		processingPredicate = instantiate();
		successfullyApplied = true;
	}

	/**
	 * Traverses the tree representing the given predicate, searching for
	 * replacements of the form <code>boundIdent = expression</code> or an
	 * equality on maplets. If a valid replacement is found, it is removed from
	 * the tree and the replacement data is kept.
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
			return processLimp((BinaryPredicate) pred, polarity, rootLevel);
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

	private Predicate instantiate() {
		final QuantifiedPredicate newQPred = ff.makeQuantifiedPredicate(
				qPred.getTag(), bids, processingPredicate, null);
		OnePointInstantiator ops = new OnePointInstantiator(newQPred,
				biReplaced, replacementExpression, ff);
		return ops.instantiate();
	}

	/**
	 * Converts the given predicate of the form E ∈ {x · P(x) ∣ E} in a suitable
	 * form for the processing to be applied (∃x · x=E ∧ P(x)).
	 * 
	 * TODO Benoît: rename
	 */
	private QuantifiedPredicate toNormalForm(RelationalPredicate predicate) {
		assert match(predicate);
		NormalFormUtil nfu = matchAndDissociate(predicate);

		final Predicate replacement = ff.makeRelationalPredicate(
				EQUAL,
				nfu.getExpression(),
				nfu.getElement().shiftBoundIdentifiers(
						nfu.getBoundIdents().length, ff), null);

		final Predicate conjunctionPred = ff.makeAssociativePredicate(LAND,
				new Predicate[] { replacement, nfu.getGuard() }, null);

		return ff.makeQuantifiedPredicate(EXISTS, nfu.getBoundIdents(),
				conjunctionPred, null);
	}

	/**
	 * If a LAND operator is encountered in a negative context, then no valid
	 * replacement can be found in its children.
	 */
	private Predicate processLand(AssociativePredicate pred, boolean polarity,
			boolean rootLevel) {
		if (rootLevel && qPred.getTag() == FORALL) {
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

	/**
	 * If a LIMP operator is encountered at a position that is not the root of
	 * the predicate, then no valid replacement can be found in its children.
	 */
	private Predicate processLimp(BinaryPredicate pred, boolean polarity,
			boolean rootLevel) {
		// TODO Benoît
		// if (rootLevel) {
		if (qPred.getTag() != EXISTS) {
			final Predicate left = matchAndSimplify(pred.getLeft(), polarity,
					false);
			final Predicate right = matchAndSimplify(pred.getRight(),
					!polarity, false);
			return processBinary(left, right, LIMP);
		}
		// }
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
		if (polarity && isAReplacement(pred)) {
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

	private boolean isAReplacement(RelationalPredicate pred) {
		ReplacementUtil ru = matchReplacement(pred);

		if (ru != null) {
			final BoundIdentifier bi = ru.getBiToReplace();
			final Expression expr = ru.getReplacementExpression();

			if (isValid(bi, expr)) {
				biReplaced = bi;
				replacementExpression = expr;
				return true;
			}
		}

		return false;
	}

	private boolean isValid(BoundIdentifier boundIdent, Expression expression) {
		final int idx = boundIdent.getBoundIndex();
		if (idx >= bids.length) {
			// Bound outside this quantifier
			return false;
		}
		for (final BoundIdentifier bi : expression.getBoundIdentifiers()) {
			if (bi.getBoundIndex() <= idx) {
				// BoundIdentifier in replacement expression is inner bound
				return false;
			}
		}
		return true;
	}

	private boolean isMapletEquality(RelationalPredicate pred) {
		return pred.getTag() == EQUAL && pred.getLeft().getTag() == MAPSTO
				&& pred.getRight().getTag() == MAPSTO;
	}

	/**
	 * Breaks an equality of maplets into a conjunction of equalities on the
	 * children of the maplets. For instance, a↦b = c↦d shall be rewritten in
	 * a=c ∧ b=d.
	 */
	private AssociativePredicate breakMaplet(RelationalPredicate pred) {
		assert isMapletEquality(pred);

		final List<Predicate> conjunction = new ArrayList<Predicate>();
		simpEqualsMapsTo(pred, conjunction);

		return ff.makeAssociativePredicate(LAND, conjunction, null);
	}

	private void simpEqualsMapsTo(Predicate predicate,
			final List<Predicate> conjuncts) {

		MapletUtil mu = getMapletEqualities(predicate, ff);

		if (mu != null) {
			simpEqualsMapsTo(mu.getLeftEquality(), conjuncts);
			simpEqualsMapsTo(mu.getRightEquality(), conjuncts);
		} else {
			conjuncts.add(predicate);
		}
	}

	public boolean wasSuccessfullyApplied() {
		return successfullyApplied;
	}

	public Predicate getProcessedPredicate() {
		assert successfullyApplied;
		return processingPredicate;
	}

	public Expression getReplacement() {
		assert successfullyApplied;
		return replacementExpression;
	}

	public QuantifiedPredicate getQPred() {
		return qPred;
	}

}
