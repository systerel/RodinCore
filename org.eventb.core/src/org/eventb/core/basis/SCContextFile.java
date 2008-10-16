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
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IFileElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B statically checked contexts as an extension of the
 * Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IContextFile</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
@Deprecated
public class SCContextFile extends EventBFile implements ISCContextFile {

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

	public ISCCarrierSet[] getSCCarrierSets() 
	throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCCarrierSets();
	}
	
	public ISCConstant[] getSCConstants() throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCConstants();
	}

	public ISCAxiom[] getSCAxioms() throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCAxioms();
	}

	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCTheorems();
	}

	public ISCInternalContext[] getAbstractSCContexts() throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getAbstractSCContexts();
	}
	
	public ISCExtendsContext getSCExtendsClause(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCExtendsClause(elementName);
	}

	public ISCExtendsContext[] getSCExtendsClauses() throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCExtendsClauses();
	}

	public ISCInternalContext getSCInternalContext(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCInternalContext(elementName);
	}

	public ISCAxiom getSCAxiom(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCAxiom(elementName);
	}

	public ISCCarrierSet getSCCarrierSet(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCCarrierSet(elementName);
	}

	public ISCConstant getSCConstant(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCConstant(elementName);
	}

	public ISCTheorem getSCTheorem(String elementName) {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getSCTheorem(elementName);
	}

	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {
		final ISCContextRoot root = (ISCContextRoot) getRoot();
		return root.getTypeEnvironment(factory);
	}

}
