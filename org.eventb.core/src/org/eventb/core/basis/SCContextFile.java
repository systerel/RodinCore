/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
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
import org.eventb.core.IContextFile;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCTheorem;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B SC context files as an extension of the Rodin
 * database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>ISCContextFile</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public class SCContextFile extends RodinFile implements ISCContextFile {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCContextFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ISCCarrierSet[] getSCCarrierSets(IProgressMonitor monitor) 
	throws RodinDBException {
		return SCContextUtil.getSCCarrierSets(this, monitor);
	}
	
	@Deprecated
	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		return getSCCarrierSets(null);
	}
	
	public ISCConstant[] getSCConstants(IProgressMonitor monitor) throws RodinDBException {
		return SCContextUtil.getSCConstants(this, monitor);
	}

	@Deprecated
	public ISCConstant[] getSCConstants() throws RodinDBException {
		return getSCConstants(null);
	}

	public ISCAxiom[] getSCAxioms(IProgressMonitor monitor) throws RodinDBException {
		return SCContextUtil.getSCAxioms(this, monitor);
	}

	@Deprecated
	public ISCAxiom[] getSCAxioms() throws RodinDBException {
		return getSCAxioms(null);
	}

	public ISCTheorem[] getSCTheorems(IProgressMonitor monitor) throws RodinDBException {
		return SCContextUtil.getSCTheorems(this, monitor);
	}

	@Deprecated
	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return getSCTheorems(null);
	}

	public IContextFile getContextFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IContextFile) project.getRodinFile(uName);
	}

	public ISCInternalContext[] getAbstractSCContexts(IProgressMonitor monitor) throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCInternalContext.ELEMENT_TYPE);
		ISCInternalContext[] contexts = new ISCInternalContext[list.size()];
		list.toArray(contexts);
		return contexts; 
	}
	
	@Deprecated
	public ISCInternalContext[] getAbstractSCContexts() throws RodinDBException {
		return getAbstractSCContexts(null); 
	}

}
