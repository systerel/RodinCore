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
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IProof;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Farhad Mehta
 *
 */
public class PRFile extends POFile implements IPRFile {

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
	public String getElementType() {
		return IPRFile.ELEMENT_TYPE;
	}
	
	@Override
	public IPRFile getPRFile() {
		return this;
	}
	
	public IContext getContext() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IContext) project.getRodinFile(scName);
	}

	public IMachine getMachine() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IMachine) project.getRodinFile(scName);
	}

	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName);
	}

	@Override
	public IPRSequent[] getSequents() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPRSequent.ELEMENT_TYPE);
		PRSequent[] sequents = new PRSequent[list.size()];
		list.toArray(sequents);
		return sequents;
	}

	public Map<String, IProof> getProofs() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IProof.ELEMENT_TYPE);
		HashMap<String, IProof> proofs = new HashMap<String, IProof>(list.size());
		for (IRodinElement element : list){
			// avoid two proofs with the same name
			assert proofs.containsKey(element.getElementName());
			proofs.put(element.getElementName(),(IProof)element);
		}
		return proofs;
	}

	public IProof getProof(String name) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IProof.ELEMENT_TYPE);
		// RodinElement[] list = getChildren();
		for (IRodinElement element : list){
			if (element.getElementName().equals(name)) return (IProof)element;
		}
		return null;
	}
	
	

}
