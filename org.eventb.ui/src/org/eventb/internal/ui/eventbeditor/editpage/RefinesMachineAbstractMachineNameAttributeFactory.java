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
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineAbstractMachineNameAttributeFactory implements
		IAttributeFactory<IRefinesMachine> {

	public void setDefaultValue(IRefinesMachine element,
			IProgressMonitor monitor) throws RodinDBException {
		String name = "abstract_machine";
		element.setAbstractMachineName(name, new NullProgressMonitor());
	}

	public String getValue(IRefinesMachine element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getAbstractMachineName();
	}

	public void setValue(IRefinesMachine element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		element.setAbstractMachineName(str, new NullProgressMonitor());
	}

	public String[] getPossibleValues(IRefinesMachine element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		IMachineRoot machine = (IMachineRoot) element.getParent();
		String machineName = machine.getRodinFile().getBareName();
		IMachineRoot[] machineRoots = getMachineRoots(element);
		for (IMachineRoot root : machineRoots) {
			String bareName = root.getElementName();
			if (!machineName.equals(bareName))
				results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	private IMachineRoot[] getMachineRoots(IRefinesMachine refinesMachine) {
		final IRodinProject rp = refinesMachine.getRodinProject();
		try {
			return rp.getRootElementsOfType(IMachineRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing the list of machines of project "
					+ rp);
			return new IMachineRoot[0];
		}
	}

	public void removeAttribute(IRefinesMachine element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IRefinesMachine element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasAbstractMachineName();
	}
}
