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
import org.eventb.core.IContext;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ITheorem;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B SC context as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCContext</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SCContext extends Context implements ISCContext {

	public SCContext(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ISCContext.ELEMENT_TYPE;
	}

	public IAxiom[] getOldAxioms() throws RodinDBException {
		ArrayList<IRodinElement> axiomSetList = getFilteredChildrenList(ISCAxiomSet.ELEMENT_TYPE);
		
		assert axiomSetList.size() <= 1;
		
		if(axiomSetList.size()==0)
			return new IAxiom[0];
		
		ISCAxiomSet axiomSet = (ISCAxiomSet) axiomSetList.get(0);
		return axiomSet.getAxioms();
	}

	public ITheorem[] getOldTheorems() throws RodinDBException {
		ArrayList<IRodinElement> theoremSetList = getFilteredChildrenList(ISCTheoremSet.ELEMENT_TYPE);
		
		assert theoremSetList.size() <= 1;
		
		if(theoremSetList.size()==0)
			return new ITheorem[0];
		
		ISCTheoremSet theoremSet = (ISCTheoremSet) theoremSetList.get(0);
		return theoremSet.getTheorems();
	}
	
	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		ArrayList<IRodinElement> identifierList = getFilteredChildrenList(ISCCarrierSet.ELEMENT_TYPE);
		
		SCCarrierSet[] identifiers = new SCCarrierSet[identifierList.size()];
		identifierList.toArray(identifiers);
		return identifiers; 
	}
	
	public ISCConstant[] getSCConstants() throws RodinDBException {
		ArrayList<IRodinElement> identifierList = getFilteredChildrenList(ISCConstant.ELEMENT_TYPE);
		
		SCConstant[] identifiers = new SCConstant[identifierList.size()];
		identifierList.toArray(identifiers);
		return identifiers; 
	}

	public IContext getUncheckedVersion() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IContext) project.getRodinFile(uName);
	}

}
