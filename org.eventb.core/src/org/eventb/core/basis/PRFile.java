/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B proof files as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPRFile</code>.
 * </p>
 * 
 * @author Farhad Mehta
 */
@Deprecated
public class PRFile extends EventBFile implements IPRFile {

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
		IPRRoot root = (IPRRoot) getRoot();
		return root.getProofs();
	}

	public IPRProof getProof(String name) {
		IPRRoot root = (IPRRoot) getRoot();
		return root.getProof(name);
	}

}
