/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;

public abstract class AbstractInferrer implements IInferrer {

	protected List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
	protected List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
	protected List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
	protected List<EqualityLiteral> conditions = new ArrayList<EqualityLiteral>();
	
	protected IVariableContext context;
	
	protected HashMap<SimpleTerm, SimpleTerm> substitutionsMap;
	
	public AbstractInferrer(IVariableContext context) {
		this.context = context;
	}
	
	protected void init(Clause clause, HashMap<SimpleTerm, SimpleTerm> map) {
		equalities.addAll(clause.getEqualityLiterals());
		predicates.addAll(clause.getPredicateLiterals());
		arithmetic.addAll(clause.getArithmeticLiterals());
		conditions.addAll(clause.getConditions());
		
		initialize(clause);
		
		getListCopy(equalities,map,context);
		getListCopy(predicates,map,context);
		getListCopy(arithmetic,map,context);
		getListCopy(conditions,map,context);
	}
	
	protected void init(DisjunctiveClause clause) {
		substitutionsMap = new HashMap<SimpleTerm, SimpleTerm>();
		init(clause,substitutionsMap);
	}
	
	protected void init(EquivalenceClause clause) {
		substitutionsMap = new HashMap<SimpleTerm, SimpleTerm>();
		init(clause,substitutionsMap);
	}
	
	protected <T extends Literal<?,?>> void getListCopy(List<T> list,
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap, IVariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add((T)pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		list.clear();
		list.addAll(result);
	}
	
	protected abstract void initialize(Clause clause) throws IllegalStateException;
	
	protected void reset() {
		equalities.clear();
		predicates.clear();
		arithmetic.clear();
		conditions.clear();
	}

	protected abstract void inferFromDisjunctiveClauseHelper(Clause clause);

	protected abstract void inferFromEquivalenceClauseHelper(Clause clause);

	public void inferFromDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		inferFromDisjunctiveClauseHelper(clause);
		reset();
	}

	public void inferFromEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		inferFromEquivalenceClauseHelper(clause);
		reset();
	}

	protected boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
}
