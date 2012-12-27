/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.internal.core.basis.SCContextUtil;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin
 * database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>ISCMachineRoot</code>.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class SCMachineRoot extends EventBRoot implements ISCMachineRoot {

	/**
	 * Constructor used by the Rodin database.
	 */
	public SCMachineRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<ISCMachineRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public ISCVariable[] getSCVariables() throws RodinDBException {
		return getChildrenOfType(ISCVariable.ELEMENT_TYPE);
	}

	@Override
	public ISCEvent[] getSCEvents() throws RodinDBException {
		return getChildrenOfType(ISCEvent.ELEMENT_TYPE);
	}

	@Override
	public ISCInternalContext[] getSCSeenContexts() throws RodinDBException {
		return getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);
	}

	@Override
	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		return getChildrenOfType(ISCInvariant.ELEMENT_TYPE);
	}

	@Override
	public IRodinFile[] getAbstractSCMachines() throws RodinDBException {
		ISCRefinesMachine[] refinesMachines = getSCRefinesClauses();
		final int length = refinesMachines.length;
		IRodinFile[] machineFiles = new IRodinFile[length];
		for (int i = 0; i < length; i++) {
			machineFiles[i] = refinesMachines[i].getAbstractSCMachine();
		}
		return machineFiles;
	}

	@Override
	public ISCRefinesMachine[] getSCRefinesClauses() throws RodinDBException {
		return getChildrenOfType(ISCRefinesMachine.ELEMENT_TYPE);
	}

	@Override
	public ISCSeesContext[] getSCSeesClauses() throws RodinDBException {
		return getChildrenOfType(ISCSeesContext.ELEMENT_TYPE);
	}

	@Override
	public ISCVariant[] getSCVariants() throws RodinDBException {
		return getChildrenOfType(ISCVariant.ELEMENT_TYPE);
	}

	@Override
	public ISCEvent getSCEvent(String elementName) {
		return getInternalElement(ISCEvent.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCInvariant getSCInvariant(String elementName) {
		return getInternalElement(ISCInvariant.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCRefinesMachine getSCRefinesClause(String elementName) {
		return getInternalElement(ISCRefinesMachine.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCSeesContext getSCSeesClause(String elementName) {
		return getInternalElement(ISCSeesContext.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCVariable getSCVariable(String elementName) {
		return getInternalElement(ISCVariable.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCVariant getSCVariant(String elementName) {
		return getInternalElement(ISCVariant.ELEMENT_TYPE, elementName);
	}

	@Override
	public ISCInternalContext getSCSeenContext(String elementName) {
		return getInternalElement(ISCInternalContext.ELEMENT_TYPE, elementName);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public ITypeEnvironmentBuilder getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException {

		ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
		for (ISCInternalContext ictx : getSCSeenContexts()) {
			SCContextUtil.augmentTypeEnvironment(ictx, typenv, factory);
		}
		for (ISCVariable vrb : getSCVariables()) {
			typenv.add(vrb.getIdentifier(factory));
		}
		return typenv;
	}

}
