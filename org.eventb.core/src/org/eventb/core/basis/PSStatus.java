/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import static org.eventb.core.EventBAttributes.CONFIDENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONTEXT_DEPENDENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.MANUAL_PROOF_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PROOF_BROKEN_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B proof obligation status as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface {@link IPSStatus}.
 * </p>
 *
 * @author Farhad Mehta
 *
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PSStatus extends EventBProofElement implements IPSStatus {

	public PSStatus(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType<IPSStatus> getElementType() {
		return ELEMENT_TYPE;
	}
	
	
	@Override
	public IPRProof getProof(){
		final IPSRoot psRoot = (IPSRoot) getRoot();
		final IPRRoot prRoot = psRoot.getPRRoot();
		return prRoot.getProof(getElementName());
	}


	@Override
	public boolean isBroken() throws RodinDBException {
		return isAttributeTrue(PROOF_BROKEN_ATTRIBUTE);
	}

	@Override
	public void setBroken(boolean value, IProgressMonitor monitor)
			throws RodinDBException {

		setAttributeTrue(PROOF_BROKEN_ATTRIBUTE, value, monitor);
	}
	
	
	@Override
	public void copyProofInfo(IProgressMonitor monitor) throws RodinDBException {
		IPRProof proof = getProof();
		if (proof.exists()) {
			setAttributeValue(CONFIDENCE_ATTRIBUTE, proof.getConfidence(), monitor);
			setAttributeValue(MANUAL_PROOF_ATTRIBUTE, proof.getHasManualProof(), monitor);
		} else {
			removeAttribute(CONFIDENCE_ATTRIBUTE, monitor);
			removeAttribute(MANUAL_PROOF_ATTRIBUTE, monitor);
		}
	}
	
	@Override
	public IPOSequent getPOSequent() {
		final IPSRoot psRoot = (IPSRoot) getRoot();
		return psRoot.getPORoot().getSequent(getElementName());
	}

	/**
	 * @since 3.0
	 */
	@Override
	public boolean isContextDependent() throws RodinDBException {
		return isAttributeTrue(CONTEXT_DEPENDENT_ATTRIBUTE);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void setContextDependent(boolean value, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeTrue(CONTEXT_DEPENDENT_ATTRIBUTE, value, monitor);
	}
	
}

