/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinDBException;

/**
 * Proof obligation stamps signal changes in PO sequents (i.e. proof
 * obligations) and predicate sets (i.e. hypotheses). One stamp is associated
 * with a PO file. If any sequent or predicate set has changed, the PO file
 * stamp is changed too. Deletion of a predicate set or sequent alone does not
 * affect the PO file stamp, insertion does (and is treated like a change).
 * <p>
 * The PO file stamp is related to the maximal stamp used in the PO file: If
 * some sequent or predicate sets changes, the PO file stamp is incremented.
 * This same stamp is then used for all changed sequents and predicate sets. Any
 * change in a PO file increases the PO file stamp, even if none of the
 * remaining proof obligations (or predicate sets) have changed, e.g., after a
 * deletion. This means, the PO file stamp my be larger than any other stamp
 * used in the PO file.
 * </p>
 * 
 * @see IPOFile
 * @see IPOSequent
 * @see IPOPredicateSet
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface IPOStampedElement {
	
	/**
	 * The initial value to be used for stamps. That is, initially the stamp of
	 * an element should be set to <code>INIT_STAMP</code>:
	 * 
	 * <pre>
	 * setStamp(INIT_STAMP, monitor);
	 * </pre>
	 */
	static long INIT_STAMP = Long.MIN_VALUE;
	
	/**
	 * Sets the stamp of this element.
	 * 
	 * @param stamp the stamp to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setStamp(long stamp, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the stamp of this element.
	 * 
	 * @return the stamp of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	long getStamp() throws RodinDBException;

}
