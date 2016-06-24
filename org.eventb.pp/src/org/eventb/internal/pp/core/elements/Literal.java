/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * Abstract base class for literals.
 * <p>
 * There are three types of literals, {@link PredicateLiteral}, {@link EqualityLiteral}
 * and {@link ArithmeticLiteral}. 
 * <p>
 * Instances of this class are immutable and all accessor methods return an immutable
 * object or a shallow copy of a list of immutable objects.
 *
 * @author François Terrier
 *
 * @param <S> subclasses should put themselves as a parameter. Used to implement true
 * covariance.
 * @param <T> type of terms contained in this literal
 */
public abstract class Literal<S extends Literal<S,T>, T extends Term> extends Hashable {

	final protected List<T> terms;
	
	public Literal(List<T> terms, int hashCode) {
		super( 
				combineHashCodes(combineHashCodesWithSameVariables(terms),hashCode),
				combineHashCodes(combineHashCodesWithDifferentVariables(terms),hashCode)
		);
		
		this.terms = terms;
	}

	/**
	 * Returns the terms of this literal.
	 * 
	 * @return the terms of this literal
	 */
	public List<T> getTerms() {
		return new ArrayList<T>(terms);
	}
	
	/**
	 * Returns the term at the specified index.
	 * 
	 * @param index the index
	 * @return the term at the specified index
	 */
	public T getTerm(int index) {
		return terms.get(index);
	}
	
	/**
	 * Returns the number of terms of this literal.
	 * 
	 * @return the number of terms of this literal
	 */
	public int getTermsSize() {
		return terms.size();
	}
	
	/**
	 * Returns <code>true</code> if this literal is quantified,
	 * <code>false</code> otherwise.
	 * <p>
	 * A literal is quantified iff one of the term contains an
	 * instance of {@link LocalVariable}.
	 * 
	 * @return <code>true</code> if this literal is quantified,
	 * <code>false</code> otherwise
	 */
	public boolean isQuantified() {
		for (T term : terms) {
			if (term.isQuantified()) return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if this literal is constant and
	 * <code>false</code> otherwise.
	 * <p>
	 * A literal is constant if none of its terms contain an instance
	 * of {@link Variable}.
	 * 
	 * @return <code>true</code> if this literal is constant and
	 * <code>false</code> otherwise
	 */
	public boolean isConstant() {
		for (T term : terms) {
			if (!term.isConstant()) return false;
		}
		return true;
	}
	
	/**
	 * Returns <code>true</code> if this literal is quantified and
	 * the quantifier is a forall quantifier.
	 * 
	 * @return <code>true</code> if this literal is quantified and
	 * the quantifier is a forall quantifier, <code>false</code> otherwise
	 */
	public boolean isForall() {
		for (T term : terms) {
			if (term.isForall()) return true;
		}
		return false;
	}
	
	/**
	 * Returns a copy of this literal where all variables and local variables
	 * have been replaced by a fresh instance.
	 * <p>
	 * It holds that <code>literal.getCopyWithNewVariables(...).equalsWithDifferentVariables(
	 * literal)</code>
	 * 
	 * @param context the variable context
	 * @param substitutionsMap the substitution map
	 * @return a copy of this literal with new instances of variables.
	 */
	public S getCopyWithNewVariables(VariableContext context, 
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap) {
		Set<Variable> variables = new LinkedHashSet<Variable>();
		Set<LocalVariable> localVariables = new LinkedHashSet<LocalVariable>();
		for (Term term : terms) {
			term.collectVariables(variables);
			term.collectLocalVariables(localVariables);
		}
		for (Variable variable : variables) {
			Variable copy = context.getNextVariable(variable.getSort());
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, copy);
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextLocalVariable(variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}
	
	/**
	 * Puts all local variables that occur in this literal in the
	 * specified set.
	 * 
	 * @param variables the set of local variables to populate
	 */
	public void collectLocalVariables(Set<LocalVariable> variables) {
		for (Term term : terms) {
			term.collectLocalVariables(variables);
		}
	}
	
	/**
	 * Puts all variables that occur in this literal in the specified set.
	 * 
	 * @param variables the set of variables to populate
	 */
	public void collectVariables(Set<Variable> variables) {
		for (Term term : terms) {
			term.collectVariables(variables);
		}
	}
	
	/**
	 * Returns a copy of this literal where the substitution given in the specified
	 * map is applied.
	 * 
	 * @param map the substitution map
	 * @return a copy of this literal with the applied substitution
	 */
	public abstract S substitute(Map<SimpleTerm, SimpleTerm> map);
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Literal) {
			Literal<?, ?> temp = (Literal<?,?>)obj;
			return terms.equals(temp.terms);
		}
		return false;
	}
	
	/**
	 * Return <code>true</code> if this literal is equal to the specified 
	 * literal but with different instances of variables.
	 * <p>
	 * @see Term#equalsWithDifferentVariables(Term, HashMap)
	 * 
	 * @param literal the literal to be checked for equality
	 * @param map the map of correspondance for variables
	 * @return <code>true</code> if this literal is equal to the specified
	 * literal but with different instances of variables, <code>false</code> otherwise
	 */
	public boolean equalsWithDifferentVariables(S literal, HashMap<SimpleTerm, SimpleTerm> map) {
		if (terms.size() != literal.terms.size()) return false;
		else {
			for (int i = 0; i < terms.size(); i++) {
				T term1 = terms.get(i);
				Term term2 = literal.terms.get(i);
				if (!term1.equalsWithDifferentVariables(term2, map)) return false;
			}
			return true;
		}
	}
	
	protected static <S extends Term> List<S> substituteHelper(Map<SimpleTerm, SimpleTerm> map, List<S> terms) {
		List<S> result = new ArrayList<S>();
		for (S term : terms) {
			result.add(Term.substituteSimpleTerms(map, term));
		}
		return result;
	}
	
	protected static <U extends Term> List<U> getInverseHelper(List<U> terms) {
		List<U> result = new ArrayList<U>();
		for (U term : terms) {
			result.add(Term.getInverse(term));
		}
		return result;
	}
	
	/**
	 * Returns a copy of this literal with the opposite sign.
	 * 
	 * @return a copy of this literal with the opposite sign
	 */
	public abstract S getInverse();
	
	@Override
	public String toString() {
		return toString(new HashMap<Variable, String>());
	}
	
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append("[");
		for (T term : terms) {
			str.append(term.toString(variableMap));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("]");
		return str.toString();
	}
	
}
