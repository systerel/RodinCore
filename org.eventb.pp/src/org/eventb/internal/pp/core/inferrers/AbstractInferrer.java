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

import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;

/**
 * Abstract base class for inferrers. This abstract class provides the necessary
 * mechanisms to copy the literals of a clause.
 * <p>
 * 
 * @author François Terrier
 *
 */
public abstract class AbstractInferrer implements IInferrer {
	protected final ClauseFactory cf = ClauseFactory.getDefault();
	
	protected List<EqualityLiteral> equalities;
	protected List<PredicateLiteral> predicates;
	protected List<ArithmeticLiteral> arithmetic;
	protected List<EqualityLiteral> conditions;
	
	protected final VariableContext context;
	
	protected HashMap<SimpleTerm, SimpleTerm> substitutionsMap;
	
	public AbstractInferrer(VariableContext context) {
		this.context = context;
	}
	
	/**
	 * Initializes the inferrer with the specified clause.
	 * <p>
	 * This method is called right before the fields have been populated with
	 * the literals of the clause. When this method is called, all original literals 
	 * of the clause can be accessed, and manipulated (some of them can be removed,
	 * for instance). After this method returns, the remaining literal lists are copied.
	 * <p>
	 * This method is also the right place to throw an exception if the inferrer
	 * is not initialized properly.
	 * 
	 * @param clause
	 * @throws IllegalStateException
	 */
	protected abstract void initialize(Clause clause) throws IllegalStateException;
	
	/**
	 * Infers from a disjunctive clause.
	 * <p>
	 * This method is called just before reset(). When this method returns, the
	 * result of the inferrence must be ready.
	 * 
	 * @param clause
	 */
	protected abstract void inferFromDisjunctiveClauseHelper(Clause clause);
	
	/**
	 * Infers from an equivalence clause.
	 * <p>
	 * This method is called just before reset(). When this method returns, the
	 * result of the inferrence must be ready.
	 * 
	 * @param clause
	 */
	protected abstract void inferFromEquivalenceClauseHelper(Clause clause);

	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.inferrers.IInferrer#inferFromDisjunctiveClause(org.eventb.internal.pp.core.elements.DisjunctiveClause)
	 */
	@Override
	public final void inferFromDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		inferFromDisjunctiveClauseHelper(clause);
		reset();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.inferrers.IInferrer#inferFromEquivalenceClause(org.eventb.internal.pp.core.elements.EquivalenceClause)
	 */
	@Override
	public final void inferFromEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		inferFromEquivalenceClauseHelper(clause);
		reset();
	}
	
	private void init(Clause clause) {
		substitutionsMap = new HashMap<SimpleTerm, SimpleTerm>();
		init(clause,substitutionsMap);
	}
	
	private void init(Clause clause, HashMap<SimpleTerm, SimpleTerm> map) {
		setFields(clause);
		initialize(clause);
		copyFields(map);
	}
	
	private void setFields(Clause clause) {
		equalities = clause.getEqualityLiterals();
		predicates = clause.getPredicateLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	private void copyFields(HashMap<SimpleTerm, SimpleTerm> map) {
		getListCopy(equalities,map,context);
		getListCopy(predicates,map,context);
		getListCopy(arithmetic,map,context);
		getListCopy(conditions,map,context);
	}
	
	protected void reset() {
		equalities = null;
		predicates = null;
		arithmetic = null;
		conditions = null;
	}

	protected final boolean isEmptyWithConditions() {
		return conditions.size() == 0 && isEmptyWithoutConditions();
	}

	protected final boolean isEmptyWithoutConditions() {
		return predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
	protected final <T extends Literal<?,?>> void getListCopy(List<T> list,
			HashMap<SimpleTerm, SimpleTerm> substitutionsMap, VariableContext context) {
		List<T> result = new ArrayList<T>();
		for (T pred : list) {
			result.add((T)pred.getCopyWithNewVariables(context, substitutionsMap));
		}
		list.clear();
		list.addAll(result);
	}
}
