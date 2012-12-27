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
package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

/**
 * Abstract base class for clauses.
 * <p>
 * There are four types of clause, the two non-empty clause types {@link EquivalenceClause}
 * and {@link DisjunctiveClause} and the two empty clause types {@link TrueClause} 
 * and {@link FalseClause}.
 * <p>
 * Instances of this class are immutable and all accessor methods return an immutable
 * object or a shallow copy of a list of immutable objects.
 * <p>
 * Equality check and hashcode between clauses using the {@link #equals(Object)} and
 * {@link #hashCode()} method does not
 * take into account the origin of a clause for equivalence and disjunctive clause but
 * does for true and false clauses. This is because disjunctive and equivalence clauses
 * are optimised to be used in hashtables. {@link Hashtable#contains(Object)} returns
 * true if the hashtable already contains the clause regardless of the origin of the clause.
 * 
 * @author François Terrier
 *
 */
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
	
	private final void computeHashCode(int hashCode) {
		hashCode = 37*hashCode + hashCode(predicates);
		hashCode = 37*hashCode + hashCode(equalities);
		hashCode = 37*hashCode + hashCode(arithmetic);
		hashCode = 37*hashCode + hashCode(conditions);
		this.hashCode = hashCode;
	}

	protected final int hashCode(List<? extends Literal<?,?>> list) {
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
	
	private <T extends Literal<T,?>> boolean listEquals(List<T> list1, List<T> list2,
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
	
	/**
	 * Returns <code>true</code> if this clause contains
	 * conditions, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this clause contains
	 * conditions, <code>false</code> otherwise
	 */
	public boolean hasConditions() {
		return !conditions.isEmpty();
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
	
	
	/**
	 * Returns the level of this clause.
	 * 
	 * @return the level of this clause
	 */
	public Level getLevel() {
		return origin.getLevel();
	}

	protected BitSet negativeLiterals = new BitSet();
	protected BitSet positiveLiterals = new BitSet();

	/**
	 * Computes the bit set for predicate literals.
	 * <p>
	 * For each predicate literal, we associate a bit in either the
	 * positive literal bit set or the negative literal bit set,
	 * corresponding to the index of the predicate literal. Those
	 * bit sets are used for checking efficiently whether a predicate
	 * literal appears in this clause or not (in the {@link #matches(
	 * PredicateLiteralDescriptor, boolean)} method).
	 */
	protected abstract void computeBitSets();

	/**
	 * Returns <code>true</code> if this clause contains a predicate literal
	 * that matches the given descriptor with the given sign.
	 * <p>
	 * 
	 * @param predicate the predicate to be matched
	 * @param isPositive the sign of the predicate
	 * @return <code>true</code> if this clause contains a predicate literal
	 * that matches the given descriptor, <code>false</code> otherwise
	 */
	public abstract boolean matches(PredicateLiteralDescriptor predicate, boolean isPositive);
	
	/**
	 * Returns <code>true</code> if the specifed descriptor with the specified
	 * sign matches the predicate literal at the specified position in this clause.
	 * 
	 * @param predicate the predicate to be matched
	 * @param isPositive the sign of that predicate
	 * @param position the position in this clause where the predicate has to match
	 * @return <code>true</code> the specifed descriptor with the specified
	 * sign matches the predicate literal at the specified position in this clause,
	 * <code>false</code> otherwise
	 */
	public abstract boolean matchesAtPosition(PredicateLiteralDescriptor predicate, boolean isPositive, int position);
	
	protected boolean hasPredicateOfSign(PredicateLiteralDescriptor predicate, boolean isPositive) {
		if (isPositive) return positiveLiterals.get(predicate.getIndex());
		else return negativeLiterals.get(predicate.getIndex());
	}

	/**
	 * Returns the equality literals of this clause.
	 * 
	 * @return the equality literals of this clause
	 */
	public final List<EqualityLiteral> getEqualityLiterals() {
		return new ArrayList<EqualityLiteral>(equalities);
	}
	
	/**
	 * Returns the equality literal at the specified index.
	 * 
	 * @param index the index
	 * @return the equality literal at the specified index
	 */
	public final EqualityLiteral getEqualityLiteral(int index) {
		return equalities.get(index);
	}
	
	/**
	 * Returns the number of equality literals in this clause.
	 * 
	 * @return the number of equality literals in this clause
	 */
	public final int getEqualityLiteralsSize() {
		return equalities.size();
	}

	/**
	 * Returns the predicate literals in this clause.
	 * 
	 * @return the predicate literals in this clause
	 */
	public final List<PredicateLiteral> getPredicateLiterals() {
		return new ArrayList<PredicateLiteral>(predicates);
	}
	
	/**
	 * Returns the predicate literal at the specified index.
	 * 
	 * @param index the index
	 * @return the predicate literal at the specified index
	 */
	public final PredicateLiteral getPredicateLiteral(int index) {
		return predicates.get(index);
	}
	
	/**
	 * Returns the number of predicate literals in this clause.
	 * 
	 * @return the number of predicate literals in this clause
	 */
	public final int getPredicateLiteralsSize() {
		return predicates.size();
	}

	/**
	 * Returns the arithmetic literals in this clause.
	 * 
	 * @return the arithmetic literals in this clause
	 */
	public final List<ArithmeticLiteral> getArithmeticLiterals() {
		return new ArrayList<ArithmeticLiteral>(arithmetic);
	}
	
	/**
	 * Returns the arithmetic literal at the specified index.
	 * 
	 * @param index the index
	 * @return the arithmetic literal at the specified index
	 */
	public final ArithmeticLiteral getArithmeticLiteral(int index) {
		return arithmetic.get(index);
	}
	
	/**
	 * Returns the number of arithmetic literals in this clause.
	 * 
	 * @return the number of arithmetic literals in this clause
	 */
	public final int getArithmeticLiteralsSize() {
		return arithmetic.size();
	}
	
	/**
	 * Returns the conditions of this clause.
	 * 
	 * @return the conditions of this clause
	 */
	public final List<EqualityLiteral> getConditions() {
		return new ArrayList<EqualityLiteral>(conditions);
	}
	
	/**
	 * Returns the condition at the specified index.
	 * 
	 * @param index the index
	 * @return the condition at the specified index
	 */
	public final EqualityLiteral getCondition(int index) {
		return conditions.get(index);
	}
	
	/**
	 * Returns the number of conditions.
	 * 
	 * @return the number of conditions
	 */
	public final int getConditionsSize() {
		return conditions.size();
	}
	
	/**
	 * Returns <code>true</code> if this clause is a unit clause, i.e.
	 * if it contains only one literal, taking conditions into account.
	 * 
	 * @return <code>true</code> if this clause is a unit clause, <code>false</code> otherwise
	 */
	public boolean isUnit() {
		if (equalities.size() + predicates.size() + arithmetic.size() + conditions.size() == 1) return true;
		return false;
	}

	/**
	 * Returns the size of this clause without taking the conditions into account.
	 * 
	 * @return the size of this clause without taking the conditions into account
	 */
	public int sizeWithoutConditions() {
		return equalities.size() + predicates.size() + arithmetic.size();
	}

	/**
	 * Returns <code>true</code> if this clause is equal to the specified clause
	 * and has the same level.
	 * 
	 * @param clause the clause to be checked for equality
	 * @return <code>true</code> if this clause is equal to the specified clause
	 * and has the same level, <code>false</code> otherwise
	 */
	public boolean equalsWithLevel(Clause clause) {
		return getLevel().equals(clause.getLevel()) && equals(clause);
	}

	/**
	 * Returns the origin of this clause.
	 * 
	 * @return the origin of this clause
	 */
	public IOrigin getOrigin() {
		return origin;
	}
	
	/**
	 * Returns <code>true</code> if this clause has quantified literals (i.e.
	 * literals that contain terms that are instances of {@link LocalVariable}).
	 * 
	 * @return <code>true</code> if this clause has quantified literals, <code>false</code> otherwise
	 */
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
	
	/**
	 * Returns <code>true</code> if this is an equivalence clause, <code>false</code>
	 * otherwise.
	 * 
	 * @return <code>true</code> if this is an equivalence clause, <code>false</code>
	 * otherwise
	 */
	public abstract boolean isEquivalence();
	
	/**
	 * Returns <code>true</code> if this clause is ⊥.
	 * 
	 * @return <code>true</code> if this clause is ⊥, and <code>false</code> otherwise
	 */
	public abstract boolean isFalse();
	
	/**
	 * Returns <code>true</code> if this clause is ⊤.
	 * 
	 * @return <code>true</code> if this clause is ⊤, and <code>false</code> otherwise
	 */
	public abstract boolean isTrue();
	
	/**
	 * Runs the specified inferrer on this clause.
	 * 
	 * @param inferrer the inferrer
	 */
	public abstract void infer(IInferrer inferrer);

	/**
	 * Runs the specified simplifier on this clause and returns its 
	 * resulting clause.
	 * 
	 * @param simplifier the simplifier to be run
	 * @return the simplified clause
	 */
	public abstract Clause simplify(ISimplifier simplifier);
	
}
