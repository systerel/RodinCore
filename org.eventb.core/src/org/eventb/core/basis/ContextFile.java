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
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ITheorem;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
@Deprecated
public class ContextFile extends EventBFile implements IContextFile {
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public ContextFile(IFile file, IRodinElement parent) {
		super(file, parent);
	}

	@Override
	public IFileElementType getElementType() {
		return ELEMENT_TYPE;
	}

	public ICarrierSet getCarrierSet(String elementName) {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getCarrierSet(elementName);
	}

	public ICarrierSet[] getCarrierSets() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getCarrierSets();
	}
	
	public IConstant getConstant(String elementName) {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getConstant(elementName);
	}

	public IConstant[] getConstants() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getConstants();
	}
	
	public IAxiom getAxiom(String elementName) {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getAxiom(elementName);
	}

	public IAxiom[] getAxioms() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getAxioms();
	}
	
	public ITheorem getTheorem(String elementName) {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getTheorem(elementName);
	}

	public ITheorem[] getTheorems() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getTheorems();
	}

	public IExtendsContext getExtendsClause(String elementName) {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getExtendsClause(elementName);
	}

	public IExtendsContext[] getExtendsClauses() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getExtendsClauses();
	}

	public void setComment(String comment, IProgressMonitor monitor)
			throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		root.setComment(comment, monitor);
	}

	public boolean hasComment() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.hasComment();
	}

	public String getComment() throws RodinDBException {
		final IContextRoot root = (IContextRoot) getRoot();
		return root.getComment();
	}

}
