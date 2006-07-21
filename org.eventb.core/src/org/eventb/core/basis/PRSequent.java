/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IConfidence;
import org.eventb.core.prover.IProofTree;
import org.eventb.internal.core.pom.PRUtil;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
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
	
	public IPRProofTree getProofTree() throws RodinDBException {
		IPRProofTree proof = ((IPRFile)getOpenable()).getProofTree(getName());
		assert proof != null;
		return proof;
	}

	public IProofTree rebuildProofTree() throws RodinDBException {
		return PRUtil.rebiuldProofTree(this);
	}
	

	public IProofTree makeFreshProofTree() throws RodinDBException {
		return PRUtil.makeInitialProofTree(this);
	}
	
	public void updateProofTree(final IProofTree pt) throws CoreException {
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				PRUtil.updateProofTree(PRSequent.this, pt);
			}

		}, null);
	}

	public boolean isClosed() throws RodinDBException {
		if (isProofBroken()) return false;
		IPRProofTree proof = getProofTree();
		return (proof.getConfidence() != IConfidence.PENDING);
	}

	public boolean isProofBroken() throws RodinDBException {
		return getContents().equals("ProofBroken");
	}

	public void setProofBroken(boolean broken) throws RodinDBException {
		if (broken) setContents("ProofBroken");
		else setContents("ProofValid");
	}

	public boolean proofAttempted() throws RodinDBException {
		IPRProofTree proof = getProofTree();
		if (proof == null || ! proof.proofAttempted()) return false;
		return true;
	}
	
	
	
}
