/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.List;
import java.util.Stack;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public interface IClause {
	
	public Level getLevel();
	
	public void getDependencies(Stack<Level> dependencies);
	
	public List<IEquality> getEqualityLiterals();
	
	public List<IPredicate> getPredicateLiterals();
	
	public List<IArithmetic> getArithmeticLiterals();
	
	public List<IEquality> getConditions();
	
	public boolean isBlocked();
	
//	// stack of contexts, level-dependent information
//	public Stack<IClauseContext> getContexts();


//	// parents
//	public List<IClause> getParents();
//	
//	// TODO this makes the class mutable
//	public void setParents(List<IClause> parents);
//	
//	public Predicate getOriginalPredicate();
//	
//	public void setOriginalPredicate(Predicate predicate);
	
	public void setOrigin(IOrigin origin);
	
	public IOrigin getOrigin();
	
	
	public boolean isUnit();
	
	public boolean isEmpty();
	
	public boolean contains(IPredicate predicate);

	public boolean matches(IPredicate predicate);
	
	public boolean equalsWithLevel(IClause clause);
	
//	public boolean matchesAtPosition(IPredicate predicate, int position);
	
	public IClause simplify(ISimplifier simplifier);
	
	public void infer(IInferrer inferrer);
	
//	/**
//	 * Returns a copy of this clause, whose variables are
//	 * distinct from the ones of the original clause.
//	 * <p>
//	 * The copy does not set the parents. Right after a call
//	 * to {@link #copy(IVariableContext)}, {@link #getParents()}
//	 * returns an empty list.
//	 * 
//	 * @param context the context
//	 * @return a copy of this clause
//	 */
//	public IClause copy(IVariableContext context);
	
	public void reset();
	
}
