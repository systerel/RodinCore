/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.pp;

/**
 * Common protocol for monitoring a Predicate Prover proof.
 * <p>
 * Provides means for canceling a proof.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 0.2
 */
public interface IPPMonitor {

	/**
	 * Tells whether this proof has been canceled. This method is regularly
	 * called by the Predicate Prover. It it returns true, then the current
	 * proof is aborted.
	 * 
	 * @return <code>true</code> iff this proof has been canceled.
	 */
	boolean isCanceled();
}
