/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBFile extends RodinFile {

	protected EventBFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getPOFile()
	 */
	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getPRFile()
	 */
	public IPSFile getPSFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPSFile) project.getRodinFile(prName);
	}

	@Deprecated
	protected IRodinElement getSingletonChild(IElementType elementType,
			String message) throws RodinDBException {

		return EventBUtil.getSingletonChild(this, elementType, message);
	}

}
