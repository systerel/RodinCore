/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eventb.core.pm.IHypothesisDelta;
import org.eventb.core.prover.sequent.Hypothesis;

/**
 * @see IHypothesisDelta
 */
public class HypothesisDelta implements IHypothesisDelta {
	Hypothesis hypothesis;
	private int flags;
	
	
	public HypothesisDelta(Hypothesis hypothesis) {
		this.hypothesis = hypothesis;
		flags = 0;
		return;
	}
	

	/**
	 * Specify that the hypothesis has been added to the set of selected hypotheses.
	 */
	public void setAddedToSelected() {flags |= F_ADDED_TO_SELECTED;}
	
	
	/**
	 * Specify that the hypothesis has been removed from the set of selected hypotheses.
	 */
	public void setRemovedFromSelected() {flags |= F_REMOVED_FROM_SELECTED;}
	
	
	/**
	 * Specify that the hypothesis has been added to the set of (valid) cached hypotheses.
	 */
	public void setAddedToCached() {flags |= F_ADDED_TO_CACHED;}
	
	
	/**
	 * Specify that the hypothesis has been removed from the set of (valid) cached hypotheses.
	 */
	public void setRemovedFromCached() {flags |= F_REMOVED_FROM_CACHED;}

	
	/**
	 * Specify that the hypothesis has been added to the set of searched hypotheses.
	 */
	public void setAddedToSearched() {flags |= F_ADDED_TO_SEARCHED;}
	
	
	/**
	 * Specify that the hypothesis has been removed from the set of searched hypotheses.
	 */
	public void setRemovedFromSearched() {flags |= F_REMOVED_FROM_SEARCHED;}

	
	/**
	 * @see org.eventb.core.pm.IHypothesisDelta#getFlags()
	 */
	public int getFlags() {
		return flags;
	}

	
	/**
	 * @see org.eventb.core.pm.IHypothesisDelta#getHypothesis()
	 */
	public Hypothesis getHypothesis() {
		return hypothesis;
	}

}
