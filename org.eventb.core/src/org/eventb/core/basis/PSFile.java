/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IPSFile;
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
		return (IPSStatus[]) getChildrenOfType(PSStatus.ELEMENT_TYPE);
	}

	public IPSStatus getStatus(String name) {
		return (IPSStatus) getInternalElement(IPSStatus.ELEMENT_TYPE, name);
	}

}