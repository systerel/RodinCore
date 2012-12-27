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

import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * Simplifies trivial equalities between two equal or unequal terms.
 * <p>
 * This implements the following rules :
 * <ul>
 * <li> <tt>x=x ∨ C to ⊤</tt> </li>
 * <li> <tt>x≠x ∨ C to C</tt> </li>
 * <li> <tt>x=x ⇔ C to C</tt> </li>
 * <li> <tt>x≠x ⇔ C to ¬C</tt> </li>
 * </ul>
 *
 * @author François Terrier
 *
 */
public class EqualitySimplifier extends AbstractSimplifier {

	private VariableContext context;
	
	public EqualitySimplifier(VariableContext context) {
		this.context = context;
	}
	
	@Override
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		if (simplifyEqualityDisj(equalities)) return cf.makeTRUE(clause.getOrigin());
		if (simplifyEqualityDisj(conditions)) return cf.makeTRUE(clause.getOrigin());
		
		if (isEmptyWithConditions()) return cf.makeFALSE(clause.getOrigin()); 
		else return cf.makeDisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}

	@Override
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		
		if (simplifyEqualityDisj(conditions)) return cf.makeTRUE(clause.getOrigin());
		boolean inverse = simplifyEqualityEq(equalities);
		if (!inverse && isEmptyWithoutConditions()) return cf.makeTRUE(clause.getOrigin());
		if (inverse && isEmptyWithConditions()) return cf.makeFALSE(clause.getOrigin());
		
		if (inverse) EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		return cf.makeClauseFromEquivalenceClause(clause.getOrigin(),predicates, equalities, arithmetic, conditions, context);
	}
	
	private boolean simplifyEqualityDisj(List<EqualityLiteral> list) {
		for (Iterator<EqualityLiteral> iter = list.iterator(); iter.hasNext();) {
			EqualityLiteral equality = iter.next();
			if (equal(equality)) {
				if (equality.isPositive()) return true;
				else iter.remove();
			}
		}
		return false;
	}
	
	private boolean simplifyEqualityEq(List<EqualityLiteral> list) {
		int inverse = 0;
		for (Iterator<EqualityLiteral> iter = list.iterator(); iter.hasNext();) {
			EqualityLiteral equality = iter.next();
			if (equal(equality)) {
				iter.remove();
				if (!equality.isPositive()) inverse++; 
			}
		}
		return inverse%2!=0;
	}
	
	private static boolean equal(EqualityLiteral equality) {
		return equality.getTerm(0).equals(equality.getTerm(1));
	}

	@Override
	public boolean canSimplify(Clause clause) {
		return clause.getEqualityLiteralsSize() > 0 || clause.getConditionsSize() > 0;
	}
}
