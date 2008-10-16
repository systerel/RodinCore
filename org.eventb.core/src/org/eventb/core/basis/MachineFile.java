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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
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
@Deprecated
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
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getVariables();
	}
	
	public ITheorem[] getTheorems() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getTheorems();
	}
	
	public IInvariant[] getInvariants() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getInvariants();
	}
	
	public IEvent[] getEvents() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getEvents();
	}
	
	public ISeesContext[] getSeesClauses() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getSeesClauses();
	}
	
	@Deprecated
	public IRefinesMachine getRefinesClause(IProgressMonitor monitor) throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getRefinesClause();
	}

	@Deprecated
	public IRefinesMachine getRefinesClause() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getRefinesClause();
	}

	@Deprecated
	public IVariant getVariant() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getVariant();
	}

	public IRefinesMachine[] getRefinesClauses() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getRefinesClauses();
	}

	public IVariant[] getVariants() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getVariants();
	}

	public IEvent getEvent(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getEvent(elementName);
	}

	public IInvariant getInvariant(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getInvariant(elementName);
	}

	public IRefinesMachine getRefinesClause(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getRefinesClause(elementName);
	}

	public ISeesContext getSeesClause(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getSeesClause(elementName);
	}

	public ITheorem getTheorem(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getTheorem(elementName);
	}

	public IVariable getVariable(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getVariable(elementName);
	}

	public IVariant getVariant(String elementName) {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getVariant(elementName);
	}

	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		root.setComment(comment, monitor);
	}

	public boolean hasComment() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.hasComment();
	}

	public String getComment() throws RodinDBException {
		IMachineRoot root = (IMachineRoot) getRoot();
		return root.getComment();
	}
	

}
