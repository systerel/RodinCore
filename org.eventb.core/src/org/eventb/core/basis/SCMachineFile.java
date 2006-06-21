/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInternalMachine;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B SC machines as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCMachineFile</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SCMachineFile extends RodinFile implements ISCMachineFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCMachineFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public String getElementType() {
		return ISCMachineFile.ELEMENT_TYPE;
	}

	public ISCVariable[] getSCVariables() throws RodinDBException {
		return SCMachineUtil.getSCVariables(this); 
	}
	
	public ISCEvent[] getSCEvents() throws RodinDBException {
		return SCMachineUtil.getSCEvents(this); 
	}
	
	public IMachineFile getMachineFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IMachineFile) project.getRodinFile(uName);
	}

	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		ISCRefinesMachine machine = getRefinesClause();
		if (machine == null)
			return null;
		else
			return machine.getAbstractSCMachine();
	}

	public ISCInternalContext[] getSCInternalContexts() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCInternalContext.ELEMENT_TYPE);
		SCInternalContext[] contexts = new SCInternalContext[list.size()];
		list.toArray(contexts);
		return contexts; 
	}

	public ISCInternalMachine[] getSCInternalMachines() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCInternalMachine.ELEMENT_TYPE);
		SCInternalMachine[] theorems = new SCInternalMachine[list.size()];
		list.toArray(theorems);
		return theorems; 
	}

	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		return SCMachineUtil.getSCInvariants(this);
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return SCMachineUtil.getSCTheorems(this);
	}

	private ISCRefinesMachine getRefinesClause() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCRefinesMachine.ELEMENT_TYPE);
		if (list.size() == 1) {
			return (SCRefinesMachine) list.get(0);
		}
		return null;
	}

}
