/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pog.state.IPredicateTable;
import org.eventb.internal.core.tool.state.ToolState;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class PredicateTable extends ToolState implements IPredicateTable {

	protected final ISCPredicateElement[] predicateElements;
	protected final Predicate[] predicates;

	public PredicateTable(
			ISCPredicateElement[] elements, 
			ITypeEnvironment typeEnvironment, 
			FormulaFactory factory) throws RodinDBException {
		predicateElements = elements;
		predicates = new Predicate[elements.length];
		
		for (int i=0; i<elements.length; i++) {
			predicates[i] = elements[i].getPredicate(factory, typeEnvironment);
		}
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getElements()
	 */
	public ISCPredicateElement[] getElements() {
		ISCPredicateElement[] pe = new ISCPredicateElement[predicateElements.length];
		System.arraycopy(predicateElements, 0, pe, 0, predicateElements.length);
		return pe;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getPredicates()
	 */
	public Predicate[] getPredicates() {
		Predicate[] p = new Predicate[predicates.length];
		System.arraycopy(predicates, 0, p, 0, predicates.length);
		return p;
	}

	public int indexOfPredicate(Predicate predicate) {
		for (int i=0; i<predicates.length; i++) {
			if (predicates[i].equals(predicate))
				return i;
		}
		return -1;
	}
	
}
