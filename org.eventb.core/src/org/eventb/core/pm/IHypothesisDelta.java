/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.rodinp.core.IRodinElementDelta.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.prover.sequent.Hypothesis;

/**
 * A Hypothesis delta describes changes in hypothesis sets between two discrete
 * points in time. Given a delta, clients can process the hypothesis that has
 * added or removed.
 * <p>
 * Deltas have different flags depending on the kind of change they
 * represent. The list below summarizes each flag (as returned by
 * <code>getFlags</code>) and its meaning (see individual constants for a more
 * detailled description):
 * <ul>
 * <li><code>F_ADDED_TO_SELECTED</code> - The hypothesis described by the delta has been
 * added to the set of selected hypotheses.</li>
 * <li><code>F_REMOVED_FROM_SELECTED</code> - The hypothesis described by the delta has been
 * removed from the set of selected hypotheses.</li>
 * <li><code>F_ADDED_TO_CACHED</code> - The hypothesis described by the delta has been
 * added to the set of (valid) cached hypotheses.</li>
 * <li><code>F_REMOVED_FROM_CACHED</code> - The hypothesis described by the delta has been
 * removed from the set of (valid) cached hypotheses.</li>
 * <li><code>F_ADDED_TO_SEARCHED</code> - The hypothesis described by the delta has been
 * added to the set of (valid) searched hypotheses.</li>
 * <li><code>F_REMOVED_FROM_SEARCHED</code> - The hypothesis described by the delta has been
 * removed from the set of (valid) searched hypotheses.</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IHypothesisDelta {
	
	/**
	 * Status constant indicating that the element has been added to the set of selected hypotheses.
	 */
	public static final int F_ADDED_TO_SELECTED = 0x00001;
	
	/**
	 * Status constant indicating that the element has been removed from the set of selected hypotheses.
	 */
	public static final int F_REMOVED_FROM_SELECTED = 0x00002;
		
	/**
	 * Status constant indicating that the element has been added to the set of (valid) cached hypotheses.
	 */
	public static final int F_ADDED_TO_CACHED = 0x00004;
	
	/**
	 * Status constant indicating that the element has been removed from the set of (valid) cached hypotheses.
	 */
	public static final int F_REMOVED_FROM_CACHED = 0x00008;
	
	/**
	 * Status constant indicating that the element has been added to the set of (valid) searched hypotheses.
	 */
	public static final int F_ADDED_TO_SEARCHED = 0x00010;
	
	/**
	 * Status constant indicating that the element has been removed from the set of (valid) searched hypotheses.
	 */
	public static final int F_REMOVED_FROM_SEARCHED = 0x00020;
	
	/**
	 * Returns flags that describe how an element has changed. 
	 * Such flags should be tested using the <code>&</code> operand. For example:
	 * <pre>
	 * if ((delta.getFlags() & IHypothesisDelta.F_ADDED_TO_SELECTED) != 0) {
	 * 	// the delta indicates this hypothesis is added to the set of selected hypotheses.
	 * }
	 * </pre>
	 *
	 * @return flags that describe how an element has changed
	 */
	public int getFlags();

	/**
	 * Returns the hypothesis that this delta describes a change to.
	 * @return the hypothesis that this delta describes a change to
	 */
	public Hypothesis getHypothesis();
	
}
