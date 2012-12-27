/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinDBException;

/**
 * An accuracy element contains a grade of accuracy: it is accurate or not. This
 * is used by the static checker to annotate statically checked contexts ({@link ISCContextRoot}),
 * machines ({@link ISCMachineRoot}), and events ({@link ISCEvent}) with
 * information of whether some attribute or child element was added, removed, or
 * modified.
 * <p>
 * The accuracy information is then propagated by the proof obligation generator
 * to proof obligations ({@link IPOSequent}).
 * <p>
 * The accuracy is an estimation only. It may be used to inform the user of
 * inaccurate elements in derived files, e.g., proof obligations.
 * 
 * @see ISCContextRoot
 * @see ISCMachineRoot
 * @see ISCEvent
 * @see IPOSequent
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IAccuracyElement {

	/**
	 * Returns whether this element is accurate or not.
	 * 
	 * @return the accuracy of this element
	 * 
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	boolean isAccurate() throws RodinDBException;
	
	/**
	 * Sets the accuracy of this element.
	 * 
	 * @param accurate
	 *            the accuracy of the element:
	 *            <code>true</code> means it is accurate,
	 *            <code>false</code> means it is not
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setAccuracy(boolean accurate, IProgressMonitor monitor) throws RodinDBException;
	
}
