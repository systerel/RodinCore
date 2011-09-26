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
package org.eventb.internal.core.seqprover.eventbExtensions.tactics;

import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.IPosition.ROOT;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.totalDomGetSubstitutions;

import java.math.BigInteger;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomRewrites;

/**
 * Implementation for TotalDomToCprodTac.
 * 
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public class TotalDomToCprodTacImpl {

	private final IProverSequent sequent;
	private final Predicate goal;

	%include {FormulaV2.tom}

	public TotalDomToCprodTacImpl(IProverSequent sequent) {
		this.sequent = sequent;
		this.goal = sequent.goal();
	}

	/**
	 * Returns null if the goal does not possess the right shape, else it 
	 * returns the domain.
	 */
	private UnaryExpression checkGoal() {
		%match(goal) {
			In(Mapsto(_, _), dom@Dom(_)) -> {
				return (UnaryExpression) `dom;
			}
		}
		return null;
	}

	/**
	 * Returns a cartesian product as substitution for the given domain.
	 */
	private Expression getSubstitute(UnaryExpression domain) {
		final Set<Expression> substitutes = totalDomGetSubstitutions(
				this.sequent, domain.getChild());
		for (Expression substitute : substitutes) {
			%match (substitute) {
				Cprod(_, _) -> {
					return substitute;
				}
			}
		}
		return null;
	}

	/**
	 * Compute the input for the tactic. It returns null if the goal does not
	 * possess the right shape, or if the domain cannot be substituted by
	 * a Cartesian product, else it returns an input.
	 */
	public TotalDomRewrites.Input computeInput() {
		final UnaryExpression domain = checkGoal();
		if (domain == null) {
			return null;
		}
		final Expression substitute = getSubstitute(domain);
		if (substitute == null) {
			return null;
		}
		// Corresponding to the position "1"
		final IPosition pos = ROOT.getFirstChild().getNextSibling();
		return new TotalDomRewrites.Input(null, pos, substitute);
	}

}