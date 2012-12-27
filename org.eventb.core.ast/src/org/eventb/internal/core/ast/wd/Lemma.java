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
package org.eventb.internal.core.ast.wd;

import java.util.Iterator;
import java.util.Set;

import org.eventb.core.ast.Predicate;

/**
 * A data structure used to store an implication in a form that allows to
 * compare implications and to define a relation of subsumption between
 * implications. </p>
 */
public class Lemma {

	final Set<Predicate> antecedents;
	final Predicate consequent;
	private final Node origin;

	public Lemma(Set<Predicate> antecedents, Predicate consequent, Node origin) {
		this.antecedents = antecedents;
		this.consequent = consequent;
		this.origin = origin;
	}

	/**
	 * Indicates whether some other lemma is "equal to" this lemma.
	 * <p>
	 * Two lemmas are equals if they got the same consequents and antecedents.
	 * </p>
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (this.getClass() != obj.getClass())
			return false;

		final Lemma other = (Lemma) obj;

		if (!antecedents.equals(other.antecedents))
			return false;

		if (!consequent.equals(other.consequent))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 1;
		final int prime = 37;
		result = prime * result + antecedents.hashCode();
		result = prime * result + consequent.hashCode();
		return result;
	}

	/**
	 * Checks if this lemma subsumes the given one. Subsumption is a partial
	 * order on lemmas. A lemma A subsumes another lemma B if and only if they
	 * have the same consequent and the antecedents of A are included in the
	 * antecedents of B.
	 * 
	 * @param other
	 *            other predicate to test for subsumption
	 * @return <code>true</code> iff this predicate subsumes the given predicate
	 */
	public boolean subsumes(Lemma other) {
		return this.consequent.equals(other.consequent)
				&& other.antecedents.containsAll(this.antecedents);
	}

	/**
	 * Marks this lemma as subsumed by another lemma.
	 */
	public void setSubsumed() {
		origin.setNodeSubsumed();
	}

	@Override
	public String toString() {
		return antecedents + " => " + consequent;
	}

	/**
	 * Adds this lemma to the given set, checking for subsumption.
	 * 
	 * @param set
	 *            set to which this lemma is added
	 */
	public void addToSet(Set<Lemma> set) {
		final Iterator<Lemma> iter = set.iterator();
		while (iter.hasNext()) {
			final Lemma other = iter.next();

			// Must never mark both lemmas as unused
			if (other.subsumes(this)) {
				this.setSubsumed();
				return;
			} else if (this.subsumes(other)) {
				iter.remove();
				other.setSubsumed();
			}
		}
		set.add(this);
	}

}