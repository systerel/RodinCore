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
import java.util.Stack;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.IOrigin;

public abstract class AbstractPPClause implements IClause {

	protected List<IEquality> equalities;
	protected List<IArithmetic> arithmetic = new ArrayList<IArithmetic>();
	protected List<IPredicate> predicates;
	
	protected IOrigin origin;
	protected Level level = Level.base;
	
	public AbstractPPClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		this.level = level;
		this.predicates = predicates;
		this.equalities = equalities;
		this.arithmetic = arithmetic;
		
		computeBitSets();
	}
	
	public AbstractPPClause(List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		this.predicates = predicates;
		this.equalities = equalities;
		this.arithmetic = arithmetic;
		
		computeBitSets();
	}
	
	
	protected boolean equalsWithDifferentVariables(IClause clause, HashMap<AbstractVariable, AbstractVariable> map) {
		if (clause == this) return true;
		if (clause instanceof AbstractPPClause) {
			AbstractPPClause temp = (AbstractPPClause) clause;
			return listEquals(predicates, temp.predicates, map) && listEquals(equalities, temp.equalities, map)
				&& listEquals(arithmetic, temp.arithmetic, map);
		}
		return false;
	}
	
	protected int hashCodeWithDifferentVariables() {
		int hashCode = 1;
		hashCode = 31*hashCode + hashCode(predicates);
		hashCode = 31*hashCode + hashCode(equalities);
		hashCode = 31*hashCode + hashCode(arithmetic);
		return hashCode;
	}
	
	protected int hashCode(List<? extends ILiteral<?>> list) {
		int hashCode = 1;
		for (ILiteral<?> literal : list) {
			hashCode = 31*hashCode + (literal==null ? 0 : literal.hashCodeWithDifferentVariables());
		}
		return hashCode;
	}
	
	protected <T extends ILiteral<T>> boolean listEquals(List<T> list1, List<T> list2,
			HashMap<AbstractVariable, AbstractVariable> map) {
		if (list1.size() != list2.size()) return false;
		for (int i = 0; i < list1.size(); i++) {
			T el1 = list1.get(i);
			T el2 = list2.get(i);
			if (!el1.equalsWithDifferentVariables(el2, map)) return false;
		}
		return true;
	}
	
	
	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}

	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(""+level);
		str.append("[");
		for (ILiteral literal : predicates) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		for (ILiteral literal : equalities) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		for (ILiteral literal : arithmetic) {
			str.append(literal.toString(variableMap));
			str.append(", ");
		}
		str.append("]");
		return str.toString();
	}
	

	public Level getLevel() {
		return level;
	}
//
//	public Stack<IClauseContext> getContexts() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public void getDependencies(Stack<Level> dependencies) {
		if (!dependencies.contains(level))
			dependencies.push(level);
		if (level.equals(Level.base)) {
			return;
		}
		origin.getDependencies(dependencies);
	}

	protected BitSet negativeLiterals = new BitSet();
	protected BitSet positiveLiterals = new BitSet();
	
	protected abstract void computeBitSets();

	
	public boolean contains(IPredicate predicate) {
		return hasPredicateOfSign(predicate, false);
	}

	public boolean matches(IPredicate predicate) {
		return hasPredicateOfSign(predicate, true);
	}
	
	protected boolean hasPredicateOfSign(IPredicate predicate, boolean opposite) {
		if (predicate.isPositive()) return opposite?negativeLiterals.get(predicate.getIndex()):positiveLiterals.get(predicate.getIndex());
		else return (!opposite)?negativeLiterals.get(predicate.getIndex()):positiveLiterals.get(predicate.getIndex());
	}

	public List<IEquality> getEqualityLiterals() {
		List<IEquality> result = new ArrayList<IEquality>();
		result.addAll(equalities);
		return result;
	}

	public List<IPredicate> getPredicateLiterals() {
		List<IPredicate> result = new ArrayList<IPredicate>();
		result.addAll(predicates);
		return result;
	}

	public List<IArithmetic> getArithmeticLiterals() {
		List<IArithmetic> result = new ArrayList<IArithmetic>();
		result.addAll(arithmetic);
		return result;
	}
	
	protected <T extends ILiteral<T>> List<T> getListCopy(List<T> list,
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap, IVariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add(pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		return result;
	}
	
	public boolean isUnit() {
		if (equalities.size() + predicates.size() + arithmetic.size() == 1) return true;
		return false;
	}

	public boolean isEmpty() {
		if (equalities.size() + predicates.size() + arithmetic.size() == 0) return true;
		return false;
	}


	public boolean equalsWithLevel(IClause clause) {
		return level.equals(clause.getLevel()) && equals(clause);
	}

	public IOrigin getOrigin() {
		return origin;
	}
	
	public void setOrigin(IOrigin origin) {
		this.origin = origin;
	}
	
	public void reset() {
		for (IPredicate predicate : predicates) {
			predicate.resetInstantiationCount();
		}
	}

}
