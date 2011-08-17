/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;


import java.util.List;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.Predicate;

/**
 * Protocol for accessing a group of predicate elements.
 * It is common to
 * <li>
 * <ul>context axioms ({@link IContextAxiomTable})</ul>
 * <ul>machine invariants ({@link IMachineInvariantTable})</ul>
 * <ul>event guards ({@link IConcreteEventGuardTable}, {@link IAbstractEventGuardTable})</ul>
 * </li>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPredicateTable<PE extends ISCPredicateElement> extends IPOGState {

	/**
	 * Returns the predicate elements contained in this predicate table.
	 * 
	 * @return the predicate elements contained in this predicate table
	 */
	List<PE> getElements();
	
	/**
	 * Returns the parsed and type-checked predicates corresponding to the
	 * predicate elements contained in this table.
	 * <p>
	 * The indices of this list correspond to the indices of the list of
	 * predicate elements returned by <code>getElements()</code>.
	 * </p>
	 * 
	 * @return the parsed and type-checked predicates corresponding to the
	 * predicate elements contained in this table
	 */
	List<Predicate> getPredicates();
	
	/**
	 * Returns the index of the specified predicate in the predicate array,
	 * or <code>-1</code> if the predicate is not contained in the array.
	 * 
	 * @param predicate the predicate to search
	 * @return the index of the specified predicate in the predicate array,
	 * or <code>-1</code> if the predicate is not contained in the array
	 */
	int indexOfPredicate(Predicate predicate);

}
