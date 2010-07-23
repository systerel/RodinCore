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
 * @since 1.0
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

	@Override
	public IVariable[] getVariables() throws RodinDBException {
		return getChildrenOfType(IVariable.ELEMENT_TYPE);
	}
	
	@Override
	@Deprecated
	public org.eventb.core.ITheorem[] getTheorems() throws RodinDBException {
		return getChildrenOfType(org.eventb.core.ITheorem.ELEMENT_TYPE);
	}
	
	@Override
	public IInvariant[] getInvariants() throws RodinDBException {
		return getChildrenOfType(IInvariant.ELEMENT_TYPE);
	}
	
	@Override
	public IEvent[] getEvents() throws RodinDBException {
		return getChildrenOfType(IEvent.ELEMENT_TYPE); 
	}
	
	@Override
	public ISeesContext[] getSeesClauses() throws RodinDBException {
		return getChildrenOfType(ISeesContext.ELEMENT_TYPE); 
	}
	
	@Deprecated
	public IRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException {
		return getSingletonChild(IRefinesMachine.ELEMENT_TYPE,
				Messages.database_MachineMultipleRefinesFailure);
	}

	@Override
	@Deprecated
	public IRefinesMachine getRefinesClause() throws RodinDBException {
		return getRefinesClause( (IProgressMonitor) null);
	}

	@Override
	@Deprecated
	public IVariant getVariant() throws RodinDBException {
		return getSingletonChild(IVariant.ELEMENT_TYPE,
				Messages.database_MachineMultipleVariantFailure);
	}

	@Override
	public IRefinesMachine[] getRefinesClauses() throws RodinDBException {
		return getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
	}

	@Override
	public IVariant[] getVariants() throws RodinDBException {
		return getChildrenOfType(IVariant.ELEMENT_TYPE);
	}

	@Override
	public IEvent getEvent(String elementName) {
		return getInternalElement(IEvent.ELEMENT_TYPE, elementName);
	}

	@Override
	public IInvariant getInvariant(String elementName) {
		return getInternalElement(IInvariant.ELEMENT_TYPE, elementName);
	}

	@Override
	public IRefinesMachine getRefinesClause(String elementName) {
		return getInternalElement(IRefinesMachine.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISeesContext getSeesClause(String elementName) {
		return getInternalElement(ISeesContext.ELEMENT_TYPE, elementName);
	}

	@Override
	@Deprecated
	public org.eventb.core.ITheorem getTheorem(String elementName) {
		return getInternalElement(org.eventb.core.ITheorem.ELEMENT_TYPE, elementName);
	}

	@Override
	public IVariable getVariable(String elementName) {
		return getInternalElement(IVariable.ELEMENT_TYPE, elementName);
	}

	@Override
	public IVariant getVariant(String elementName) {
		return getInternalElement(IVariant.ELEMENT_TYPE, elementName);
	}

}
