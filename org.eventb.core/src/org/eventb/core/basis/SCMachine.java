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
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IInvariant;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInvariantSet;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class SCMachine extends Machine implements ISCMachine {

	/**
	 * @param file
	 * @param parent
	 */
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

	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		CarrierSet[] carrierSets = new CarrierSet[list.size()];
		list.toArray(carrierSets);
		return carrierSets; 
	}
	
	public IConstant[] getConstants() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(IConstant.ELEMENT_TYPE);
		Constant[] constants = new Constant[list.size()];
		list.toArray(constants);
		return constants; 
	}
	
	public ISCAxiomSet[] getAxiomSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(ISCAxiomSet.ELEMENT_TYPE);
		ISCAxiomSet[] axiomSets = new ISCAxiomSet[list.size()];
		list.toArray(axiomSets);
		return axiomSets; 
	}
	
	public ISCTheoremSet[] getTheoremSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(ISCTheoremSet.ELEMENT_TYPE);
		ISCTheoremSet[] theoremSets = new ISCTheoremSet[list.size()];
		list.toArray(theoremSets);
		return theoremSets; 
	}
	
	public IAxiom[] getOldAxioms() throws RodinDBException {
		ArrayList<IRodinElement> axiomSetList = getChildrenOfType(ISCAxiomSet.ELEMENT_TYPE);
		
		assert axiomSetList.size() <= 1;
		
		if(axiomSetList.size()==0)
			return new IAxiom[0];
		
		ISCAxiomSet axiomSet = (ISCAxiomSet) axiomSetList.get(0);
		return axiomSet.getAxioms();
	}

	@Override
	public ISCEvent[] getEvents() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(ISCEvent.ELEMENT_TYPE);
		SCEvent[] events = new SCEvent[list.size()];
		list.toArray(events);
		return events; 
	}
	
	public IInvariant[] getOldInvariants() throws RodinDBException {
		ArrayList<IRodinElement> axiomSetList = getChildrenOfType(ISCAxiomSet.ELEMENT_TYPE);
		
		assert axiomSetList.size() <= 1;
		
		if(axiomSetList.size()==0)
			return new IInvariant[0];
		
		ISCInvariantSet invariantSet = (ISCInvariantSet) axiomSetList.get(0);
		return invariantSet.getInvariants();
	}

	public ITheorem[] getOldTheorems() throws RodinDBException {
		ArrayList<IRodinElement> theoremSetList = getChildrenOfType(ISCTheoremSet.ELEMENT_TYPE);
		
		assert theoremSetList.size() <= 1;
		
		if(theoremSetList.size()==0)
			return new ITheorem[0];
		
		ISCTheoremSet theoremSet = (ISCTheoremSet) theoremSetList.get(0);
		return theoremSet.getTheorems();
	}
	
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> identifierList = getChildrenOfType(IPOIdentifier.ELEMENT_TYPE);
		
		IPOIdentifier[] identifiers = new IPOIdentifier[identifierList.size()];
		identifierList.toArray(identifiers);
		return identifiers; 
	}

}
