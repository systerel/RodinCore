/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B PO files as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPOFile</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 */
public class POFile extends EventBFile implements IPOFile {

	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<IPOFile> getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicateSet getPredicateSet(String elementName) {
		return getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	public IPOSequent[] getSequents() throws RodinDBException {
		return getChildrenOfType(IPOSequent.ELEMENT_TYPE); 
	}

	public IPOPredicateSet[] getPredicateSets() throws RodinDBException {
		return getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
	}

	public IPOSequent getSequent(String elementName) {
		return getInternalElement(IPOSequent.ELEMENT_TYPE, elementName);
	}

}
