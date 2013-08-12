/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.DLib.False;
import static org.eventb.core.seqprover.eventbExtensions.DLib.True;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * Represents one substitution of a predicate into the sequent by ⊤ or ⊥ in the
 * GenMP reasoner.
 * 
 * @author Josselin Dolhen
 */
public class Substitute {

	/**
	 * Returns a substitute for the source predicate coming from a hypothesis or
	 * a goal.
	 * 
	 * @param origin
	 *            the hypothesis or goal predicate
	 * @param isGoal
	 *            <code>true</code> if coming from goal
	 * @param source
	 *            the predicate to be substituted, modulo negation
	 * @return a fresh substitution
	 */
	public static Substitute makeSubstitute(Predicate origin, boolean isGoal,
			Predicate source) {
		final Predicate toReplace;
		final Predicate substitute;
		final FormulaFactory ff = origin.getFactory();
		if (isNeg(source)) {
			toReplace = makeNeg(source);
			substitute = isGoal ? True(ff) : False(ff);
		} else {
			toReplace = source;
			substitute = isGoal ? False(ff) : True(ff);
		}
		return new Substitute(origin, isGoal, toReplace, substitute);
	}

	// the predicate from which this substitution comes (hypothesis or goal)
	private final Predicate origin;

	// Does it come from the goal
	private final boolean isGoal;

	// the sub-predicate to be replaced
	private final Predicate toReplace;

	// the substitute (⊤ or ⊥) of the predicate to be replaced
	private final Predicate substitute;

	public Substitute(Predicate origin, boolean isGoal, Predicate toReplace,
			Predicate substitute) {
		super();
		this.origin = origin;
		this.isGoal = isGoal;
		this.toReplace = toReplace;
		this.substitute = substitute;
	}

	public Predicate origin() {
		return origin;
	}

	public Predicate hypOrGoal() {
		if (isGoal) {
			return null;
		}
		return origin;
	}

	boolean fromGoal() {
		return isGoal;
	}

	public Predicate toReplace() {
		return toReplace;
	}

	public Predicate substitute() {
		return substitute;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Substitute ");
		sb.append(substitute);
		sb.append(" for ");
		sb.append(toReplace);
		sb.append(" (");
		sb.append(isGoal ? "goal: " : "hyp: ");
		sb.append(origin);
		sb.append(")");
		return sb.toString();
	}

}
