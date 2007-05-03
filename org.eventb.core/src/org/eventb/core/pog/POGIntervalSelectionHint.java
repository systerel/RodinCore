/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.rodinp.core.RodinDBException;

/**
 * An interval selection hint specifies a sequence of predicate sets.
 * 
 * @see IPOSelectionHint
 * 
 * @author Stefan Hallerstede
 *
 */
class POGIntervalSelectionHint implements IPOGHint {

	private final IPOPredicateSet start;
	private final IPOPredicateSet end;
	
	/**
	 * Creates an interval selection hint.
	 * 
	 * @param start the predicate set immediately preceding the interval 
	 * @param end the last predicate set in the interval
	 */
	POGIntervalSelectionHint(final IPOPredicateSet start, final IPOPredicateSet end) {
		super();
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the last predicate set in the interval.
	 * 
	 * @return the last predicate set in the interval
	 */
	public IPOPredicateSet getEnd() {
		return end;
	}

	/**
	 * Returns the predicate set immediately preceding the interval.
	 * 
	 * @return the predicate set immediately preceding the interval
	 */
	public IPOPredicateSet getStart() {
		return start;
	}

	public void create(IPOSequent sequent, String name, IProgressMonitor monitor) 
	throws RodinDBException {
		IPOSelectionHint selectionHint = sequent.getSelectionHint(name);
		selectionHint.create(null, monitor);
		selectionHint.setInterval(start, end, null);
	}
	
	
}
