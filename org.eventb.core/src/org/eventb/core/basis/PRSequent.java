/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPSFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPSstatus;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

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
public class PRSequent extends InternalElement implements IPRSequent {

	public PRSequent(String name, IRodinElement parent) {
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
		IPRProofTree proofTree = ((IPRFile)getOpenable()).getProofTree(getName());
		// assert proofTree != null;
		if ( proofTree == null || (!proofTree.exists())) return null;
		return proofTree;
	}

//	public IProofTree rebuildProofTree() throws RodinDBException {
//		return PRUtil.rebiuldProofTree(this);
//	}
	

//	public IProofTree makeFreshProofTree() throws RodinDBException {
//		return PRUtil.makeInitialProofTree(this);
//	}
	
//	public void updateProofTree(final IProofTree pt) throws CoreException {
//		RodinCore.run(new IWorkspaceRunnable() {
//			public void run(IProgressMonitor monitor) throws CoreException {
//				PRUtil.updateProofTree(PRSequent.this, pt);
//			}
//
//		}, null);
//	}

	public boolean isProofBroken() throws RodinDBException {
		return getContents().equals("ProofBroken");
	}

	public void setProofBroken(boolean broken) throws RodinDBException {
		if (broken) setContents("ProofBroken");
		else setContents("ProofValid");
	}

	public IPOSequent getPOSequent() {
		IPRFile prFile = (IPRFile) getOpenable();
		IPOFile poFile = prFile.getPOFile();
		IPOSequent poSeq = (IPOSequent) poFile.getInternalElement(IPOSequent.ELEMENT_TYPE,getName());
		if (! poSeq.exists()) return null;
		return poSeq;
	}

	public void updateStatus() throws RodinDBException {
		IProverSequent seq =  POLoader.readPO(getPOSequent());
		final IPRProofTree proofTree = getProofTree();
		IProofDependencies deps = proofTree.getProofDependencies();
		boolean validity = ProverLib.proofReusable(deps,seq);
		setProofBroken(! validity);
	}
	
	
	
}
