/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.rodinp.core.RodinDBException;

/**
 * A predicate selection hint specifies a single predicate.
 * 
 * @see IPOSelectionHint
 * 
 * @author Stefan Hallerstede
 *
 */
class POGPredicateSelectionHint implements IPOGHint {
	
	private final IPOPredicate predicate;
	
	/**
	 * Creates a predicate selection hint for <code>predicate</code>.
	 * 
	 * @param predicate the predicate to select
	 */
	POGPredicateSelectionHint(final IPOPredicate predicate) {
		this.predicate = predicate;
	}

	/**
	 * Returns the predicate to select.
	 * 
	 * @return the predicate to select
	 */
	public IPOPredicate getPredicate() {
		return predicate;
	}

	@Override
	public void create(IPOSequent sequent, String name, IProgressMonitor monitor)
			throws RodinDBException {
		IPOSelectionHint selectionHint = sequent.createChild(
				IPOSelectionHint.ELEMENT_TYPE, null, monitor);
		selectionHint.setPredicate(predicate, null);
	}

}
