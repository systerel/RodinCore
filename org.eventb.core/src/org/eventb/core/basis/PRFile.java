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
import org.eventb.core.IPRProof;
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
	
	public IPRProof[] getProofs() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPRProof.ELEMENT_TYPE);
		PRProof[] proofTrees = new PRProof[list.size()];
		list.toArray(proofTrees);
		return proofTrees;
	}

	public IPRProof getProof(String name) {
		IInternalElement proof = getInternalElement(IPRProof.ELEMENT_TYPE,name);
		return (IPRProof) proof;
	}

}
