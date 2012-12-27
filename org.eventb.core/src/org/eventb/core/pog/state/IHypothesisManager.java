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
package org.eventb.core.pog.state;


import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.IRodinElement;

/**
 * Hypotheses in a PO file are arranged in a tree structure. Hypothesis managers are used
 * to anticipate and create this structure. The hypotheses of each manegers from a list. 
 * Often, there is more than one hypothesis manager. In that case these hypothesis managers 
 * form a tree: the hypotheses tree of the PO file. Hypothesis managers are plugged together 
 * by means of <b>root hypotheses</b> and <b>full hypotheses</b> that refer to the hypothesis
 * sets immediately preceding the hypotheses list of a hypothesis manager and to the last 
 * hypothesis in this list.
 * <p>
 * Hypotheses are represented by predicate sets.
 * Each predicate set may be associated with a type environment which is represented
 * by a set of typed identifiers. When the predicate sets of this manager are created, the
 * type environment associated with this manager is added to the first predicate set created
 * (the one that is included in all other predicate sets of this manager). The identifiers
 * of this manager can be accessed via the <code>Iterable</code> interface it implements.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IHypothesisManager extends IPOGState, Iterable<FreeIdentifier> {
	
	/**
	 * Returns the parent element from which the hypothesis are taken.
	 * This can be, e.g., an SC context file, an SC machine file, or an SC event.
	 * 
	 * @return the parent element from which the hypothesis are taken
	 */
	IRodinElement getParentElement();
	
	/**
	 * Returns the name for the hypothesis set of a predicate element.
	 * When this method has been called the hypothesis set for for the predicate 
	 * element is created. It will be saved to the PO file when 
	 * <code>createHypotheses()</code> is called. If this method is not called
	 * for a particular predicate element the corresponding hypothesis set is not created. 
	 * @param element the predicate element for which a hypothesis set is required
	 * 
	 * @return the name of the hypothesis set
	 * @throws CoreException if this hypthesis manager is immutable
	 * 
	 */
	IPOPredicateSet makeHypothesis(ISCPredicateElement element) throws CoreException;
	
	/**
	 * Returns a handle to a PO predicate of this hypothesis manager, or <code>null</code>
	 * if the predicate element passed as parameter is not among the predicates managed
	 * by this hypothesis manager.
	 * <p>
	 * This requires that the hypothesis manager is immutable. 
	 * </p>
	 * <p>
	 * Using this method PO predicate sets in the PO file can be referenced before they are created.
	 * </p>
	 * @param element the predicate element corresponding to the predicate
	 * 
	 * @return a handle to the predicate in the PO file corresponding to 
	 * 		the predicate element passed as the parameter
	 * @throws CoreException if this hypothesis manager is mutable
	 */
	IPOPredicate getPredicate(ISCPredicateElement element) throws CoreException;
	
	/**
	 * Returns a handle to the predicate set that contains all predicates managed by this manager.
	 * 
	 * @return a handle to the predicate set that contains all predicates managed by this manager
	 */
	IPOPredicateSet getFullHypothesis();
	
	/**
	 * Returns a handle to the root predicate set of this hypothesis set.
	 * This is the predicate set contained in all hypotheses of this manager.
	 * 
	 * @return a handle to the root predicate set of this hypothesis set
	 */
	IPOPredicateSet getRootHypothesis();
	
}
