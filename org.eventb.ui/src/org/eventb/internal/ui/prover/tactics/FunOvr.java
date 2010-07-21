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
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunOvr extends AbstractHypGoalTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.funOvr(hyp, position);
	}

	@Override
	public List<IPosition> retrievePositions(Predicate pred, FormulaFactory ff) {
		return Tactics.funOvrGetPositions(pred);
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		final Formula<?> subFormula = predicate.getSubFormula(position);
		final Expression left = ((BinaryExpression) subFormula).getLeft();
		final AssociativeExpression leftExpr = (AssociativeExpression) left;
		final Expression[] children = leftExpr.getChildren();
		final Expression last = children[children.length - 1];
		final Expression secondLast = children[children.length - 2];
		return getOperatorPosition(predStr, secondLast.getSourceLocation()
				.getEnd() + 1, last.getSourceLocation().getStart());
	}

}
