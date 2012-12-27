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
package org.eventb.internal.pp.core.simplifiers;

import java.util.List;

import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;

/**
 * Abstract class providing initialization for simplifiers.
 *
 * @author François Terrier
 *
 */
public abstract class AbstractSimplifier implements ISimplifier {

	protected ClauseFactory cf = ClauseFactory.getDefault();
	protected List<EqualityLiteral> equalities;
	protected List<PredicateLiteral> predicates;
	protected List<ArithmeticLiteral> arithmetic;
	protected List<EqualityLiteral> conditions;
	
	/**
	 * Initialization the fields with the literals of the
	 * specified clause.
	 * 
	 * @param clause the clause for which the fields must be initialized
	 */
	protected void init(Clause clause) {
		equalities = clause.getEqualityLiterals();
		predicates = clause.getPredicateLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	/**
	 * Returns <code>true</code> if the number of literals is equal to
	 * zero, without taking into account conditions. Returns <code>false</code>
	 * otherwise.
	 * <p>
	 * {@link #init(Clause)} must have been called before.
	 * 
	 * @return <code>true</code> if the number of literals is equal to
	 * zero, without taking into account conditions. Returns <code>false</code>
	 * otherwise.
	 */
	protected boolean isEmptyWithoutConditions() {
		return predicates.isEmpty() && equalities.isEmpty() && arithmetic.isEmpty();
	}
	
	/**
	 * Returns <code>true</code> if the number of literals is equal to
	 * zero, taking into account conditions. Returns <code>false</code>
	 * otherwise.
	 * <p>
	 * {@link #init(Clause)} must have been called before.
	 * 
	 * @return <code>true</code> if the number of literals is equal to
	 * zero, taking into account conditions. Returns <code>false</code>
	 * otherwise.
	 */
	protected boolean isEmptyWithConditions() {
		return predicates.isEmpty() && equalities.isEmpty() && arithmetic.isEmpty() && conditions.isEmpty();
	}
	
}
