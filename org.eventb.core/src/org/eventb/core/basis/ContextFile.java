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
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IPOFile;
import org.eventb.core.IPSFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ITheorem;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinFile;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * file element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IContextFile</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class ContextFile extends RodinFile implements IContextFile {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ContextFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ICarrierSet getCarrierSet(String elementName) {
		return (ICarrierSet) getInternalElement(ICarrierSet.ELEMENT_TYPE,
				elementName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getCarrierSets()
	 */
	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ICarrierSet.ELEMENT_TYPE);
		return (ICarrierSet[]) elements; 
	}
	
	public IConstant getConstant(String elementName) {
		return (IConstant) getInternalElement(IConstant.ELEMENT_TYPE, elementName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getConstants()
	 */
	public IConstant[] getConstants() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IConstant.ELEMENT_TYPE);
		return (IConstant[]) elements; 
	}
	
	public IAxiom getAxiom(String elementName) {
		return (IAxiom) getInternalElement(IAxiom.ELEMENT_TYPE, elementName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getAxioms()
	 */
	public IAxiom[] getAxioms() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IAxiom.ELEMENT_TYPE);
		return (IAxiom[]) elements; 
	}
	
	public ITheorem getTheorem(String elementName) {
		return (ITheorem) getInternalElement(ITheorem.ELEMENT_TYPE, elementName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getTheorems()
	 */
	public ITheorem[] getTheorems() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(ITheorem.ELEMENT_TYPE);
		return (Theorem[]) elements; 
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getSCContext()
	 */
	public ISCContextFile getSCContextFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String scName = EventBPlugin.getSCContextFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (ISCContextFile) project.getRodinFile(scName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getPOFile()
	 */
	public IPOFile getPOFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String poName = EventBPlugin.getPOFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPOFile) project.getRodinFile(poName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getPRFile()
	 */
	public IPSFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPSFile) project.getRodinFile(prName);
	}

	public IExtendsContext getExtendsClause(String elementName) {
		return (IExtendsContext) getInternalElement(IExtendsContext.ELEMENT_TYPE, elementName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getExtendsClauses()
	 */
	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		IRodinElement[] elements = getChildrenOfType(IExtendsContext.ELEMENT_TYPE);
		return (IExtendsContext[]) elements; 
	}

}
