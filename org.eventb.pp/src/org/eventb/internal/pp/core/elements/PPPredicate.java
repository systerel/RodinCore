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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * This class represents a predicate with arguments.
 *
 * @author François Terrier
 *
 */
public class PPPredicate extends PPProposition implements IPredicate {

	private List<Term> terms;
	
	public PPPredicate (int index, boolean isPositive, List<Term> terms) {
		super(index,isPositive);
		assert terms != null;
		
		this.terms = terms;
	}
	
	public List<Term> getTerms() {
		return terms;
	}

	@Override
	public int hashCode() {
		return super.hashCode() + terms.hashCode()*31;
	}
	
	@Override
	public int hashCodeWithDifferentVariables() {
		int hashCode = super.hashCodeWithDifferentVariables();
		for (Term term : terms) {
			hashCode = 31*hashCode + term.hashCodeWithDifferentVariables();
		}
		return hashCode;
	}
	
	@Override
	public ILiteralDescriptor getDescriptor() {
		return new PredicateDescriptor(index);
	}
	
	@Override
	public String toString() {
		return toString(new HashMap<Variable, String>());
	}

	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(isPositive?"":"¬");
		str.append("P" + index + "(");
		for (Term term : terms) {
			str.append(term.toString(variableMap));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append(")");
		return str.toString();
	}

	public IPredicate getInverse() {
		List<Term> newTerms = new ArrayList<Term>();
		for (Term term : terms) {
			newTerms.add(term.getInverse());
		}
		return new PPPredicate(index, !isPositive, newTerms);
	}
	
	@Override
	// MUST BE called on a unit-clause
	public List<IEquality> getConditions(IPredicate predicate) {
		// We first do a copy of the predicate, in which we replace all
		// local variables by a fresh existential variable and all
		// variables by a fresh variable. This ensures invariant of variables
		// and local variables which states that there must not be equal variables
		// in 2 different clauses and no equal local variables in 2 different
		// literals.
		List<IEquality> result = new ArrayList<IEquality>();
		for (int i = 0; i < terms.size(); i++) {
			Term term1 = terms.get(i);
			Term term2 = predicate.getTerms().get(i);
			result.add(new PPEquality(term1,term2,false));
		}
		return result;
	}
	
	public IPredicate getCopyWithNewVariables(IVariableContext context, 
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		Set<Variable> variables = new HashSet<Variable>();
		List<LocalVariable> localVariables = new ArrayList<LocalVariable>();
		for (Term term : terms) {
			term.collectVariables(variables);
			term.collectLocalVariables(localVariables);
		}
		for (Variable variable : variables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextVariable(variable.getSort()));
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, new LocalVariable(context.getNextLocalVariableID(),variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}
	
	@Override
	public IPredicate substitute(Map<AbstractVariable, ? extends Term> map) {
		List<Term> result = new ArrayList<Term>();
		for (Term child : terms) {
			result.add(child.substitute(map));
		}
		return new PPPredicate(index, isPositive, result);
	}
	
	public boolean isQuantified() {
		for (Term term : terms) {
			if (term.isQuantified()) return true;
		}
		return false;
	}

	public boolean isConstant() {
		for (Term term : terms) {
			if (!term.isConstant()) return false;
		}
		return true;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPPredicate) {
			PPPredicate temp = (PPPredicate) obj;
			return super.equals(temp) && terms.equals(temp.terms);
		}
		return false;
	}
	
	@Override
	public boolean equalsWithDifferentVariables(IPredicate literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPPredicate) {
			PPPredicate temp = (PPPredicate) literal;
			if (!super.equals(temp) || terms.size() != temp.terms.size()) return false;
			else {
				for (int i = 0; i < terms.size(); i++) {
					Term term1 = terms.get(i);
					Term term2 = temp.terms.get(i);
					if (!term1.equalsWithDifferentVariables(term2, map)) return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean updateInstantiationCount(IPredicate predicate) {
		boolean result = false;
		for (int i = 0; i < terms.size(); i++) {
			Term term1 = terms.get(i);
			Term term2 = predicate.getTerms().get(i);
			if (!term1.isConstant() && term2.isConstant()) {
				term1.incrementInstantiationCount();
				if (term1.isBlocked()) result = true;
			}
			// we do not increment the instantiation count of the unit clause !
//			if (!term2.isConstant() && term1.isConstant()) {
//				term2.incrementInstantiationCount();
//				if (term2.isBlocked()) result = true;
//			}
		}
		return result;
	}
	
	@Override
	public void resetInstantiationCount() {
		for (Term term : terms) {
			term.resetInstantiationCount();
		}
	}
	
	
	public static boolean match(IPredicate matcher, IPredicate matched, boolean equivalence) {
		// 1 test same index
		if (matcher.getIndex() != matched.getIndex()) return false;
		// 2 test matching signs
		if (matcher.isPositive() == matched.isPositive() && !equivalence) return false;
		// 3 test compatible terms
		// we reject constant term in unit clause matching pseudo constant term in non-unit clause
		for (int i=0;i<matcher.getTerms().size();i++) {
			Term matcherTerm = matcher.getTerms().get(i);
			Term matchedTerm = matched.getTerms().get(i);
			if ((matcherTerm.isQuantified() || matcherTerm.isConstant()) && (matchedTerm.isQuantified() || matchedTerm.isConstant())) {
				// we do not match on a locally quantified variable
				if (matcherTerm.isQuantified()) return false;
				if (matchedTerm.isQuantified() && !equivalence) return false;
				if (matchedTerm.isQuantified() && equivalence) {
					boolean forall = isForall(matched, new ArrayList<LocalVariable>());
					boolean sameSign = matcher.isPositive() == matched.isPositive();
					if (forall == sameSign) return false;
				}
			}
		}
		return true;
	}
	
	public static boolean isForall(IPredicate predicate, List<LocalVariable> pseudoConstants) {
		assert predicate.isQuantified();
		
		for (Term term : predicate.getTerms()) {
			term.collectLocalVariables(pseudoConstants);
		}
		
		return pseudoConstants.get(0).isForall();
	}
	
}
