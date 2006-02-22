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
import org.eventb.core.IAxiom;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariantSet;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC machine as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCMachine</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SCMachine extends Machine implements ISCMachine {

	public SCMachine(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ISCMachine.ELEMENT_TYPE;
	}

	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCCarrierSet.ELEMENT_TYPE);
		SCCarrierSet[] carrierSets = new SCCarrierSet[list.size()];
		list.toArray(carrierSets);
		return carrierSets; 
	}
	
	public ISCConstant[] getSCConstants() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCConstant.ELEMENT_TYPE);
		SCConstant[] constants = new SCConstant[list.size()];
		list.toArray(constants);
		return constants; 
	}
	
	public ISCVariable[] getSCVariables() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCVariable.ELEMENT_TYPE);
		SCVariable[] constants = new SCVariable[list.size()];
		list.toArray(constants);
		return constants; 
	}
	
	public ISCEvent[] getSCEvents() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCEvent.ELEMENT_TYPE);
		SCEvent[] events = new SCEvent[list.size()];
		list.toArray(events);
		return events; 
	}
	
	public ISCAxiomSet[] getAxiomSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCAxiomSet.ELEMENT_TYPE);
		ISCAxiomSet[] axiomSets = new ISCAxiomSet[list.size()];
		list.toArray(axiomSets);
		return axiomSets; 
	}
	
	public ISCTheoremSet[] getTheoremSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCTheoremSet.ELEMENT_TYPE);
		ISCTheoremSet[] theoremSets = new ISCTheoremSet[list.size()];
		list.toArray(theoremSets);
		return theoremSets; 
	}
	
	public IAxiom[] getOldAxioms() throws RodinDBException {
		ArrayList<IRodinElement> axiomSetList = getFilteredChildrenList(ISCAxiomSet.ELEMENT_TYPE);
		
		assert axiomSetList.size() <= 1;
		
		if(axiomSetList.size()==0)
			return new IAxiom[0];
		
		ISCAxiomSet axiomSet = (ISCAxiomSet) axiomSetList.get(0);
		return axiomSet.getAxioms();
	}

	@Override
	public ISCEvent[] getEvents() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCEvent.ELEMENT_TYPE);
		SCEvent[] events = new SCEvent[list.size()];
		list.toArray(events);
		return events; 
	}
	
	public IInvariant[] getOldInvariants() throws RodinDBException {
		ArrayList<IRodinElement> invariantSetList = getFilteredChildrenList(ISCInvariantSet.ELEMENT_TYPE);
		
		assert invariantSetList.size() <= 1;
		
		if(invariantSetList.size()==0)
			return new IInvariant[0];
		
		ISCInvariantSet invariantSet = (ISCInvariantSet) invariantSetList.get(0);
		return invariantSet.getInvariants();
	}

	public ITheorem[] getOldTheorems() throws RodinDBException {
		ArrayList<IRodinElement> theoremSetList = getFilteredChildrenList(ISCTheoremSet.ELEMENT_TYPE);
		
		assert theoremSetList.size() <= 1;
		
		if(theoremSetList.size()==0)
			return new ITheorem[0];
		
		ISCTheoremSet theoremSet = (ISCTheoremSet) theoremSetList.get(0);
		return theoremSet.getTheorems();
	}

	public IMachine getMachine() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IMachine) project.getRodinFile(uName);
	}

}
