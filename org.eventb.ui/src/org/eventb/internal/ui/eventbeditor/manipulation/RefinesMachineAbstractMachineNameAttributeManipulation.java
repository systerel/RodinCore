/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
 *     Systerel - filtered getPossibleValues
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
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

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// do nothing
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesMachine(element).getAbstractMachineName();
	}

	@Override
	public void setValue(IRodinElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		IRefinesMachine ref = asRefinesMachine(element);
		ref.setAbstractMachineName(str, null);
		IMachineRoot root = (IMachineRoot) ref.getRoot();
		IMachineRoot abstractMachineRoot = ref.getAbstractMachineRoot();
		if (abstractMachineRoot.exists()) {
			try {
				root.setConfiguration(abstractMachineRoot.getConfiguration(),
						null);
			} catch (RodinDBException e) {
				UIUtils.showUnexpectedError(e,
						"Impossible to update the configuration for machine "
								+ root.getComponentName() + "!");
			}
		}
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		final IRefinesMachine refines = asRefinesMachine(element);
		final List<String> results = new ArrayList<String>();
		final IMachineRoot machine = (IMachineRoot) refines.getRoot();
		final IMachineRoot[] machineRoots = getMachineRoots(refines);
		for (IMachineRoot root : machineRoots) {
			if (!machine.equals(root) && !isAbstractionOf(machine, root))
				results.add(root.getElementName());
		}
		return results.toArray(new String[results.size()]);
	}

	/**
	 * Returns true if abstractMachine is a (direct or indirect) abstraction of
	 * root.
	 */
	private boolean isAbstractionOf(IMachineRoot abstractMachine,
			IMachineRoot root) {
		try {
			final IMachineRoot rootAbs = EventBUtils.getAbstractMachine(root);
			if (rootAbs == null) {
				return false;
			} else if (rootAbs.equals(abstractMachine)) {
				return true;
			} else {
				return isAbstractionOf(abstractMachine, rootAbs);
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
			return false;
		}
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

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asRefinesMachine(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesMachine(element).hasAbstractMachineName();
	}
}
