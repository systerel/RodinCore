/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - extracted from ForallmpD provider
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;

/**
 * Abstract class for AllmpD and AllmtD applications.
 */
public abstract class ForallFollowedApplication extends
		DefaultPositionApplication {

	public ForallFollowedApplication(Predicate hyp) {
		super(hyp, IPosition.ROOT);
	}

	@Override
	public Point getHyperlinkBounds(String parsedString,
			Predicate parsedPredicate) {
		return getOperatorPosition(parsedPredicate, parsedString);
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr) {
		final Formula<?> subFormula = predicate.getSubFormula(position);
		final Predicate pred = (Predicate) subFormula;
		final QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
		final BinaryPredicate impPred = (BinaryPredicate) qPred.getPredicate();

		final Predicate left = impPred.getLeft();
		final Predicate right = impPred.getRight();
		return getOperatorPosition(predStr,
				left.getSourceLocation().getEnd() + 1, right
						.getSourceLocation().getStart());
	}

}
