/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
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
		final IPRFile prFile = ((IPSFile)getOpenable()).getPRFile();
		IPRProof proofTree = prFile.getProof(getElementName());
		// if ( proofTree == null || (!proofTree.exists())) return null;
		return proofTree;
	}


	public boolean getProofValidAttribute() throws RodinDBException {
		return getAttributeValue(EventBAttributes.PROOF_VALIDITY_ATTRIBUTE);
	}

	public void setProofValidAttribute(boolean valid, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.PROOF_VALIDITY_ATTRIBUTE, valid,monitor);
	}
		
	public int getProofConfidence() throws RodinDBException {
		return getAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE);
	}
	
	public void setProofConfidence(int confidence, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.CONFIDENCE_ATTRIBUTE, confidence, monitor);
	}
	
	public IPOSequent getPOSequent() {
		IPSFile psFile = (IPSFile) getOpenable();
		IPOFile poFile = psFile.getPOFile();
		IPOSequent poSeq = (IPOSequent) poFile.getInternalElement(IPOSequent.ELEMENT_TYPE,getElementName());
		if (! poSeq.exists()) return null;
		return poSeq;
	}

	public boolean hasAutoProofAttribute() throws RodinDBException {
		return hasAttribute(EventBAttributes.AUTO_PROOF_ATTRIBUTE);
	}

	public boolean getAutoProofAttribute() throws RodinDBException {
		// if (!hasAutoProofAttribute(monitor)) return false;
		return getAttributeValue(EventBAttributes.AUTO_PROOF_ATTRIBUTE);
	}
	
	public void setAutoProofAttribute(boolean autoProof, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.AUTO_PROOF_ATTRIBUTE, autoProof, monitor);
	}
	
}

