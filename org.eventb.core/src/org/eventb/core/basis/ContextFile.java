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
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ICarrierSet getCarrierSet(String elementName) {
		return (ICarrierSet) getInternalElement(ICarrierSet.ELEMENT_TYPE,
				elementName);
	}

	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		return (ICarrierSet[]) elements; 
	}
	
	public IConstant getConstant(String elementName) {
		return (IConstant) getInternalElement(IConstant.ELEMENT_TYPE, elementName);
	}

	public IConstant[] getConstants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IConstant.ELEMENT_TYPE);
		return (IConstant[]) elements; 
	}
	
	public IAxiom getAxiom(String elementName) {
		return (IAxiom) getInternalElement(IAxiom.ELEMENT_TYPE, elementName);
	}

	public IAxiom[] getAxioms() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IAxiom.ELEMENT_TYPE);
		return (IAxiom[]) elements; 
	}
	
	public ITheorem getTheorem(String elementName) {
		return (ITheorem) getInternalElement(ITheorem.ELEMENT_TYPE, elementName);
	}

	public ITheorem[] getTheorems() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ITheorem.ELEMENT_TYPE);
		return (Theorem[]) elements; 
	}

	public IExtendsContext getExtendsClause(String elementName) {
		return (IExtendsContext) getInternalElement(IExtendsContext.ELEMENT_TYPE, elementName);
	}

	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IExtendsContext.ELEMENT_TYPE);
		return (IExtendsContext[]) elements; 
	}

}
