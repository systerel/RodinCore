/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * A hint for a proof of a proof obligation. A hint is labeled so that hints can be
 * accumulated.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel(IProgressMonitor)
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 */
public interface IPOHint extends ILabeledElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poHint"); //$NON-NLS-1$
	
	
	/**
	 * Predefined hint labels: 
	 * <ul>
	 * <li><code>SELECT_HYP</code> for selecting hypothesis</li>
	 * <li><code>CHOOSE_PRV</code> for suggesting a proof strategy by way of a characterisation of 
	 * the proof obligation
	 * </ul>
	 */
	String SELECT_HYP = "select-hyp";
	String CHOOSE_PRV = "choose-prv";
	
	/**
	 * Set the value of the hint. There are no constraints on the format or contents of a hint. 
	 * @param value the value of the hint
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setHint(String value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the value of this hint.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the value of this hint
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	String getHint(IProgressMonitor monitor) throws RodinDBException;
}
