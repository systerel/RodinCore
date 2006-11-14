/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IHypothesisManager extends IStatePOG, Iterable<FreeIdentifier> {
	
	/**
	 * Hypotheses are represented by predicate sets.
	 * Each predicate set may be associated with a type environment which is represented
	 * by a set of typed identifiers. When the predicate sets of this manager are created, the
	 * type environment associated with this manager is added to the first predicate set created
	 * (the one that is included in all other predicate sets of this manager). The identifiers
	 * of this manager can be accessed via the <code>Iterable</code> interface it implements.
	 * 
	 * @param identifier the free identifier to be added
	 */
	void addIdentifier(FreeIdentifier identifier);
	
	/**
	 * Returns the parent element from which the hypothesis are taken
	 * 
	 * @return the parent element from which the hypothesis are taken
	 */
	IRodinElement getParentElement();
	
	/**
	 * Return the name for the hypothesis set of a predicate element.
	 * After this method has been called the hypothesis set must be created
	 * when <code>createHypothesis</code> is called. If this method was not called
	 * the correspondingly named hypothesis set does not need to be created. 
	 * 
	 * @param element the predicate elemenent for which a hypothesis set is required
	 * @return the name of the hypothesis set
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	String getHypothesisName(ISCPredicateElement element, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Creates the requested hypothesis sets in the proof olgigation file.
	 * 
	 * @param file the proof obligation file where to hypothesis are to be generated
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void createHypotheses(IPOFile file, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the name of the predicate set that contains all predicates managed by this manager.
	 * 
	 * @return the name of the predicate set
	 */
	String getFullHypothesisName();
	
	/**
	 * Returns the name of the root predicate set contained in this hypothesis set.
	 * 
	 * @return the name of the predicate set
	 */
	String getRootHypothesisName();
	
	/**
	 * Returns the list of managed predicates in the correct order.
	 * These are not guaranteed to be of the same database type, e.g.,
	 * the list may contain axioms and theorems.
	 * 
	 * @return the list of managed predicates
	 */
	List<ISCPredicateElement> getManagedPredicates();

}
