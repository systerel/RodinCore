/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunCompImg extends AbstractHypGoalTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.funCompImg(hyp, position);
	}

	@Override
	public List<IPosition> retrievePositions(Predicate pred, FormulaFactory ff) {
		return Tactics.funCompImgGetPositions(pred);
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		final Formula<?> right = predicate.getSubFormula(position);
		final IPosition prevPosition = position.getPreviousSibling();
		final Formula<?> left = predicate.getSubFormula(prevPosition);
		return getOperatorPosition(predStr,
				left.getSourceLocation().getEnd() + 1, right
						.getSourceLocation().getStart());
	}

}
