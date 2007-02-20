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
import org.eventb.core.ISCSeesContext;
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
	public IFileElementType<ISCMachineFile> getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getChildrenOfType(ISCVariable.ELEMENT_TYPE); 
	}
	
	public ISCEvent[] getSCEvents() throws RodinDBException {
		return getChildrenOfType(ISCEvent.ELEMENT_TYPE); 
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
		return getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);
	}

	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		return getChildrenOfType(ISCInvariant.ELEMENT_TYPE);
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return getChildrenOfType(ISCTheorem.ELEMENT_TYPE);
	}

	@Deprecated
	public ISCRefinesMachine getRefinesClause() throws RodinDBException {
		return getSingletonChild(
				ISCRefinesMachine.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleRefinesFailure);
	}

	@Deprecated
	public ISCVariant getSCVariant() throws RodinDBException {
		return getSingletonChild(
				ISCVariant.ELEMENT_TYPE, 
				Messages.database_SCMachineMultipleVariantFailure);
	}

	public ISCMachineFile[] getAbstractSCMachines() throws RodinDBException {
		ISCRefinesMachine[] refinesMachines = getSCRefinesClauses();
		final int length = refinesMachines.length;
		ISCMachineFile[] machineFiles = new ISCMachineFile[length];
		for (int i=0; i<length; i++) {
			machineFiles[i] = refinesMachines[i].getAbstractSCMachine();
		}
		return machineFiles;
	}

	public ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException {
		return getChildrenOfType(ISCRefinesMachine.ELEMENT_TYPE); 
	}

	public ISCSeesContext[] getSCSeesClauses() throws RodinDBException {
		return getChildrenOfType(ISCSeesContext.ELEMENT_TYPE); 
	}

	public ISCVariant[] getSCVariants() throws RodinDBException {
		return getChildrenOfType(ISCVariant.ELEMENT_TYPE); 
	}

	public ISCEvent getSCEvent(String elementName) {
		return getInternalElement(ISCEvent.ELEMENT_TYPE, elementName);
	}

	public ISCInvariant getSCInvariant(String elementName) {
		return getInternalElement(ISCInvariant.ELEMENT_TYPE, elementName);
	}

	public ISCRefinesMachine getSCRefinesClause(String elementName) {
		return getInternalElement(ISCRefinesMachine.ELEMENT_TYPE, elementName);
	}

	public ISCSeesContext getSCSeesClause(String elementName) {
		return getInternalElement(ISCSeesContext.ELEMENT_TYPE, elementName);
	}

	public ISCTheorem getSCTheorem(String elementName) {
		return getInternalElement(ISCTheorem.ELEMENT_TYPE, elementName);
	}

	public ISCVariable getSCVariable(String elementName) {
		return getInternalElement(ISCVariable.ELEMENT_TYPE, elementName);
	}

	public ISCVariant getSCVariant(String elementName) {
		return getInternalElement(ISCVariant.ELEMENT_TYPE, elementName);
	}

	public ISCInternalContext getSCSeenContext(String elementName) {
		return getInternalElement(ISCInternalContext.ELEMENT_TYPE, elementName);
	}

}
