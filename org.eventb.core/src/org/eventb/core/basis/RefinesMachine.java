/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B machine refinement as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>IRefinesMachine</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class RefinesMachine extends InternalElement implements IRefinesMachine {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public RefinesMachine(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#getRefinedMachine()
	 */
	public String getAbstractMachineName() throws RodinDBException {
		return getContents();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#getAbstractMachine()
	 */
	public IMachineFile getAbstractMachine() throws RodinDBException {
		final String bareName = getAbstractMachineName();
		final String scName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = getRodinProject();
		return (IMachineFile) project.getRodinFile(scName);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#getAbstractSCMachine()
	 */
	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		final String bareName = getAbstractMachineName();
		final String scName = EventBPlugin.getSCMachineFileName(bareName);
		final IRodinProject project = getRodinProject();
		return (ISCMachineFile) project.getRodinFile(scName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#setAbstractMachineName(java.lang.String)
	 */
	public void setAbstractMachineName(String name) throws RodinDBException {
		setContents(name);
	}

}
