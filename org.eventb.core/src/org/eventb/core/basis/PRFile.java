/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPSstatus;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Farhad Mehta
 *
 */
public class PRFile extends RodinFile implements IPRFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PRFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	
	public IPRFile getPRFile() {
		return this;
	}
	
	public IContextFile getContext() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IContextFile) project.getRodinFile(scName);
	}

	public IMachineFile getMachine() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IMachineFile) project.getRodinFile(scName);
	}

	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName).getSnapshot();
	}

	@Deprecated
	public IPSstatus[] getSequents() throws RodinDBException {
		IRodinElement[] list = getChildrenOfType(IPSstatus.ELEMENT_TYPE);
		IPSstatus[] sequents = new PSstatus[list.length];
		for (int i = 0; i < sequents.length; i++) {
			sequents[i] = (IPSstatus) list[i];
		}
		return sequents;
	}
	
	@Deprecated
	public IPSstatus getSequent(String name) {
		IPSstatus prSeq = (IPSstatus) getInternalElement(IPSstatus.ELEMENT_TYPE,name);
		if (!prSeq.exists()) return null;
		return prSeq;
	}

	public Map<String, IPRProofTree> getProofTrees() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPRProofTree.ELEMENT_TYPE);
		HashMap<String, IPRProofTree> proofs = new HashMap<String, IPRProofTree>(list.size());
		for (IRodinElement element : list){
			// avoid two proofs with the same name
			assert (! proofs.containsKey(element.getElementName()));
			proofs.put(element.getElementName(),(IPRProofTree)element);
		}
		return proofs;
	}

	public IPRProofTree getProofTree(String name) {
		IInternalElement proofTree = getInternalElement(IPRProofTree.ELEMENT_TYPE,name);
		if (proofTree.exists()) return (IPRProofTree) proofTree;
		return null;
	}

	public IPRProofTree createProofTree(String name) throws RodinDBException {
		IPRProofTree prProofTree = (IPRProofTree) createInternalElement(
				IPRProofTree.ELEMENT_TYPE,name, null, null);
		prProofTree.initialize();
		return prProofTree;
	}
	
	

}
