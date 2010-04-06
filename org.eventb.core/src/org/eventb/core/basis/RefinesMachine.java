/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

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
 * @since 1.0
 */
public class RefinesMachine extends EventBElement implements IRefinesMachine {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public RefinesMachine(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	public boolean hasAbstractMachineName() throws RodinDBException {
		return hasAttribute(EventBAttributes.TARGET_ATTRIBUTE);
	}

	public String getAbstractMachineName() throws RodinDBException {
		return getAttributeValue(EventBAttributes.TARGET_ATTRIBUTE);
	}

	@Override
	public IInternalElementType<IRefinesMachine> getElementType() {
		return ELEMENT_TYPE;
	}

	public IRodinFile getAbstractMachine() throws RodinDBException {
		return getAbstractMachineRoot().getRodinFile();
	}

	public IMachineRoot getAbstractMachineRoot() throws RodinDBException {
		final String bareName = getAbstractMachineName();
		return getEventBProject().getMachineRoot(bareName);
	}

	public IRodinFile getAbstractSCMachine() throws RodinDBException {
		return getAbstractSCMachineRoot().getRodinFile();
	}

	public ISCMachineRoot getAbstractSCMachineRoot() throws RodinDBException {
		final String bareName = getAbstractMachineName();
		return getEventBProject().getSCMachineRoot(bareName);
	}

	public void setAbstractMachineName(String name, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(EventBAttributes.TARGET_ATTRIBUTE, name, monitor);
	}

	@Deprecated
	public void setAbstractMachineName(String name) throws RodinDBException {
		setAbstractMachineName(name, null);
	}

}
