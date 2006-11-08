/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSFileRename;
import org.eventb.core.IPSstatusRename;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Farhad Mehta
 *
 */
public class PSFileRename extends RodinFile implements IPSFileRename {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PSFileRename(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
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

	public IPSFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPSFile) project.getRodinFile(prName).getSnapshot();
	}
	
	public IPSstatusRename[] getStatus() throws RodinDBException {
		IRodinElement[] list = getChildrenOfType(IPSstatusRename.ELEMENT_TYPE);
		IPSstatusRename[] statuses = new PSstatusRename[list.length];
		for (int i = 0; i < statuses.length; i++) {
			statuses[i] = (IPSstatusRename) list[i];
		}
		return statuses;
	}
	
	public IPSstatusRename getStatus(String name) {
		IPSstatusRename status = (IPSstatusRename) getInternalElement(IPSstatusRename.ELEMENT_TYPE,name);
		if (!status.exists()) return null;
		return status;
	}

}
