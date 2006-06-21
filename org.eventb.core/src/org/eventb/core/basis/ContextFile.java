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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IPOFile;
import org.eventb.core.IPRFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ITheorem;
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
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getCarrierSets()
	 */
	public CarrierSet[] getCarrierSets() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ICarrierSet.ELEMENT_TYPE);
		CarrierSet[] carrierSets = new CarrierSet[list.size()];
		list.toArray(carrierSets);
		return carrierSets; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getConstants()
	 */
	public Constant[] getConstants() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IConstant.ELEMENT_TYPE);
		Constant[] constants = new Constant[list.size()];
		list.toArray(constants);
		return constants; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getAxioms()
	 */
	public Axiom[] getAxioms() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IAxiom.ELEMENT_TYPE);
		Axiom[] axioms = new Axiom[list.size()];
		list.toArray(axioms);
		return axioms; 
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getTheorems()
	 */
	public Theorem[] getTheorems() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(ITheorem.ELEMENT_TYPE);
		Theorem[] theorems = new Theorem[list.size()];
		list.toArray(theorems);
		return theorems;
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
	public IPRFile getPRFile() {
		final String bareName = EventBPlugin.getComponentName(getElementName());
		final String prName = EventBPlugin.getPRFileName(bareName);
		final IRodinProject project = (IRodinProject) getParent();
		return (IPRFile) project.getRodinFile(prName);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IContextFile#getExtendsClauses()
	 */
	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IExtendsContext.ELEMENT_TYPE);
		IExtendsContext[] contexts = new IExtendsContext[list.size()];
		list.toArray(contexts);
		return contexts; 
	}

}
