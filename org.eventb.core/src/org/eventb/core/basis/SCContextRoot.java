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

import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>ISCContextRoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class SCContextRoot extends EventBRoot implements ISCContextRoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCContextRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCContextRoot> getElementType() {
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

	@Deprecated
	public org.eventb.core.ISCTheorem getSCTheorem(String elementName) {
		return getInternalElement(org.eventb.core.ISCTheorem.ELEMENT_TYPE, elementName);
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		
		ITypeEnvironment typenv = factory.makeTypeEnvironment();
		for (ISCInternalContext ictx: getAbstractSCContexts()) {
			SCContextUtil.augmentTypeEnvironment(ictx, typenv, factory);
		}
		SCContextUtil.augmentTypeEnvironment(this, typenv, factory);
		return typenv;
	}

}
