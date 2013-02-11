/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fully refactored code
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.RELIMAGE;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

public class CompImgRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".compImgRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return "Comp. img. rewrites in goal";
		return "Comp img. rewrites in hyp (" + pred + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	/**
	 * Set Theory : (p;...;q;r;...;s)[S] == (r;...;s)[(p;...;q)[S]]
	 */
	@Override
	@ProverRule("DERIV_RELIMAGE_FCOMP")
	public Predicate rewrite(Predicate pred, IPosition position,
			FormulaFactory ff) {
		if (position.isRoot())
			return null;
		final IPosition parentPos = position.getParent();
		if (parentPos.isRoot())
			return null;
		final IPosition grandParentPos = parentPos.getParent();
		final Formula<?> grandParent = pred.getSubFormula(grandParentPos);
		if (grandParent == null || grandParent.getTag() != RELIMAGE)
			return null;
		final BinaryExpression image = (BinaryExpression) grandParent;
		if (parentPos.getChildIndex() != 0)
			return null;
		final Formula<?> parent = image.getLeft();
		final Expression set = image.getRight();
		if (parent == null || parent.getTag() != FCOMP)
			return null;
		final AssociativeExpression fcomp = (AssociativeExpression) parent;
		final Expression[] children = fcomp.getChildren();
		final int index = position.getChildIndex();
		if (index < 1 || index >= children.length)
			return null;
		final Expression pToq = fcomp(children, 0, index, ff);
		final Expression rTos = fcomp(children, index, children.length, ff);
		final Expression newImage = relImage(rTos, relImage(pToq, set, ff), ff);
		return pred.rewriteSubFormula(grandParentPos, newImage);
	}

	private Expression fcomp(Expression[] children, int start, int end,
			FormulaFactory ff) {
		final int length = end - start;
		if (length == 1) {
			return children[start];
		}
		final Expression[] newChildren = new Expression[length];
		System.arraycopy(children, start, newChildren, 0, length);
		return ff.makeAssociativeExpression(FCOMP, newChildren, null);
	}

	private Expression relImage(Expression rel, Expression set,
			FormulaFactory ff) {
		return ff.makeBinaryExpression(RELIMAGE, rel, set, null);
	}

	public static List<IPosition> getPositions(Predicate predicate) {
		return predicate.inspect(new DefaultInspector<IPosition>() {
			@Override
			public void inspect(BinaryExpression expr,
					IAccumulator<IPosition> acc) {
				if (expr.getTag() != RELIMAGE) {
					return;
				}
				final Expression left = expr.getLeft();
				if (left.getTag() != FCOMP) {
					return;
				}
				final IPosition pos = acc.getCurrentPosition();
				final IPosition leftPos = pos.getFirstChild();
				final int count = left.getChildCount();
				for (int i = 1; i < count; ++i) {
					acc.add(leftPos.getChildAtIndex(i));
				}
			}
		});
	}

}
