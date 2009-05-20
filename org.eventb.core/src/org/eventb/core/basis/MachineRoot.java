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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IMachineRoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class MachineRoot extends EventBRoot implements IMachineRoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public MachineRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IMachineRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	public IVariable[] getVariables() throws RodinDBException {
		return getChildrenOfType(IVariable.ELEMENT_TYPE);
	}
	
	@Deprecated
	public org.eventb.core.ITheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(org.eventb.core.ITheorem.ELEMENT_TYPE);
	}
	
	public IInvariant[] getInvariants() throws RodinDBException {
		return getChildrenOfType(IInvariant.ELEMENT_TYPE);
	}
	
	public IEvent[] getEvents() throws RodinDBException {
		return getChildrenOfType(IEvent.ELEMENT_TYPE); 
	}
	
	public ISeesContext[] getSeesClauses() throws RodinDBException {
		return getChildrenOfType(ISeesContext.ELEMENT_TYPE); 
	}
	
	@Deprecated
	public IRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException {
		return getSingletonChild(IRefinesMachine.ELEMENT_TYPE,
				Messages.database_MachineMultipleRefinesFailure);
	}

	@Deprecated
	public IRefinesMachine getRefinesClause() throws RodinDBException {
		return getRefinesClause( (IProgressMonitor) null);
	}

	@Deprecated
	public IVariant getVariant() throws RodinDBException {
		return getSingletonChild(IVariant.ELEMENT_TYPE,
				Messages.database_MachineMultipleVariantFailure);
	}

	public IRefinesMachine[] getRefinesClauses() throws RodinDBException {
		return getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
	}

	public IVariant[] getVariants() throws RodinDBException {
		return getChildrenOfType(IVariant.ELEMENT_TYPE);
	}

	public IEvent getEvent(String elementName) {
		return getInternalElement(IEvent.ELEMENT_TYPE, elementName);
	}

	public IInvariant getInvariant(String elementName) {
		return getInternalElement(IInvariant.ELEMENT_TYPE, elementName);
	}

	public IRefinesMachine getRefinesClause(String elementName) {
		return getInternalElement(IRefinesMachine.ELEMENT_TYPE, elementName);
	}

	public ISeesContext getSeesClause(String elementName) {
		return getInternalElement(ISeesContext.ELEMENT_TYPE, elementName);
	}

	@Deprecated
	public org.eventb.core.ITheorem getTheorem(String elementName) {
		return getInternalElement(org.eventb.core.ITheorem.ELEMENT_TYPE, elementName);
	}

	public IVariable getVariable(String elementName) {
		return getInternalElement(IVariable.ELEMENT_TYPE, elementName);
	}

	public IVariant getVariant(String elementName) {
		return getInternalElement(IVariant.ELEMENT_TYPE, elementName);
	}

}
