/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCTheorem;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B statically checked contexts as an extension of the
 * Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class SCContextFile extends EventBFile implements ISCContextFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCContextFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType<ISCContextFile> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCCarrierSet[] getSCCarrierSets() 
	throws RodinDBException {
		return getChildrenOfType(ISCCarrierSet.ELEMENT_TYPE); 
	}
	
	public ISCConstant[] getSCConstants() throws RodinDBException {
		return getChildrenOfType(ISCConstant.ELEMENT_TYPE); 
	}

	public ISCAxiom[] getSCAxioms() throws RodinDBException {
		return getChildrenOfType(ISCAxiom.ELEMENT_TYPE); 
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return getChildrenOfType(ISCTheorem.ELEMENT_TYPE); 
	}

	public ISCInternalContext[] getAbstractSCContexts() throws RodinDBException {
		return getChildrenOfType(ISCInternalContext.ELEMENT_TYPE); 
	}
	
	public ISCExtendsContext getSCExtendsClause(String elementName) {
		return getInternalElement(ISCExtendsContext.ELEMENT_TYPE, elementName);
	}

	public ISCExtendsContext[] getSCExtendsClauses() throws RodinDBException {
		return getChildrenOfType(ISCExtendsContext.ELEMENT_TYPE); 
	}

	public ISCInternalContext getSCInternalContext(String elementName) {
		return getInternalElement(ISCInternalContext.ELEMENT_TYPE, elementName);
	}

	public ISCAxiom getSCAxiom(String elementName) {
		return getInternalElement(ISCAxiom.ELEMENT_TYPE, elementName);
	}

	public ISCCarrierSet getSCCarrierSet(String elementName) {
		return getInternalElement(ISCCarrierSet.ELEMENT_TYPE, elementName);
	}

	public ISCConstant getSCConstant(String elementName) {
		return getInternalElement(ISCConstant.ELEMENT_TYPE, elementName);
	}

	public ISCTheorem getSCTheorem(String elementName) {
		return getInternalElement(ISCTheorem.ELEMENT_TYPE, elementName);
	}

}
