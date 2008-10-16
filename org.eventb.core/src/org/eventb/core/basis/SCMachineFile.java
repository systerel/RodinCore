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

import org.eclipse.core.resources.IFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
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
@Deprecated
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
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCVariables(); 
	}
	
	public ISCEvent[] getSCEvents() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCEvents(); 
	}
	
	@Deprecated
	public ISCMachineFile getAbstractSCMachine() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return (ISCMachineFile) root.getAbstractSCMachine(); 
	}

	public ISCInternalContext[] getSCSeenContexts() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCSeenContexts(); 
	}

	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCInvariants(); 
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCTheorems(); 
	}

	@Deprecated
	public ISCRefinesMachine getRefinesClause() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getRefinesClause(); 
	}

	@Deprecated
	public ISCVariant getSCVariant() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCVariant(); 
	}

	public ISCMachineFile[] getAbstractSCMachines() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return (ISCMachineFile[]) root.getAbstractSCMachines(); 
	}

	public ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCRefinesClauses(); 
	}

	public ISCSeesContext[] getSCSeesClauses() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCSeesClauses(); 
	}

	public ISCVariant[] getSCVariants() throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCVariants(); 
	}

	public ISCEvent getSCEvent(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCEvent(elementName); 
	}

	public ISCInvariant getSCInvariant(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCInvariant(elementName);
	}

	public ISCRefinesMachine getSCRefinesClause(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCRefinesClause(elementName); 
	}

	public ISCSeesContext getSCSeesClause(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCSeesClause(elementName); 
	}

	public ISCTheorem getSCTheorem(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCTheorem(elementName); 
	}

	public ISCVariable getSCVariable(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCVariable(elementName); 
	}

	public ISCVariant getSCVariant(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCVariant(elementName); 
	}

	public ISCInternalContext getSCSeenContext(String elementName) {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getSCSeenContext(elementName); 
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		ISCMachineRoot root = (ISCMachineRoot) getRoot();
		return root.getTypeEnvironment(factory); 
	}

}
