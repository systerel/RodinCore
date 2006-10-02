/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) machines as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * file element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IMachineFile</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class MachineFile extends EventBFile implements IMachineFile {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public MachineFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getVariables()
	 */
	public IVariable[] getVariables() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IVariable.ELEMENT_TYPE);
		Variable[] variables = new Variable[list.size()];
		list.toArray(variables);
		return variables; 
	}
	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getTheorems()
	 */
	public ITheorem[] getTheorems() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ITheorem.ELEMENT_TYPE);
		Theorem[] theorems = new Theorem[list.size()];
		list.toArray(theorems);
		return theorems; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getInvariants()
	 */
	public IInvariant[] getInvariants() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IInvariant.ELEMENT_TYPE);
		Invariant[] invariants = new Invariant[list.size()];
		list.toArray(invariants);
		return invariants; 
	}
	
	public IEvent[] getEvents() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IEvent.ELEMENT_TYPE);
		Event[] events = new Event[list.size()];
		list.toArray(events);
		return events; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getSees()
	 */
	public ISeesContext[] getSeesClauses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISeesContext.ELEMENT_TYPE);
		
		SeesContext[] sees = new SeesContext[list.size()];
		list.toArray(sees);
		return sees; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getSCMachine()
	 */
	public ISCMachineFile getSCMachineFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getSCMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (ISCMachineFile) project.getRodinFile(scName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getPOFile()
	 */
	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IMachineFile#getPRFile()
	 */
	public IPRFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPRFile) project.getRodinFile(prName);
	}

	public IRefinesMachine getRefinesClause() throws RodinDBException {
		return (IRefinesMachine) getSingletonChild(IRefinesMachine.ELEMENT_TYPE, Messages.database_MachineMultipleRefinesFailure);
	}

	public IVariant getVariant() throws RodinDBException {
		return (IVariant) getSingletonChild(IVariant.ELEMENT_TYPE, Messages.database_MachineMultipleVariantFailure);
	}


}
