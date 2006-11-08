/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSstatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.pom.POLoader;
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
public class PSstatus extends InternalElement implements IPSstatus {

	public PSstatus(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public String getName() {
		return getElementName();
	}
	
	public IPRProofTree getProofTree(){
		IPRProofTree proofTree = ((IPSFile)getOpenable()).getPRFile().getProofTree(getName());
		// assert proofTree != null;
		if ( proofTree == null || (!proofTree.exists())) return null;
		return proofTree;
	}


	public boolean isProofValid() throws RodinDBException {
		return getBooleanAttribute(EventBAttributes.PROOF_VALIDITY_ATTRIBUTE, null);
	}

	private void setProofValid(boolean valid) throws RodinDBException {
		setBooleanAttribute(EventBAttributes.PROOF_VALIDITY_ATTRIBUTE, valid, null);
	}
	
	public int getProofConfidence() throws RodinDBException {
		return getIntegerAttribute(EventBAttributes.CONFIDENCE_ATTRIBUTE, null);
	}
	
	private void setProofConfidence(int confidence) throws RodinDBException {
		setIntegerAttribute(EventBAttributes.CONFIDENCE_ATTRIBUTE, confidence, null);
	}
	
	public IPOSequent getPOSequent() {
		IPSFile psFile = (IPSFile) getOpenable();
		IPOFile poFile = psFile.getPOFile();
		IPOSequent poSeq = (IPOSequent) poFile.getInternalElement(IPOSequent.ELEMENT_TYPE,getName());
		if (! poSeq.exists()) return null;
		return poSeq;
	}

	public void updateStatus() throws RodinDBException {
		IProverSequent seq =  POLoader.readPO(getPOSequent());
		final IPRProofTree proofTree = getProofTree();
		if (proofTree == null) {
			setProofConfidence(IConfidence.UNATTEMPTED);
			setProofValid(true);
			return;
		}
		IProofDependencies deps = proofTree.getProofDependencies();
		boolean valid = ProverLib.proofReusable(deps,seq);
		setProofConfidence(proofTree.getConfidence());
		setProofValid(valid);
	}
	
}

