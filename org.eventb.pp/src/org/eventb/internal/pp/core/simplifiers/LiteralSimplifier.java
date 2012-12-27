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

import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * This simplifier removes all duplicate predicate, arithmetic or equality literals
 * in a clause.
 * <p>
 * The following rules are handled by this simplifier:
 * <ul>
 * <li> <tt>P ∨ ¬ P ∨ C to ⊤</tt> </li>
 * <li> <tt>P ∨ P ∨ C to P ∨ C</tt> </li>
 * <li> <tt>P ⇔ ¬P ⇔ C to ¬C</tt> </li>
 * <li> <tt>P ⇔ P ⇔ C to C</tt> </li>
 * </ul>
 * Conditions are also simplified in the same way. If the clause is disjunctive,
 * conditions can be simplified with the equalities.
 *
 * @author François Terrier
 *
 */
public class LiteralSimplifier extends AbstractSimplifier {
	
	private final VariableContext context;
	
	public LiteralSimplifier(VariableContext context) {
		this.context = context;
	}
	
	@Override
	public boolean canSimplify(Clause clause) {
		if (!clause.isUnit()) return true;
		return false;
	}
	
	@Override
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		
		if (getDisjSimplifiedList(predicates)) return cf.makeTRUE(clause.getOrigin());
		if (getDisjSimplifiedList(equalities)) return cf.makeTRUE(clause.getOrigin());
		if (getDisjSimplifiedList(arithmetic)) return cf.makeTRUE(clause.getOrigin());
		if (getDisjSimplifiedList(conditions)) assert false;

		// we look for conditions that are in the equalities
		if (getDisjSimplifiedConditions(equalities, conditions)) return cf.makeTRUE(clause.getOrigin());
		
		if (isEmptyWithoutConditions() && conditions.isEmpty()) return cf.makeFALSE(clause.getOrigin());
		else return cf.makeDisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
	}

	@Override
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		
		int numberOfFalse = 0;
		if (getEqSimplifiedList(predicates)) numberOfFalse++;
		if (getEqSimplifiedList(equalities)) numberOfFalse++;
		if (getEqSimplifiedList(arithmetic)) numberOfFalse++;
		if (getDisjSimplifiedList(conditions)) assert false;

		if (even(numberOfFalse) && isEmptyWithoutConditions()) {
			return cf.makeTRUE(clause.getOrigin());
		}
		if (!even(numberOfFalse) && isEmptyWithConditions()) {
			return cf.makeFALSE(clause.getOrigin());
		}
		else if (!even(numberOfFalse)) {
			EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		}
		return cf.makeClauseFromEquivalenceClause(clause.getOrigin(), predicates, 
				equalities, arithmetic, conditions, context);
	}
	
	private static boolean even(int value) {
		return value%2==0;
	}
	
	private static boolean getDisjSimplifiedConditions(List<EqualityLiteral> equalities, List<EqualityLiteral> conditions) {
		int i = 0;
		while (i < conditions.size()) {
			EqualityLiteral condition = conditions.get(i);
			for (int j = 0; j < equalities.size(); j++) {
				EqualityLiteral equality = equalities.get(j);
				if (condition.getInverse().equals(equality)) return true;
				if (condition.equals(equality)) conditions.remove(condition);
			}
			if (conditions.contains(condition)) i++;
		}
		return false;
	}
	
	private static <T extends Literal<?,?>> boolean getDisjSimplifiedList(List<T> literals) {
		LinkedHashSet<T> set = new LinkedHashSet<T>();
		for (int i = 0; i < literals.size(); i++) {
			T literal1 = literals.get(i);
			for (int j = i+1; j < literals.size(); j++) {
				T literal2 = literals.get(j);
				if (literal1.getInverse().equals(literal2)) {
					return true;
				}
			}
			set.add(literal1);
		}
		literals.clear();
		literals.addAll(set);
		return false;
	}
	
	private static <T extends Literal<T,?>> boolean getEqSimplifiedList(List<T> literals) {
		int inverse = 0;
		for (int i = 0; i < literals.size();) {
			T literal1 = literals.get(i);
			boolean removeLiteral1 = false;
			for (int j = i+1; j < literals.size(); j++) {
				T literal2 = literals.get(j);
				if (literal1.equals(literal2)) {
					removeLiteral1 = true;  
					literals.remove(j);
					j = literals.size();
				}
				else if (literal1.equals(literal2.getInverse())) {
					removeLiteral1 = true;
					literals.remove(j);
					j = literals.size();
					inverse++;
				}
			}
			if (removeLiteral1) literals.remove(i);
			else i++;
		}
		return !even(inverse);
	}
	
}
