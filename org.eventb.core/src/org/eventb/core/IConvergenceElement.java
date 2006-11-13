/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B convergence properties of an event.
 * <p>
 * A convergence is represented by an integer constant. Only the specified values
 * {@link IConvergenceElement#ORDINARY}, {@link IConvergenceElement#CONVERGENT}, and
 * {@link IConvergenceElement#ANTICIPATED} are permitted.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IConvergenceElement extends IInternalElement {

	int ORDINARY = 0;
	int CONVERGENT = 1; 
	int ANTICIPATED = 2;
	
	/**
	 * Sets the convergence to one of the values {@link IConvergenceElement#ORDINARY}, 
	 * {@link IConvergenceElement#CONVERGENT}, or {@link IConvergenceElement#ANTICIPATED}.
	 * @param value the convergence to be set
	 * @param monitor 
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setConvergence(int value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the convergence stored in this attribute.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the convergence; one of  {@link IConvergenceElement#ORDINARY}, 
	 * {@link IConvergenceElement#CONVERGENT}, {@link IConvergenceElement#ANTICIPATED}
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	int getConvergence(IProgressMonitor monitor) throws RodinDBException;
}
