/*******************************************************************************
 * Copyright (c) 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.LIMP;

import java.util.List;

import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;

/**
 * This class allows to detect the use of an implication just below an
 * existential quantifier. This is most probably a mistake.
 */
public class ExistentialChecker extends DefaultInspector<SourceLocation> {

	/**
	 * Returns the source location of all implications occurring just below an
	 * existential quantifier within the given formula.
	 * 
	 * @param formula the formula to check
	 * @return the source locations of suspicious implications
	 */
	public static List<SourceLocation> check(Formula<?> formula) {
		if (formula instanceof BecomesEqualTo assignment) {
			return stream(assignment.getExpressions()) //
					.flatMap(e -> e.inspect(checker).stream()) //
					.collect(toList());
		} else if (formula instanceof BecomesMemberOf assignment) {
			return assignment.getSet().inspect(checker);
		} else if (formula instanceof BecomesSuchThat assignment) {
			return assignment.getCondition().inspect(checker);
		} else {
			return formula.inspect(checker);
		}
	}

	// Singleton instance
	private static final ExistentialChecker checker = new ExistentialChecker();

	private ExistentialChecker() {
		// no state, but keep it private.
	}

	@Override
	public void inspect(QuantifiedPredicate predicate, IAccumulator<SourceLocation> accumulator) {
		if (predicate.getTag() == EXISTS) {
			var child = predicate.getPredicate();
			if (child.getTag() == LIMP) {
				accumulator.add(child.getSourceLocation());
			}
		}
	}

}
