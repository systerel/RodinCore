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
import org.eventb.core.basis.EventBProofElement;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.RodinDBException;

/**
 * This interface contains getter and setter methods for internal elements that record 
 * and cache proof information.
 * 
 * <p>
 * More concrete interpretations of the values stored in the attributes can be found in 
 * the interfaces that extend this interface.
 * </p>
 * 
 * 
 * @see IPRProof
 * @see IPSStatus
 * @see EventBProofElement
 * 
 * @author Farhad Mehta
 * 
 * @since 1.0
 */
public interface IPRProofInfoElement {

	/**
	 * Returns the confidence attribute associated to this internal element. 
	 * If the confidence attribute is not present, this method 
	 * returns {@link IConfidence#UNATTEMPTED}.
	 * 
	 * @return the value of the confidence attribute, or {@link IConfidence#UNATTEMPTED} 
	 * 			in case it is not present. 
	 * 
	 * @throws RodinDBException
	 */
	int getConfidence() throws RodinDBException;
	
	/**
	 * Sets the confidence attribute associated to this internal element. 
	 * 
	 * <p>
	 * In case the value to set is {@link IConfidence#UNATTEMPTED}, the attribute is removed. 
	 * This means that the only values of this attribute that can occur are those not equal to
	 * {@link IConfidence#UNATTEMPTED}, this value being encoded as the absence of this attribute. 
	 * </p>
	 * 
	 * @param confidence
	 * 			The confidence to set this attribute to
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException;
	

	/**
	 * Returns the manual proof attribute associated to this internal element. 
	 * If this attribute is not present, this method returns false.
	 * 
	 * @return the value of the manual proof attribute, or false in case it is 
	 * 			not present. 
	 * 
	 * @throws RodinDBException
	 */
	boolean getHasManualProof() throws RodinDBException;

	/**
	 * Sets the manual proof attribute associated to this internal element.
	 * 
	 * <p>
	 * In case the value to set is <code>false</code>, the attribute is removed. 
	 * This means that the only value of this attribute that can occur is <code>true</code>,
	 * <code>false</code> being encoded as the absence of this attribute. 
	 * </p>
	 * 
	 * @param value
	 * 			The value to set this attribute to
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setHasManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException;

}
