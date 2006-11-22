/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eclipse.core.resources.IFile;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
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

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}
	
	public IPOPredicateSet getPredicateSet(String elementName) {
		return (IPOPredicateSet) getInternalElement(IPOPredicateSet.ELEMENT_TYPE, elementName);
	}

	public IPOSequent[] getSequents() throws RodinDBException {
		return (POSequent[]) getChildrenOfType(IPOSequent.ELEMENT_TYPE); 
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

	public IPOPredicateSet[] getPredicateSets() throws RodinDBException {
		return (IPOPredicateSet[]) getChildrenOfType(POPredicateSet.ELEMENT_TYPE);
	}

	public IPOSequent getSequent(String elementName) {
		return (IPOSequent) getInternalElement(IPOSequent.ELEMENT_TYPE, elementName);
	}

}
