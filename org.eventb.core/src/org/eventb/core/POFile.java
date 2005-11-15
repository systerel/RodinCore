/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.InternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinFile;

/**
 * @author halstefa
 *
 */
public class POFile extends RodinFile {

	public static final String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".poFile";
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	public POPredicateSet getPredicateSet(String name) throws RodinDBException {
		InternalElement element = getInternalElement(POPredicateSet.ELEMENT_TYPE, name);
		if(element.exists())
			return (POPredicateSet) element;
		else
			return null;
	}
	
	public POIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(POIdentifier.ELEMENT_TYPE);
		POIdentifier[] identifiers = new POIdentifier[list.size()];
		list.toArray(identifiers);
		return identifiers;
	}

	public POSequent[] getSequents() throws RodinDBException {
		ArrayList<IRodinElement> list = getChildrenOfType(POSequent.ELEMENT_TYPE);
		POSequent[] sequents = new POSequent[list.size()];
		list.toArray(sequents);
		return sequents;
	}

}
