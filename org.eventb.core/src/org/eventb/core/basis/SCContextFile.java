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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCTheorem;
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
	public String getElementType() {
		return ISCContextFile.ELEMENT_TYPE;
	}

	public ISCCarrierSet[] getSCCarrierSets() throws RodinDBException {
		return SCContextUtil.getSCCarrierSets(this);
	}
	
	public ISCConstant[] getSCConstants() throws RodinDBException {
		return SCContextUtil.getSCConstants(this);
	}

	public ISCAxiom[] getSCAxioms() throws RodinDBException {
		return SCContextUtil.getSCAxioms(this);
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return SCContextUtil.getSCTheorems(this);
	}

	public IContextFile getContextFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String uName = EventBPlugin.getContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IContextFile) project.getRodinFile(uName);
	}

	public ISCInternalContext[] getAbstractSCContexts() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ISCInternalContext.ELEMENT_TYPE);
		ISCInternalContext[] contexts = new ISCInternalContext[list.size()];
		list.toArray(contexts);
		return contexts; 
	}

}
