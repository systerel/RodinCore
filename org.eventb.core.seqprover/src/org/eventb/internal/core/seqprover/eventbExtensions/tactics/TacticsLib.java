/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.core.seqprover.eventbExtensions.tactics;


import java.util.List;

import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * This is a collection of static constants and methods that are used in
 * relation with tactics.
 * 
 */
public class TacticsLib {

	/**
	 * Creates an instance of InDomManager for the first domain occurrence in
	 * the given goal.
	 * <p>
	 * The given goal MUST have at least one occurrence of a domain. In case
	 * several occurrences are found, the first one is considered.
	 * </p>
	 * 
	 * @param goal
	 *            Goal of the sequent
	 * @return a set of InDomManager
	 */
	public static InDomGoalManager createInDomManager(final Predicate goal) {
		final List<IPosition> domPositions = TacticsLib.findDomExpression(goal);
		assert !domPositions.isEmpty();
		final UnaryExpression domExpr = ((UnaryExpression) goal
					.getSubFormula(domPositions.get(0)));
		final InDomGoalManager inDomMng = new InDomGoalManager(domExpr,
				domPositions.get(0));		
		return inDomMng;
	}

	/**
	 * Finds total domain expressions in a predicate
	 * 
	 * @param pred
	 *            a predicate
	 * @return list of total domain expression positions
	 */
	private static List<IPosition> findDomExpression(Predicate pred) {
		final List<IPosition> domPositions = pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(UnaryExpression expression) {
				return (Lib.isDom(expression) && expression.isWellFormed());
			}
		});
		Lib.removeWDUnstrictPositions(domPositions, pred);
		return domPositions;
	}

}
