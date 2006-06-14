/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IProof;
import org.eventb.core.IProof.Status;
import org.eventb.core.prover.IProofTree;
import org.eventb.internal.core.pom.PRUtil;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B PR proof obligation as an extension of the Rodin database.
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
public class PRSequent extends POSequent implements IPRSequent {

	public PRSequent(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return IPRSequent.ELEMENT_TYPE;
	}
	
	public IProof getProof() throws RodinDBException {
		IProof proof = ((IPRFile)getOpenable()).getProof(getName());
		assert proof != null;
		return proof;
	}

	public IProofTree makeProofTree() throws RodinDBException {
		return PRUtil.makeProofTree(this);
	}
	
	public void updateStatus(IProofTree pt) throws RodinDBException {
		PRUtil.updateStatus(this,pt);
	}

	public boolean isDischarged() throws RodinDBException {
		IProof proof = getProof();
		return (proof.getStatus() == Status.DISCHARGED);
	}
	
	
	
}
