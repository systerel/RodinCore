/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.tactics;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.DefaultTacticProvider;

public abstract class AbstractHypGoalTacticProvider extends DefaultTacticProvider {
	
	@Override
	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (node == null) {
			return null;
		}
		final Predicate predicate;
		if (hyp != null) {
			predicate = hyp;
		} else {
			predicate = node.getSequent().goal();
		}
		return getPositions(predicate, node.getFormulaFactory());
	}
	
	public abstract List<IPosition> retrievePositions(Predicate pred, FormulaFactory ff);
	
	private List<IPosition> getPositions(Predicate pred, FormulaFactory ff) {
		final List<IPosition> positions = retrievePositions(pred, ff);
		if (positions.size() == 0) {
			return null;
		}
		return positions;
	}
		
}
