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
package org.eventb.internal.pp.core.elements.terms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.Hashable;
import org.eventb.internal.pp.core.elements.Sort;

/**
 * Abstract base class for terms.
 *
 * @author François Terrier
 *
 */
public abstract class Term extends Hashable implements Comparable<Term> {

	final protected Sort sort;
	final protected int priority;
	
	protected Term(Sort sort, int priority, int hashCode, int hashCodeWithDifferentVariables) {
		super(combineHashCodes(priority, hashCode), combineHashCodes(priority, hashCodeWithDifferentVariables));
		
		this.sort = sort;
		this.priority = priority;
	}
	
	/**
	 * Returns the sort of this term.
	 * 
	 * @return the sort of this term
	 */
	public Sort getSort() {
		return sort;
	}

	/**
	 * Returns <code>true</code> if this term does not
	 * contain any variables. Returns <code>false</code>
	 * otherwise.
	 * 
	 * @return <code>true</code> if this term does not
	 * contain any variables. Returns <code>false</code>
	 * otherwise
	 */
	public abstract boolean isConstant();
	
	/**
	 * Return <code>true</code> if this term contains at 
	 * least one local variables. Returns <code>false</code>
	 * otherwise.
	 * 
	 * @return <code>true</code> if this term contains at 
	 * least one local variables. Returns <code>false</code>
	 * otherwise
	 */
	public abstract boolean isQuantified();

	/**
	 * Returns <code>true</code> if {@link #isQuantified()}
	 * returns <code>true</code> and if the local variables are 
	 * universally quantified. 
	 * <p>
	 * By construction, a term cannot contain both universally and existentially 
	 * quantified variables at the same time.
	 * 
	 * @return <code>true</code> if {@link #isQuantified()}
	 * returns <code>true</code> and if the local variables are 
	 * universally quantified.
	 */
	public abstract boolean isForall();
	
	/**
	 * Returns the inverse of a term. The inverse is taken by replacing all 
	 * universally quantified variables by an existentially quantified variable
	 * and vice-versa.
	 * 
	 * @param <T> 
	 * @param term the term to inverse
	 * @return the inversed term
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Term> T getInverse(T term) {
		Set<LocalVariable> variables = new HashSet<LocalVariable>();
		term.collectLocalVariables(variables);
		Map<SimpleTerm, Term> map = new HashMap<SimpleTerm, Term>();
		for (LocalVariable variable : variables) {
			map.put(variable, variable.getInverseVariable());
		}
		return (T)term.substitute(map);
	}
	
	/**
	 * Substitutes variables and local variables in the term according
	 * to the specified map.
	 * 
	 * @param <T>
	 * @param map the substitution map
	 * @param term the term on which to apply the substitution
	 * @return the result of this substitution
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Term> T substituteSimpleTerms(Map<SimpleTerm, SimpleTerm> map, T term) {
		return (T)term.substitute(map);
	}
	
	public abstract String toString(HashMap<Variable, String> variableMap);
	
	/**
	 * Returns the priority of a term.
	 * <p>
	 * Used for term comparison.
	 * 
	 * @return the priority of a term
	 */
	protected int getPriority() {
		return priority;
	}
	
	@Override
	public String toString() {
		HashMap<Variable, String> variableMap = new HashMap<Variable, String>();
		return toString(variableMap);
	}
	
	/**
	 * Returns <code>true</code> if this term contains the specified term and
	 * <code>false</code> otherwise.
	 * 
	 * @param term the term to be looked for
	 * @return <code>true</code> if this term contains the specified term and
	 * <code>false</code> otherwise.
	 */
	public abstract boolean contains(SimpleTerm term);
	
	/**
	 * Adds to the specified set all instances of {@link Variable} occurring in this term.
	 * 
	 * @param variables the set to which the instances must be added
	 */
	public abstract void collectVariables(Set<Variable> variables);
	
	/**
	 * Adds to the specified set all instances of {@link LocalVariable} occurring in this term.
	 * 
	 * @param localVariables the set to which the instances must be added
	 */
	public abstract void collectLocalVariables(Set<LocalVariable> localVariables);
	
	/**
	 * Returns a term where all instances of {@link SimpleTerm} given in the specified map
	 * have been substituted by their respective value in the specified map.
	 * 
	 * @param <S>
	 * @param map the substitution map
	 * @return the new term after the substitution is applied
	 */
	protected abstract <S extends Term> Term substitute(Map<SimpleTerm, S> map);
	
	/**
	 * Returns <code>true</code> if this term is equal to the specified term when applying the
	 * substitution given in the specified map. Beside, all variables not already present in the 
	 * map are added to the map while computing the equality if the terms match. For instance,
	 * <code>x$1.equalsWithDifferentVariables(x$2, map)</code> with <code>map.isEmpty()==true</code>
	 * will return <code>true</code> and the map will contain the entry <code>{x$1,x$2}</code>.
	 * Calling <code>x$1.equalsWithDifferentVariables(x$2, map)</code> with map containing the
	 * entry <code>{x$1,x$3}</code> will return <code>false</code> and the map will remain unchanged.
	 * 
	 * @param term the term to be checked for equality
	 * @param map the correspondance map for the variables in the terms
	 * @return <code>true</code> if the terms are equal, <code>false</code> otherwise
	 */
	public abstract boolean equalsWithDifferentVariables(Term term, HashMap<SimpleTerm, SimpleTerm> map);

}