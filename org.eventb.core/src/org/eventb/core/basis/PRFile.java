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
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;

/**
 * @author Farhad Mehta
 *
 */
public class PRFile extends RodinFile implements IPRFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PRFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	
	public IPOPredicateSet getPredicateSet(String name) throws RodinDBException {
		InternalElement element = getInternalElement(POPredicateSet.ELEMENT_TYPE, name);
		if(element.exists())
			return (POPredicateSet) element;
		else
			return null;
	}
	
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(POIdentifier.ELEMENT_TYPE);
		POIdentifier[] identifiers = new POIdentifier[list.size()];
		list.toArray(identifiers);
		return identifiers;
	}

	public IPRSequent[] getSequents() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(PRSequent.ELEMENT_TYPE);
		PRSequent[] sequents = new PRSequent[list.size()];
		list.toArray(sequents);
		return sequents;
	}

}
