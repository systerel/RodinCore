/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateTable extends State implements IPredicateTable {

	protected List<ISCPredicateElement> predicateElements;
	protected List<Predicate> predicates;

	public PredicateTable() {
		predicateElements = new LinkedList<ISCPredicateElement>();
		predicates = new LinkedList<Predicate>();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#addElement(org.eventb.core.ISCPredicateElement, org.eventb.core.ast.ITypeEnvironment, org.eventb.core.ast.FormulaFactory)
	 */
	public void addElement(ISCPredicateElement element, ITypeEnvironment typeEnvironment, FormulaFactory factory) throws RodinDBException {
		predicateElements.add(element);
		predicates.add(element.getPredicate(factory, typeEnvironment));
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
		return predicates;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#trim()
	 */
	public void trim() {
		predicateElements = new ArrayList<ISCPredicateElement>(predicateElements);
		predicates = new ArrayList<Predicate>(predicates);
	}
	
}
