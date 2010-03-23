/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.rubin;

import org.eventb.core.ast.Predicate;

/**
 * Implementation of a sequent in pure predicate calculus.
 * 
 * @author Laurent Voisin
 */
public class Sequent {

	private final Predicate goal;
	private final Predicate[] hypotheses;
	private final String name;
	
	/**
	 * Creates a new sequent.
	 * 
	 * @param name name of the sequent. Must not be null
	 * @param hypotheses array of hypotheses of the sequent
	 * @param goal goal of the sequent
	 */
	public Sequent(String name, Predicate[] hypotheses, Predicate goal) {
		this.name = name;
		this.hypotheses = hypotheses;
		this.goal = goal;
	}

	/**
	 * @return Returns the goal of this sequent.
	 */
	public final Predicate getGoal() {
		return goal;
	}

	/**
	 * @return Returns the hypotheses of this sequent.
	 */
	public final Predicate[] getHypotheses() {
		return hypotheses;
	}

	/**
	 * @return Returns the name of this sequent.
	 */
	public final String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (! (obj instanceof Sequent)) {
			return false;
		}
		Sequent other = (Sequent) obj;
		if (! name.equals(other.name)) {
			return false;
		}
		if (! goal.equals(other.goal)) {
			return false;
		}
		if (hypotheses.length != other.hypotheses.length) {
			return false;
		}
		for (int i = 0; i < hypotheses.length; i++) {
			if (! hypotheses[i].equals(other.hypotheses[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if all hypotheses and the goal of this sequent have been
	 * type-checked.
	 * 
	 * @return <code>true</code> iff this sequent is type-checked
	 */
	public boolean isTypeChecked() {
		for (Predicate hyp : hypotheses) {
			if (! hyp.isTypeChecked()) {
				return false;
			}
		}
		return goal.isTypeChecked();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (name.length() != 0) {
			builder.append('"');
			builder.append(name);
			builder.append("\":\n");
		}
		String sep = "";
		for (Predicate hyp: hypotheses) {
			builder.append(sep);
			sep = ",\n";
			builder.append(hyp);
		}
		builder.append("\n|-");
		builder.append(goal);
		return builder.toString();
	}

}
