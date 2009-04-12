/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineAbstractMachineNameAttributeManipulation extends
		AbstractAttributeManipulation {

	private IRefinesMachine asRefinesMachine(IRodinElement element) {
		assert element instanceof IRefinesMachine;
		return (IRefinesMachine) element;
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// do nothing
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesMachine(element).getAbstractMachineName();
	}

	public void setValue(IRodinElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		asRefinesMachine(element).setAbstractMachineName(str, null);
	}

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final IRefinesMachine refines = asRefinesMachine(element);
		List<String> results = new ArrayList<String>();
		IMachineRoot machine = (IMachineRoot) refines.getParent();
		String machineName = machine.getRoot().getElementName();
		IMachineRoot[] machineRoots = getMachineRoots(refines);
		for (IMachineRoot root : machineRoots) {
			String bareName = root.getElementName();
			if (!machineName.equals(bareName))
				results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	private IMachineRoot[] getMachineRoots(IRodinElement refinesMachine) {
		final IRodinProject rp = refinesMachine.getRodinProject();
		try {
			return rp.getRootElementsOfType(IMachineRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing the list of machines of project "
					+ rp);
			return new IMachineRoot[0];
		}
	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asRefinesMachine(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesMachine(element).hasAbstractMachineName();
	}
}
