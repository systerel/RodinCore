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
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import static org.eventb.core.ast.Formula.IN;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Represents a membership predicate as used in the Membership Goal reasoner.
 * 
 * @author Laurent Voisin
 */
public class Membership {

	private final Predicate predicate;
	private final Expression member;
	private final Expression set;

	public Membership(Predicate predicate) {
		this.predicate = predicate;
		final RelationalPredicate rel = (RelationalPredicate) predicate;
		this.member = rel.getLeft();
		this.set = rel.getRight();
	}

	public Membership(Expression member, Expression set, FormulaFactory ff) {
		this.member = member;
		this.set = set;
		this.predicate = ff.makeRelationalPredicate(IN, member, set, null);
	}

	public Predicate predicate() {
		return predicate;
	}

	public Expression member() {
		return member;
	}

	public Expression set() {
		return set;
	}

	public boolean match(Predicate other) {
		return this.predicate.equals(other);
	}

	@Override
	public String toString() {
		return predicate.toString();
	}

	@Override
	public int hashCode() {
		return predicate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Membership other = (Membership) obj;
		return this.predicate.equals(other.predicate);
	}

}
