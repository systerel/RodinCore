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

	final protected List<ArithmeticLiteral> arithmetic;
	final protected List<EqualityLiteral> equalities;
	final protected List<PredicateLiteral> predicates;
	final protected List<EqualityLiteral> conditions;
	
	final protected IOrigin origin;
	
	private int hashCode;
	
	public Clause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions, int hashCode) {
		this.origin = origin;
		this.predicates = predicates;
		this.equalities = equalities;
		this.arithmetic = arithmetic;
		this.conditions = conditions;
		
		computeBitSets();
		computeHashCode(hashCode);
	}

	public Clause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, int hashCode) {
		this.origin = origin;
		this.predicates = predicates;
		this.equalities = equalities;
		this.arithmetic = arithmetic;
		this.conditions = new ArrayList<EqualityLiteral>();
		
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
	public final int hashCode() {
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

	public abstract boolean matches(PredicateDescriptor predicate);
	
	public abstract boolean matchesAtPosition(PredicateDescriptor predicate, int position);
	
	protected boolean hasPredicateOfSign(PredicateDescriptor predicate, boolean opposite) {
		if (predicate.isPositive()) return opposite?negativeLiterals.get(predicate.getIndex()):positiveLiterals.get(predicate.getIndex());
		else return (!opposite)?negativeLiterals.get(predicate.getIndex()):positiveLiterals.get(predicate.getIndex());
	}

	public List<EqualityLiteral> getEqualityLiterals() {
		List<EqualityLiteral> result = new ArrayList<EqualityLiteral>();
		result.addAll(equalities);
		return result;
	}

	public List<PredicateLiteral> getPredicateLiterals() {
		List<PredicateLiteral> result = new ArrayList<PredicateLiteral>();
		result.addAll(predicates);
		return result;
	}

	public List<ArithmeticLiteral> getArithmeticLiterals() {
		List<ArithmeticLiteral> result = new ArrayList<ArithmeticLiteral>();
		result.addAll(arithmetic);
		return result;
	}
	
	public List<EqualityLiteral> getConditions() {
		List<EqualityLiteral> result = new ArrayList<EqualityLiteral>();
		result.addAll(conditions);
		return result;
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

	public boolean isEmpty() {
		if (equalities.size() + predicates.size() + arithmetic.size() + conditions.size() == 0) return true;
		return false;
	}
	
	public int sizeWithoutConditions() {
		return equalities.size() + predicates.size() + arithmetic.size();
	}

	public boolean equalsWithLevel(Clause clause) {
		return getLevel().equals(clause.getLevel()) && equals(clause);
	}

	public IOrigin getOrigin() {
		return origin;
	}
	
	public abstract boolean isEquivalence();
	
	public abstract boolean isFalse();
	
	public abstract boolean isTrue();
	
	public abstract void infer(IInferrer inferrer);

	public abstract Clause simplify(ISimplifier simplifier);
	
}
