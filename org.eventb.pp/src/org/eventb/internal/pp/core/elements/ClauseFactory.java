/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.IOrigin;

/**
 * Factory class for clauses.
 *
 * @author François Terrier
 *
 */
public class ClauseFactory {

	private static final ClauseFactory DEFAULT = new ClauseFactory();
	
	public static ClauseFactory getDefault() {
		return DEFAULT;
	}
	
	/**
	 * Returns a disjunctive clause with the literals provided in <code>literals</code>
	 * where each variable and local variable is fresh.
	 * 
	 * @param origin the origin of the new clause
	 * @param literals the literals of the new clause
	 * @param context the variable context to create fresh instances
	 * @return the new clause
	 */
	public Clause makeDisjunctiveClauseWithNewVariables(IOrigin origin, List<Literal<?,?>> literals, VariableContext context) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (Literal<?,?> literal : literals) {
			Literal<?,?> copy = literal.getCopyWithNewVariables(context, map);
			if (copy instanceof PredicateLiteral) predicates.add((PredicateLiteral)copy);
			else if (copy instanceof EqualityLiteral) equalities.add((EqualityLiteral)copy);
			else if (copy instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)copy);
		}
		Clause clause = new DisjunctiveClause(origin,predicates,equalities,arithmetic);
		return clause;
	}
	
	/**
	 * Returns an equivalence clause with the literals provided in <code>literals</code>
	 * where each variable and local variable is fresh.
	 * 
	 * @param origin the origin of the new clause
	 * @param literals the literals of the new clause
	 * @param context the variable context to create fresh instances
	 * @return the new clause
	 */
	public Clause makeEquivalenceClauseWithNewVariables(IOrigin origin, List<Literal<?,?>> literals, VariableContext context) {
		assert literals.size() > 1;
		
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (Literal<?,?> literal : literals) {
			Literal<?,?> copy = literal.getCopyWithNewVariables(context, map);
			if (copy instanceof PredicateLiteral) predicates.add((PredicateLiteral)copy);
			else if (copy instanceof EqualityLiteral) equalities.add((EqualityLiteral)copy);
			else if (copy instanceof ArithmeticLiteral) arithmetic.add((ArithmeticLiteral)copy);
		}
		Clause clause = new EquivalenceClause(origin,predicates,equalities,arithmetic);
		return clause;
	}
	
	/**
	 * Creates a new disjunctive clause with the given literals.
	 * 
	 * @param origin the origin of the new clause
	 * @param predicates the predicate literals
	 * @param equalities the equality literals
	 * @param arithmetic the arithmetic literals
	 * @param conditions the conditions
	 * @return the new clause
	 */
	public Clause makeDisjunctiveClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		return new DisjunctiveClause(origin, predicates, equalities, arithmetic, conditions);
	}
	
	/**
	 * Creates a new equivalence clause with the given literals.
	 * 
	 * @param origin the origin of the new clause
	 * @param predicates the predicate literals
	 * @param equalities the equality literals
	 * @param arithmetic the arithmetic literals
	 * @param conditions the conditions
	 * @return the new clause
	 */
	public Clause makeEquivalenceClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		assert predicates.size() + arithmetic.size() + equalities.size() > 1;
		
		return new EquivalenceClause(origin, predicates, equalities, arithmetic, conditions);
	}
	
	/**
	 * Creates a new equivalence clause or a new disjunctive clause based on the number of
	 * literals. An equivalence clause cannot contain only one literal (conditions are not
	 * taken into account). Therefore if
	 * only one literal is given as parameter, a disjunctive clause is returned. If more
	 * than one literal is given as parameter, an equivalence clause is returned.
	 * 
	 * @param origin the origin
	 * @param predicate the predicate literals
	 * @param equality the equality literals
	 * @param arithmetic the arithmetic literals
	 * @param conditions the conditions
	 * @param context the variables context
	 * @return an equivalence clause or a disjunctive clause depending on the total number of literals
	 */
	public Clause makeClauseFromEquivalenceClause(IOrigin origin, List<PredicateLiteral> predicate, 
			List<EqualityLiteral> equality, List<ArithmeticLiteral> arithmetic, 
			List<EqualityLiteral> conditions, VariableContext context) {
		assert predicate.size() + equality.size() + arithmetic.size() + conditions.size() > 0;
		
		// we have a disjunctive clause
		if (predicate.size() + equality.size() + arithmetic.size() <= 1) {
			if (predicate.size() == 1) replaceLocalVariablesByVariables(predicate, context);
			else if (equality.size() == 1) replaceLocalVariablesByVariables(equality, context);
			else if (arithmetic.size() == 1) replaceLocalVariablesByVariables(arithmetic, context);
			return new DisjunctiveClause(origin, predicate, equality, arithmetic, conditions);
		}
		return new EquivalenceClause(origin, predicate, equality, arithmetic, conditions);
	}
	
	private static <T extends Literal<?,?>> void replaceLocalVariablesByVariables(List<T> literals, VariableContext context) {
		assert literals.size() == 1;
		T literal = literals.remove(0);
		Set<LocalVariable> localVariables = new HashSet<LocalVariable>();
		literal.collectLocalVariables(localVariables);
		if (!localVariables.isEmpty() && localVariables.iterator().next().isForall()) {
			Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
			for (LocalVariable variable : localVariables) {
				map.put(variable, variable.getVariable(context));
			}
			literal = (T)literal.substitute(map);
		}
		literals.add(literal);
	}
	
	/**
	 * Returns a TRUE clause
	 * 
	 * @param origin the origin of the clause
	 * @return the new clause
	 */
	public Clause makeTRUE(IOrigin origin) {
		return new TrueClause(origin);
	}
	
	/**
	 * Returns a FALSE clause
	 * 
	 * @param origin the origin of the clause
	 * @return the new clause
	 */
	public Clause makeFALSE(IOrigin origin) {
		return new FalseClause(origin);
	}
	
}
