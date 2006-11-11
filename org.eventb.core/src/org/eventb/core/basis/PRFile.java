/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
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
	
	public IPRProofTree[] getProofTrees() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(PSstatus.ELEMENT_TYPE);
		PRProofTree[] proofTrees = new PRProofTree[list.size()];
		list.toArray(proofTrees);
		return proofTrees;
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
