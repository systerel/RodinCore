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
public abstract class PredicateTable<PE extends ISCPredicateElement> 
extends ToolState implements IPredicateTable<PE> {

	protected final PE[] predicateElements;
	protected final Predicate[] predicates;

	public PredicateTable(
			PE[] elements, 
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
	public PE[] getElements() {
		return predicateElements.clone();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pog.IPredicateTable#getPredicates()
	 */
	public Predicate[] getPredicates() {
		return predicates.clone();
	}

	public int indexOfPredicate(Predicate predicate) {
		for (int i=0; i<predicates.length; i++) {
			if (predicates[i].equals(predicate))
				return i;
		}
		return -1;
	}
	
}
