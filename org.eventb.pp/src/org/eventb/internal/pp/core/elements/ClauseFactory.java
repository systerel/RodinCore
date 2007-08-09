/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class ClauseFactory {

	private static ClauseFactory DEFAULT = new ClauseFactory();
	
	public static ClauseFactory getDefault() {
		return DEFAULT;
	}
	
	// creates a definition clause
	public Clause newDisjClauseWithCopy(IOrigin origin, List<Literal<?,?>> literals, IVariableContext context) {
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
	
	// creates a definition clause
	public Clause newEqClauseWithCopy(IOrigin origin, List<Literal<?,?>> literals, IVariableContext context) {
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
	
	
//	public Clause newDisjClause(List<Literal> literals) {
//		List<PredicateFormula> predicates = new ArrayList<PredicateFormula>();
//		List<EqualityFormula> equalities = new ArrayList<EqualityFormula>();
//		List<ArithmeticFormula> arithmetic = new ArrayList<ArithmeticFormula>();
//		for (Literal literal : literals) {
//			
//			if (literal instanceof PredicateFormula) predicates.add((PredicateFormula)literal);
//			else if (literal instanceof EqualityFormula) equalities.add((EqualityFormula)literal);
//			else if (literal instanceof ArithmeticFormula) arithmetic.add((ArithmeticFormula)literal);
//		}
//		Clause clause = new DisjunctiveClause(Level.base,predicates,equalities,arithmetic);
//		return clause;
//	}
//	
//	public Clause newEqClause(List<Literal> literals) {
//		assert literals.size() > 1;
//		
//		List<PredicateFormula> predicates = new ArrayList<PredicateFormula>();
//		List<EqualityFormula> equalities = new ArrayList<EqualityFormula>();
//		List<ArithmeticFormula> arithmetic = new ArrayList<ArithmeticFormula>();
//		for (Literal literal : literals) {
//			
//			if (literal instanceof PredicateFormula) predicates.add((PredicateFormula)literal);
//			else if (literal instanceof EqualityFormula) equalities.add((EqualityFormula)literal);
//			else if (literal instanceof ArithmeticFormula) arithmetic.add((ArithmeticFormula)literal);
//		}
//		Clause clause = new EquivalenceClause(Level.base,predicates,equalities,arithmetic);
//		return clause;
//	}
	
}
