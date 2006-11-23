/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Proof Status elements in Event-B Proof Status (PS) files.
 * 
 * <p>
 * The convention used for associating proof obligations (IPOSequent) in the PO
 * file to proof status elements (IPSStatus) in the PS file, and proofs (in the
 * PR file) is that they all have the identical element name.
 * </p>
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see #getElementName() , IPOSequent, IPRProof
 * 
 * @author Farhad Mehta
 * 
 */
public interface IPSStatus extends IInternalElement {

	IInternalElementType ELEMENT_TYPE = RodinCore
			.getInternalElementType(EventBPlugin.PLUGIN_ID + ".psStatus"); //$NON-NLS-1$

	/**
	 * Returns the proof associated to this proof obligation from the RODIN
	 * database.
	 * <p>
	 * This is a handle-only method. The proof element may or may not be
	 * present.
	 * </p>
	 * 
	 * @return the proof associated to this status element
	 */
	IPRProof getProof();

	/**
	 * Returns the proof obligation associated to this status element from the
	 * RODIN database.
	 * <p>
	 * This is a handle-only method. The returned element may or may not be
	 * present.
	 * </p>
	 * 
	 * @return the sequent associated to this proof obligation
	 */
	IPOSequent getPOSequent();

	/**
	 * Returns the value stored in the proof validity attribute of this proof
	 * status element.
	 * <p>
	 * When consistent, the proof validity attribute is <code>true</code> iff
	 * the proof stored in the associated proof element is valid (or applicable)
	 * for the associated proof obligation.
	 * </p>
	 * 
	 * @return The value stored in the proof validity attribute of this proof
	 *         status element.
	 * 
	 * @throws RodinDBException
	 */
	boolean getProofValidAttribute() throws RodinDBException;

	/**
	 * Sets the value stored in the proof validity attribute of this proof
	 * status element.
	 * <p>
	 * When consistent, the proof validity attribute is <code>true</code> iff
	 * the proof stored in the associated proof element is valid (or applicable)
	 * for the associated proof obligation.
	 * </p>
	 * 
	 * @param valid
	 *            The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setProofValidAttribute(boolean valid, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value stored in the confidence attribute of this proof status
	 * element.
	 * <p>
	 * When consistent, the confidence attribute is identical to the confidence
	 * attribute stored in the associated proof element.
	 * </p>
	 * 
	 * @return The value stored in the confidence attribute of this proof status
	 *         element.
	 * 
	 * @throws RodinDBException
	 */
	int getProofConfidence() throws RodinDBException;

	/**
	 * Sets value stored in the confidence attribute of this proof status
	 * element.
	 * <p>
	 * When consistent, the confidence attribute is identical to the confidence
	 * attribute stored in the associated proof element.
	 * </p>
	 * 
	 * @param confidence
	 *            The confidence value to set
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 */
	void setProofConfidence(int confidence, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns whether this proof obligation has been discharged manually. A
	 * proof obligation is considered as manually discharged if the end user
	 * entered manually its associated proof (even partially).
	 * <p>
	 * The returned value is <code>true</code> iff the corresponding attribute
	 * contains <code>true</code>. Hence, if the attribute is absent,
	 * <code>false</code> is returned.
	 * </p>
	 * 
	 * @return <code>true</code> if the user contributed to the proof of this
	 *         proof obligation
	 * 
	 * @throws RodinDBException
	 */
	boolean hasManualProof() throws RodinDBException;

	/**
	 * Sets whether this proof obligation has been discharged manually.
	 * 
	 * @param value
	 *            The value to set to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * 
	 * @throws RodinDBException
	 * @see #hasManualProof()
	 */
	void setHasManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException;

}
