/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPredicateTable;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateTable 
implements IPredicateTable {

	private List<ISCPredicateElement> predicateElements;
	private List<Predicate> predicateTable;
	private HashSet<Predicate> guardPredicates;

	public PredicateTable() {
		predicateElements = new LinkedList<ISCPredicateElement>();
		predicateTable = new LinkedList<Predicate>();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#addElement(org.eventb.core.ISCPredicateElement, org.eventb.core.ast.ITypeEnvironment, org.eventb.core.ast.FormulaFactory)
	 */
	public void addElement(ISCPredicateElement element, ITypeEnvironment typeEnvironment, FormulaFactory factory) throws RodinDBException {
		predicateElements.add(element);
		predicateTable.add(element.getPredicate(factory, typeEnvironment));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getElements()
	 */
	public List<ISCPredicateElement> getElements() {
		return predicateElements;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getPredicates()
	 */
	public List<Predicate> getPredicates() {
		return predicateTable;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#trim()
	 */
	public void trim() {
		predicateElements = new ArrayList<ISCPredicateElement>(predicateElements);
		predicateTable = new ArrayList<Predicate>(predicateTable);
		guardPredicates = new HashSet<Predicate>(predicateTable.size() * 4 / 3 + 1);
		guardPredicates.addAll(predicateTable);		
	}
	
	public boolean containsAll(Collection<Predicate> predicates) {
		return guardPredicates.containsAll(predicates);
	}

	public boolean contains(Predicate predicate) {
		return guardPredicates.contains(predicate);
	}

}
