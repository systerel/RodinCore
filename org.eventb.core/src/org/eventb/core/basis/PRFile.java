/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Farhad Mehta
 */
public class PRFile extends RodinFile implements IPRFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PRFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPRProof[] getProofs() throws RodinDBException {
		return (PRProof[]) getChildrenOfType(IPRProof.ELEMENT_TYPE);
	}

	public IPRProof getProof(String name) {
		return (IPRProof) getInternalElement(IPRProof.ELEMENT_TYPE,name);
	}

}
