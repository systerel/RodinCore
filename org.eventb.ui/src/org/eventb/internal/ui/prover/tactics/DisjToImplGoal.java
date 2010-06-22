/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.ui.prover.DefaultTacticProvider;

public class DisjToImplGoal extends DefaultTacticProvider {

	@Override
	@Deprecated
	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return Tactics.disjToImpl(null, position);
	}

	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node != null) {
			List<IPosition> positions = Tactics.disjToImplGetPositions(node
					.getSequent().goal());
			if (positions.size() == 0)
				return null;
			return positions;
		}
		return null;
	}

	@Override
	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		AssociativePredicate subFormula = (AssociativePredicate) predicate
				.getSubFormula(position);
		Predicate[] children = subFormula.getChildren();
		Predicate first = children[0];
		Predicate second = children[1];
		// Return the operator between the first and second child
		return getOperatorPosition(predStr,
				first.getSourceLocation().getEnd() + 1, second
						.getSourceLocation().getStart());
	}
}
