/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCInternalContext;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B internal SC contexts as an extension of the Rodin
 * database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCInternalContext</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public class SCInternalContext extends InternalElement implements
		ISCInternalContext {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCInternalContext(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCInternalContext> getElementType() {
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

	@Deprecated
	public org.eventb.core.ISCTheorem[] getSCTheorems() throws RodinDBException {
		return getChildrenOfType(org.eventb.core.ISCTheorem.ELEMENT_TYPE); 
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

	@Deprecated
	public org.eventb.core.ISCTheorem getSCTheorem(String elementName) {
		return getInternalElement(org.eventb.core.ISCTheorem.ELEMENT_TYPE, elementName);
	}

	public String getComponentName() {
		return getElementName();
	}

}
