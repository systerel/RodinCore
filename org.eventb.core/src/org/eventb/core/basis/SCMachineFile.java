/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.internal.core.Messages;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B statically checked machines as an extension of the
 * Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 */
public class SCMachineFile extends EventBFile implements ISCMachineFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCMachineFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCVariable[] getSCVariables() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCVariable.ELEMENT_TYPE);
		return (ISCVariable[]) elements; 
	}
	
	public ISCEvent[] getSCEvents() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCEvent.ELEMENT_TYPE);
		return (ISCEvent[]) elements; 
	}
	
	@Deprecated
	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		ISCRefinesMachine machine = getRefinesClause();
		if (machine == null)
			return null;
		else
			return machine.getAbstractSCMachine();
	}

	public ISCInternalContext[] getSCSeenContexts() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);
		return (ISCInternalContext[]) elements;
	}

	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCInvariant.ELEMENT_TYPE);
		return (ISCInvariant[]) elements;
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCTheorem.ELEMENT_TYPE);
		return (ISCTheorem[]) elements;
	}

	@Deprecated
	public ISCRefinesMachine getRefinesClause() throws RodinDBException {
		return (ISCRefinesMachine) getSingletonChild(
				ISCRefinesMachine.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleRefinesFailure);
	}

	@Deprecated
	public ISCVariant getSCVariant() throws RodinDBException {
		return (ISCVariant) getSingletonChild(
				ISCVariant.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleVariantFailure);
	}

	public ISCMachineFile[] getAbstractSCMachines() throws RodinDBException {
		ISCRefinesMachine[] refinesMachines = getSCRefinesClauses();
		ISCMachineFile[] machineFiles = new ISCMachineFile[refinesMachines.length];
		for (int i=0; i<refinesMachines.length; i++)
			machineFiles[i] = 
				refinesMachines[i].getAbstractSCMachine();
		return machineFiles;
	}

	public ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCRefinesMachine.ELEMENT_TYPE);
		return (ISCRefinesMachine[]) elements; 
	}

	public ISCVariant[] getSCVariants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ISCVariant.ELEMENT_TYPE);
		return (ISCVariant[]) elements; 
	}

	public ISCEvent getSCEvent(String elementName) {
		return (ISCEvent) getInternalElement(ISCEvent.ELEMENT_TYPE, elementName);
	}

	public ISCInvariant getSCInvariant(String elementName) {
		return (ISCInvariant) getInternalElement(ISCInvariant.ELEMENT_TYPE, elementName);
	}

	public ISCRefinesMachine getSCRefinesClause(String elementName) {
		return (ISCRefinesMachine) getInternalElement(ISCRefinesMachine.ELEMENT_TYPE, elementName);
	}

	public ISCTheorem getSCTheorem(String elementName) {
		return (ISCTheorem) getInternalElement(ISCTheorem.ELEMENT_TYPE, elementName);
	}

	public ISCVariable getSCVariable(String elementName) {
		return (ISCVariable) getInternalElement(ISCVariable.ELEMENT_TYPE, elementName);
	}

	public ISCVariant getSCVariant(String elementName) {
		return (ISCVariant) getInternalElement(ISCVariant.ELEMENT_TYPE, elementName);
	}

	public ISCInternalContext getSCSeenContext(String elementName) {
		return (ISCInternalContext) getInternalElement(ISCInternalContext.ELEMENT_TYPE, elementName);
	}

}
