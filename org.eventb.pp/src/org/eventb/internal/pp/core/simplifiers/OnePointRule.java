/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * This simplifier applies the one-point rule to a clause.
 *
 * @author François Terrier
 *
 */
public class OnePointRule extends AbstractSimplifier {

	@Override
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		
		for (EqualityLiteral equality : equalities) {
			if (isAlwaysTrue(equality)) return cf.makeTRUE(clause.getOrigin());
		}
		
		onePointLoop(equalities);
		onePointLoop(conditions);
		if (isEmptyWithConditions()) return cf.makeFALSE(clause.getOrigin());
		else return cf.makeDisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}

	@Override
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		onePointLoop(conditions);
		
		// never empty
		return cf.makeEquivalenceClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}
	
	private void onePointLoop(List<EqualityLiteral> candidates) {
		// choose a candidate
		int i = 0;
		while (candidates.size() > i) {
			EqualityLiteral equality = candidates.get(i);
			if (isOnePointCandidate(equality)) {
				candidates.remove(equality);
				doOnePoint(equality);
			}
			else {
				i++;
			}
		}
	}
	
	private void doOnePoint(EqualityLiteral equality) {
		assert isOnePointCandidate(equality);
		
		Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		Variable variable = getOnePointVariable(equality);
		map.put(variable, getOnePointTerm(equality,variable));
		
		doOnePointHelper(predicates, map);
		doOnePointHelper(equalities, map);
		doOnePointHelper(arithmetic, map);
		doOnePointHelper(conditions, map);
	}
	
	protected <T extends Literal<T,?>> void doOnePointHelper(List<T> list, Map<SimpleTerm, SimpleTerm> map) {
		ArrayList<T> tmp1 = new ArrayList<T>();
		for (Literal<T,?> literal : list) {
			tmp1.add(literal.substitute(map));
		}
		list.clear();
		list.addAll(tmp1);
	}

	private SimpleTerm getOnePointTerm(EqualityLiteral equality, Variable variable) {
		assert isOnePointCandidate(equality);
		SimpleTerm result;
		SimpleTerm term1 = equality.getTerm(0);
		SimpleTerm term2 = equality.getTerm(1);
		
		if (term1 == variable) result = term2;
		else result = term1;
		
		return result;
	}

	private Variable getOnePointVariable(EqualityLiteral equality) {
		assert isOnePointCandidate(equality);
		Term term1 = equality.getTerm(0);
		Term term2 = equality.getTerm(1);
		if (term1 instanceof Variable) return (Variable)term1;
		if (term2 instanceof Variable) return (Variable)term2;
		assert false;
		return null;
	}

	private boolean isAlwaysTrue(EqualityLiteral equality) {
		if (equality.isPositive()) {
			Term term1 = equality.getTerm(0);
			Term term2 = equality.getTerm(1);
			if (!term1.isQuantified() && !term2.isQuantified()) return false;
			if (term1 instanceof LocalVariable) {
				return !term2.contains((SimpleTerm)term1);
			}
			if (term2 instanceof LocalVariable) {
				return !term1.contains((SimpleTerm)term2);
			}
			return false;
		}
		return false;
	}
	
	private boolean isOnePointCandidate(EqualityLiteral equality) {
		if (!equality.isPositive()) {
			Term term1 = equality.getTerm(0);
			Term term2 = equality.getTerm(1);
			if (term1.isQuantified()) return false;
			if (term2.isQuantified()) return false;
			if (term1 instanceof Variable) {
				return !term2.contains((SimpleTerm)term1);
			}
			if (term2 instanceof Variable) {
				return !term1.contains((SimpleTerm)term2);
			}
		}
		return false;
	}

	@Override
	public boolean canSimplify(Clause clause) {
		return !clause.isFalse();
	}
}
