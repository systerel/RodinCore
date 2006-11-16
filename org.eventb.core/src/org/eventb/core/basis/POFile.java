/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B PO file as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOFile</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POFile extends RodinFile implements IPOFile {

	public POFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.RodinElement#getElementType()
	 */
	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	@Deprecated
	public IPOPredicateSet getPredicateSet(String name, IProgressMonitor monitor) throws RodinDBException {
		InternalElement element = getInternalElement(POPredicateSet.ELEMENT_TYPE, name);
		if(element.exists())
			return (POPredicateSet) element;
		else
			return null;
	}
	
	public IPOPredicateSet getPredicateSet(String elementName) throws RodinDBException {
		return (IPOPredicateSet) getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}
	
	@Deprecated
	public IPOIdentifier[] getIdentifiers() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(POIdentifier.ELEMENT_TYPE);
		POIdentifier[] identifiers = new POIdentifier[list.size()];
		list.toArray(identifiers);
		return identifiers;
	}

	public IPOSequent[] getSequents(IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		return (POSequent[]) elements; 
	}

	@Deprecated
	public IPOSequent[] getSequents() throws RodinDBException {
		return getSequents(null);
	}

	public ISCContextFile getSCContext() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getSCContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (ISCContextFile) project.getRodinFile(scName);
	}

	public ISCMachineFile getSCMachine() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getSCMachineFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (ISCMachineFile) project.getRodinFile(scName);
	}

	public IPRFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPRFile) project.getRodinFile(prName);
	}
	
	public IPSFile getPSFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String psName = EventBPlugin.getPSFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPSFile) project.getRodinFile(psName);
	}

	public IPOPredicateSet[] getPredicateSets(IProgressMonitor monitor) throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
		return (IPOPredicateSet[]) elements;
	}

	@Deprecated
	public IPOSequent getSequent(String name, IProgressMonitor monitor) throws RodinDBException {
		IInternalElement element = getInternalElement(IPOSequent.ELEMENT_TYPE, name);
		if(element.exists())
			return (POSequent) element;
		else
			return null;
	}

	public IPOSequent getSequent(String elementName) throws RodinDBException {
		return (IPOSequent) getInternalElement(IPOSequent.ELEMENT_TYPE, elementName);
	}

}
