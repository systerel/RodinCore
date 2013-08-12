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
package org.eventb.internal.core.seqprover.eventbExtensions.genmp;

import static org.eventb.core.seqprover.eventbExtensions.DLib.False;
import static org.eventb.core.seqprover.eventbExtensions.DLib.True;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isFalse;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isTrue;

import java.util.ArrayList;
import java.util.List;

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
	 * Returns a list of substitutes for the source predicate coming from a
	 * hypothesis or a goal.
	 * 
	 * @param origin
	 *            the hypothesis or goal predicate
	 * @param fromGoal
	 *            <code>true</code> if coming from goal
	 * @param source
	 *            the predicate to be substituted, modulo negation
	 * @return a list of fresh substitutes
	 */
	public static List<Substitute> makeSubstitutes(Predicate origin,
			boolean fromGoal, Predicate source) {
		final List<Substitute> result = new ArrayList<Substitute>();
		addSubstitute(result, origin, fromGoal, source);
		return result;
	}

	private static void addSubstitute(List<Substitute> substs,
			Predicate origin, boolean fromGoal, Predicate source) {
		final Predicate toReplace;
		final Predicate substitute;
		final FormulaFactory ff = origin.getFactory();
		if (isNeg(source)) {
			toReplace = makeNeg(source);
			substitute = fromGoal ? True(ff) : False(ff);
		} else {
			toReplace = source;
			substitute = fromGoal ? False(ff) : True(ff);
		}
		if (isTrue(toReplace) || isFalse(toReplace)) {
			return;
		}
		substs.add(new Substitute(origin, fromGoal, toReplace, substitute));
	}

	// the predicate from which this substitution comes (hypothesis or goal)
	private final Predicate origin;

	// Does it come from the goal
	private final boolean fromGoal;

	// the sub-predicate to be replaced
	private final Predicate toReplace;

	// the substitute (⊤ or ⊥) of the predicate to be replaced
	private final Predicate substitute;

	public Substitute(Predicate origin, boolean fromGoal, Predicate toReplace,
			Predicate substitute) {
		super();
		this.origin = origin;
		this.fromGoal = fromGoal;
		this.toReplace = toReplace;
		this.substitute = substitute;
	}

	public Predicate origin() {
		return origin;
	}

	boolean fromGoal() {
		return fromGoal;
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
		sb.append(fromGoal ? "goal: " : "hyp: ");
		sb.append(origin);
		sb.append(")");
		return sb.toString();
	}

}
