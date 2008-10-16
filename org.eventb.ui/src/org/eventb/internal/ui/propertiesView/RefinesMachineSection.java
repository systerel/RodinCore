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
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesMachineAbstractMachineNameAttributeFactory;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineSection extends CComboSection {

	@Override
	String getLabel() {
		return "Ref. Mch.";
	}

	@Override
	String getText() throws RodinDBException {
		IRefinesMachine rElement = (IRefinesMachine) element;
		return rElement.getAbstractMachineName();
	}

	@Override
	void setData() {
		final IRodinProject project = element.getRodinProject();
		final IMachineRoot[] machines;
		try {
			machines = UIUtils.getMachineRootChildren(project);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the machines of " + project);
			return;
		}
		for (IMachineRoot machine : machines) {
			final String bareName = machine.getComponentName();
			comboWidget.add(bareName);
		}
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new RefinesMachineAbstractMachineNameAttributeFactory(), text,
				monitor);
	}

}
