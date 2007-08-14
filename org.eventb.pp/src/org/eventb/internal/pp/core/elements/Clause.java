/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public abstract class Clause {

	final protected List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
	final protected List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
	final protected List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
	final protected List<EqualityLiteral> conditions = new ArrayList<EqualityLiteral>();
	
	final protected IOrigin origin;
	
	private int hashCode;
	
	public Clause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions, int hashCode) {
		this.origin = origin;
		this.predicates.addAll(predicates);
		this.equalities.addAll(equalities);
		this.arithmetic.addAll(arithmetic);
		this.conditions.addAll(conditions);
		
		computeBitSets();
		computeHashCode(hashCode);
	}

	public Clause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, int hashCode) {
		this.origin = origin;
		this.predicates.addAll(predicates);
		this.equalities.addAll(equalities);
		this.arithmetic.addAll(arithmetic);
		
		computeBitSets();
		computeHashCode(hashCode);
	}
	
	protected boolean equalsWithDifferentVariables(Clause clause, HashMap<SimpleTerm, SimpleTerm> map) {
		if (clause == this) return true;
		return listEquals(predicates, clause.predicates, map) && listEquals(equalities, clause.equalities, map)
			&& listEquals(arithmetic, clause.arithmetic, map) && listEquals(conditions, clause.conditions, map);
	}
	
	
	private void computeHashCode(int hashCode) {
		hashCode = 37*hashCode + hashCode(predicates);
		hashCode = 37*hashCode + hashCode(equalities);
		hashCode = 37*hashCode + hashCode(arithmetic);
		hashCode = 37*hashCode + hashCode(conditions);
		this.hashCode = hashCode;
	}

	protected int hashCode(List<? extends Literal<?,?>> list) {
		int hashCode = 1;
		for (Literal<?,?> literal : list) {
			hashCode = 37*hashCode + (literal==null ? 0 : literal.hashCodeWithDifferentVariables());
		}
		return hashCode;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	protected <T extends Literal<T,?>> boolean listEquals(List<T> list1, List<T> list2,
			HashMap<SimpleTerm, SimpleTerm> map) {
		if (list1.size() != list2.size()) return false;
		for (int i = 0; i < list1.size(); i++) {
			T el1 = list1.get(i);
			T el2 = list2.get(i);
			if (!el1.equalsWithDifferentVariables(el2, map)) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Clause) {
			Clause tmp = (Clause) obj;
			HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
			return equalsWithDifferentVariables(tmp,map);
		}
		return false;
	}
	
	public boolean isBlockedOnConditions() {
		return conditions.size() > 0;
	}

	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}

	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(""+getLevel());
		str.append("[");
		for (Literal<?,?> literal : predicates) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		for (Literal<?,?> literal : equalities) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		for (Literal<?,?> literal : arithmetic) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		if (conditions.size() > 0) {
			str.append(" CONDITIONS: ");
			str.append("[");
			for (Literal<?,?> literal : conditions) {
				str.append(literal.toString(variableMap));
				str.append(", ");
			}
			str.append("]");
		}
		str.append("]");
		return str.toString();
	}
	
	
	public Level getLevel() {
		return origin.getLevel();
	}
//
//	public Stack<IClauseContext> getContexts() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	protected BitSet negativeLiterals = new BitSet();
	protected BitSet positiveLiterals = new BitSet();

	protected abstract void computeBitSets();

//	public abstract boolean contains(PredicateDescriptor predicate);

	public abstract boolean matches(PredicateLiteralDescriptor predicate, boolean isPositive);
	
	public abstract boolean matchesAtPosition(PredicateLiteralDescriptor predicate, boolean isPositive, int position);
	
	protected boolean hasPredicateOfSign(PredicateLiteralDescriptor predicate, boolean isPositive) {
		if (isPositive) return positiveLiterals.get(predicate.getIndex());
		else return negativeLiterals.get(predicate.getIndex());
	}

	public List<EqualityLiteral> getEqualityLiterals() {
		return equalities;
	}

	public List<PredicateLiteral> getPredicateLiterals() {
		return predicates;
	}

	public List<ArithmeticLiteral> getArithmeticLiterals() {
		return arithmetic;
	}
	
	public List<EqualityLiteral> getConditions() {
		return conditions;
	}
	
	protected <T extends Literal<T,?>> List<T> getListCopy(List<T> list,
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap, IVariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add(pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		return result;
	}
	
	public boolean isUnit() {
		if (equalities.size() + predicates.size() + arithmetic.size() + conditions.size() == 1) return true;
		return false;
	}

//	public boolean isEmpty() {
//		if (equalities.size() + predicates.size() + arithmetic.size() + conditions.size() == 0) return true;
//		return false;
//	}
	
	public int sizeWithoutConditions() {
		return equalities.size() + predicates.size() + arithmetic.size();
	}

	public boolean equalsWithLevel(Clause clause) {
		return getLevel().equals(clause.getLevel()) && equals(clause);
	}

	public IOrigin getOrigin() {
		return origin;
	}
	
	public boolean hasQuantifiedLiteral() {
		if (hasQuantifiedLiteral(predicates)) return true;
		if (hasQuantifiedLiteral(equalities)) return true;
		if (hasQuantifiedLiteral(arithmetic)) return true;
		if (hasQuantifiedLiteral(conditions)) return true;
		return false;
	}
	
	private <T extends Literal<?,?>> boolean hasQuantifiedLiteral(List<T> list) {
		for (T t : list) {
			if (t.isQuantified()) return true;
		}
		return false;
	}
	
	public abstract boolean isEquivalence();
	
	public abstract boolean isFalse();
	
	public abstract boolean isTrue();
	
	public abstract void infer(IInferrer inferrer);

	public abstract Clause simplify(ISimplifier simplifier);
	
}
