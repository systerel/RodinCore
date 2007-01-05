/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class ContextFile extends EventBFile implements IContextFile {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ContextFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<IContextFile> getElementType() {
		return ELEMENT_TYPE;
	}

	public ICarrierSet getCarrierSet(String elementName) {
		return getInternalElement(ICarrierSet.ELEMENT_TYPE, elementName);
	}

	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		return getChildrenOfType(ICarrierSet.ELEMENT_TYPE); 
	}
	
	public IConstant getConstant(String elementName) {
		return getInternalElement(IConstant.ELEMENT_TYPE, elementName);
	}

	public IConstant[] getConstants() throws RodinDBException {
		return getChildrenOfType(IConstant.ELEMENT_TYPE); 
	}
	
	public IAxiom getAxiom(String elementName) {
		return getInternalElement(IAxiom.ELEMENT_TYPE, elementName);
	}

	public IAxiom[] getAxioms() throws RodinDBException {
		return getChildrenOfType(IAxiom.ELEMENT_TYPE); 
	}
	
	public ITheorem getTheorem(String elementName) {
		return getInternalElement(ITheorem.ELEMENT_TYPE, elementName);
	}

	public ITheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(ITheorem.ELEMENT_TYPE); 
	}

	public IExtendsContext getExtendsClause(String elementName) {
		return getInternalElement(IExtendsContext.ELEMENT_TYPE, elementName);
	}

	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		return getChildrenOfType(IExtendsContext.ELEMENT_TYPE); 
	}

}
