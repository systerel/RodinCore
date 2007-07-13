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
 * Proof obligation stamps signal changes in {@link IPRFile} and {@link IPRProof}
 * elements.
 * <p>
 * When a {@link IPRProof} changes, its stamp is changed and the stamp of a 
 * {@link IPRFile} is also changed. The stamp of a {@link IPRFile} may also
 * independently change in case there is a change in the file such as deletion
 * of a proof.
 * </p>
 * 
 * <p>
 * Although stamps are represented as longs, they may not be used for comparison 
 * (i.e. using > or <), but only be subjected to equality checks. Even if the implementation
 * increments stamp values, they may overflow and loop back to the minimum value.
 * </p>
 * 
 * @see IPRFile
 * @see IPRProof
 * @see IPSFile
 * @see IPSStatus
 * 
 * @author Farhad Mehta
 * 
 */
public interface IPRStampedElement {
	
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
	 * Sets the prStamp of this element.
	 * 
	 * @param stamp the stamp to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setPRStamp(long stamp, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the PRStamp of this element. In case this element does not have a
	 * PRstamp, the {@link #INIT_STAMP} is returned.
	 * 
	 * @return the stamp of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	long getPRStamp() throws RodinDBException;

}
