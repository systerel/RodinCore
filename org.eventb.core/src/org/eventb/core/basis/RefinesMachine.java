/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IInternalElementType;
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
	public String getAbstractMachineName(IProgressMonitor monitor) throws RodinDBException {
		return getAttributeValue(EventBAttributes.REFINES_ATTRIBUTE, monitor);
	}

	@Deprecated
	public String getAbstractMachineName() throws RodinDBException {
		return getAbstractMachineName(null);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public IInternalElementType getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#getAbstractMachine()
	 */
	public IMachineFile getAbstractMachine(IProgressMonitor monitor) 
	throws RodinDBException {
		final String bareName = getAbstractMachineName(monitor);
		final String scName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = getRodinProject();
		return (IMachineFile) project.getRodinFile(scName);
	}
	
	@Deprecated
	public IMachineFile getAbstractMachine() 
	throws RodinDBException {
		return getAbstractMachine(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#getAbstractSCMachine()
	 */
	public ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) 
	throws RodinDBException {
		final String bareName = getAbstractMachineName(monitor);
		final String scName = EventBPlugin.getSCMachineFileName(bareName);
		final IRodinProject project = getRodinProject();
		return (ISCMachineFile) project.getRodinFile(scName);
	}

	@Deprecated
	public ISCMachineFile getAbstractSCMachine() 
	throws RodinDBException {
		return getAbstractSCMachine(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IRefinesMachine#setAbstractMachineName(java.lang.String)
	 */
	public void setAbstractMachineName(String name, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.REFINES_ATTRIBUTE, name, monitor);
	}

	@Deprecated
	public void setAbstractMachineName(String name) throws RodinDBException {
		setAbstractMachineName(name, null);
	}

}
