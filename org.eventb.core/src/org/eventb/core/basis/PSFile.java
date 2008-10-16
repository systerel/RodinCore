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
import org.eventb.core.IPSFile;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B proof status files as an extension of the Rodin
 * database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPSFile</code>.
 * </p>
 * 
 * @author Farhad Mehta
 */
@Deprecated
public class PSFile extends EventBFile implements IPSFile {

	/**
	 * Constructor used by the Rodin database.
	 */
	public PSFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public IPSStatus[] getStatuses() throws RodinDBException {
		IPSRoot root = (IPSRoot) getRoot();
		return root.getStatuses();
	}

	public IPSStatus getStatus(String name) {
		IPSRoot root = (IPSRoot) getRoot();
		return root.getStatus(name);
	}

}