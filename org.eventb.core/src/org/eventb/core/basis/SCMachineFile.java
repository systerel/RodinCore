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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

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
public class SCMachineFile extends EventBFile implements ISCMachineFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCMachineFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCVariable[] getSCVariables(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = 
			getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		SCVariable[] variables = new SCVariable[list.size()];
		list.toArray(variables);
		return variables;
	}
	
	@Deprecated
	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getSCVariables(null);
	}
	
	public ISCEvent[] getSCEvents(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = 
			getFilteredChildrenList(ISCEvent.ELEMENT_TYPE);
		SCEvent[] events = new SCEvent[list.size()];
		list.toArray(events);
		return events;
	}
	
	@Deprecated
	public ISCEvent[] getSCEvents() throws RodinDBException {
		return getSCEvents(null);
	}
	
	public IMachineFile getMachineFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IMachineFile) project.getRodinFile(uName);
	}

	public ISCMachineFile getAbstractSCMachine(IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesMachine machine = getRefinesClause(monitor);
		if (machine == null)
			return null;
		else
			return machine.getAbstractSCMachine(null);
	}

	@Deprecated
	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		return getAbstractSCMachine(null);
	}

	public ISCInternalContext[] getSCSeenContexts(IProgressMonitor monitor) 
	throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCInternalContext.ELEMENT_TYPE);
		SCInternalContext[] contexts = new SCInternalContext[list.size()];
		list.toArray(contexts);
		return contexts; 
	}

	@Deprecated
	public ISCInternalContext[] getSCSeenContexts() 
	throws RodinDBException {
		return getSCSeenContexts(null); 
	}

	public ISCInvariant[] getSCInvariants(IProgressMonitor monitor) 
	throws RodinDBException {
		ArrayList<IRodinElement> list =
			getFilteredChildrenList(ISCInvariant.ELEMENT_TYPE);
		SCInvariant[] invariants = new SCInvariant[list.size()];
		list.toArray(invariants);
		return invariants;
	}

	@Deprecated
	public ISCInvariant[] getSCInvariants() 
	throws RodinDBException {
		return getSCInvariants(null);
	}

	public ISCTheorem[] getSCTheorems(IProgressMonitor monitor) 
	throws RodinDBException {
		ArrayList<IRodinElement> list = 
			getFilteredChildrenList(ISCTheorem.ELEMENT_TYPE);
		SCTheorem[] theorems = new SCTheorem[list.size()];
		list.toArray(theorems);
		return theorems;
	}

	@Deprecated
	public ISCTheorem[] getSCTheorems() 
	throws RodinDBException {
		return getSCTheorems();
	}

	public ISCRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException {
		return (ISCRefinesMachine) getSingletonChild(
				ISCRefinesMachine.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleRefinesFailure);
	}

	public ISCVariant getSCVariant(IProgressMonitor monitor) throws RodinDBException {
		return (ISCVariant) getSingletonChild(
				ISCVariant.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleVariantFailure);
	}

	@Deprecated
	public ISCVariant getSCVariant() throws RodinDBException {
		return getSCVariant(null);
	}

}
