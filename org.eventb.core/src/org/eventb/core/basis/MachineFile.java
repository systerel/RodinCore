/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) machines as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class MachineFile extends EventBFile implements IMachineFile {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public MachineFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IVariable[] getVariables() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IVariable.ELEMENT_TYPE);
		return (IVariable[]) elements; 
	}
	
	public ITheorem[] getTheorems() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ITheorem.ELEMENT_TYPE);
		return (ITheorem[]) elements; 
	}
	
	public IInvariant[] getInvariants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IInvariant.ELEMENT_TYPE);
		return (IInvariant[]) elements; 
	}
	
	public IEvent[] getEvents() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IEvent.ELEMENT_TYPE);
		return (IEvent[]) elements; 
	}
	
	public ISeesContext[] getSeesClauses() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISeesContext.ELEMENT_TYPE);
		return (ISeesContext[]) elements; 
	}
	
	@Deprecated
	public IRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException {
		return (IRefinesMachine) getSingletonChild(
				IRefinesMachine.ELEMENT_TYPE, Messages.database_MachineMultipleRefinesFailure);
	}

	@Deprecated
	public IRefinesMachine getRefinesClause() throws RodinDBException {
		return getRefinesClause( (IProgressMonitor) null);
	}

	@Deprecated
	public IVariant getVariant() throws RodinDBException {
		return (IVariant) getSingletonChild(
				IVariant.ELEMENT_TYPE, Messages.database_MachineMultipleVariantFailure);
	}

	public IRefinesMachine[] getRefinesClauses() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
		return (IRefinesMachine[]) elements; 
	}

	public IVariant[] getVariants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IVariant.ELEMENT_TYPE);
		return (IVariant[]) elements; 
	}

	public IEvent getEvent(String elementName) {
		return (IEvent) getInternalElement(IEvent.ELEMENT_TYPE, elementName);
	}

	public IInvariant getInvariant(String elementName) {
		return (IInvariant) getInternalElement(IInvariant.ELEMENT_TYPE, elementName);
	}

	public IRefinesMachine getRefinesClause(String elementName) {
		return (IRefinesMachine) getInternalElement(IRefinesMachine.ELEMENT_TYPE, elementName);
	}

	public ISeesContext getSeesClause(String elementName) {
		return (ISeesContext) getInternalElement(ISeesContext.ELEMENT_TYPE, elementName);
	}

	public ITheorem getTheorem(String elementName) {
		return (ITheorem) getInternalElement(ITheorem.ELEMENT_TYPE, elementName);
	}

	public IVariable getVariable(String elementName) {
		return (IVariable) getInternalElement(IVariable.ELEMENT_TYPE, elementName);
	}

	public IVariant getVariant(String elementName) {
		return (IVariant) getInternalElement(IVariant.ELEMENT_TYPE, elementName);
	}

}
