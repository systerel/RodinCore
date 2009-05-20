/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextRoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 */
public class ContextRoot extends EventBRoot implements IContextRoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ContextRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IContextRoot> getElementType() {
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
	
	@Deprecated
	public org.eventb.core.ITheorem getTheorem(String elementName) {
		return getInternalElement(org.eventb.core.ITheorem.ELEMENT_TYPE, elementName);
	}

	@Deprecated
	public org.eventb.core.ITheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(org.eventb.core.ITheorem.ELEMENT_TYPE); 
	}

	public IExtendsContext getExtendsClause(String elementName) {
		return getInternalElement(IExtendsContext.ELEMENT_TYPE, elementName);
	}

	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		return getChildrenOfType(IExtendsContext.ELEMENT_TYPE); 
	}

}
