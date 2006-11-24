/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.CONFIDENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.MANUAL_PROOF_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PROOF_BROKEN_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B proof obligation status as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPRSequent</code>.
 * </p>
 *
 * @author Farhad Mehta
 *
 */
public class PSStatus extends InternalElement implements IPSStatus {

	public PSStatus(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	
	public IPRProof getProof(){
		final IPSFile psFile = (IPSFile) getRodinFile();
		final IPRFile prFile = psFile.getPRFile();
		return prFile.getProof(getElementName());
	}


	public boolean isBroken() throws RodinDBException {
		return isAttributeTrue(PROOF_BROKEN_ATTRIBUTE);
	}

	public void setBroken(boolean value, IProgressMonitor monitor)
			throws RodinDBException {

		setAttributeTrue(PROOF_BROKEN_ATTRIBUTE, value, monitor);
	}
		
	public int getProofConfidence() throws RodinDBException {
		return getAttributeValue(CONFIDENCE_ATTRIBUTE);
	}
	
	public void setProofConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(CONFIDENCE_ATTRIBUTE, confidence, monitor);
	}
	
	public IPOSequent getPOSequent() {
		final IPSFile psFile = (IPSFile) getRodinFile();
		final IPOFile poFile = psFile.getPOFile();
		return poFile.getSequent(getElementName());
	}

	public boolean hasManualProof() throws RodinDBException {
		return isAttributeTrue(MANUAL_PROOF_ATTRIBUTE);
	}
	
	public void setManualProof(boolean value, IProgressMonitor monitor)
			throws RodinDBException {

		setAttributeTrue(MANUAL_PROOF_ATTRIBUTE, value, monitor);
	}

	/**
	 * Returns whether this attribute exists and has a <code>true</code> value.
	 * 
	 * @param attrType
	 *    attribute to test
	 * @return <code>true</code> iff both the attribute exists and is true
	 * @throws RodinDBException
	 */
	private boolean isAttributeTrue(IAttributeType.Boolean attrType)
			throws RodinDBException {
		return hasAttribute(attrType) && getAttributeValue(attrType);
	}
	
	/**
	 * Sets the given attribute to the given value, removing the attribute if
	 * this would result in setting it to its default value (<code>true</code>).
	 * 
	 * @param attrType
	 *            attribute to set
	 * @param value
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 */
	private void setAttributeTrue(final IAttributeType.Boolean attrType,
			boolean value, IProgressMonitor monitor) throws RodinDBException {

		if (value) {
			setAttributeValue(attrType, true, monitor);
		} else {
			removeAttribute(attrType, monitor);
		}
	}
	
}

