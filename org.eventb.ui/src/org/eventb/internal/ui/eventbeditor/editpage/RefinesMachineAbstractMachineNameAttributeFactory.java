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
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class RefinesMachineAbstractMachineNameAttributeFactory implements
		IAttributeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setDefaultValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IRefinesMachine refinesEvent = (IRefinesMachine) element;
		String name = "abstract_machine";
		refinesEvent.setAbstractMachineName(name, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IRefinesMachine refinesMachine = (IRefinesMachine) element;
		return refinesMachine.getAbstractMachineName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setValue(org.rodinp.core.IAttributedElement,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setValue(IAttributedElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IRefinesMachine;
		IRefinesMachine refinesMachine = (IRefinesMachine) element;
		refinesMachine.setAbstractMachineName(str, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getPossibleValues(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		List<String> results = new ArrayList<String>();
		IRefinesMachine refinesMachine = (IRefinesMachine) element;
		IMachineRoot machine = (IMachineRoot) refinesMachine.getParent();
		String machineName = machine.getRodinFile().getBareName();
		IRodinProject rodinProject = refinesMachine.getRodinProject();
//		IRodinFile[] machineFiles = rodinProject
//				.getChildrenOfType(IMachineFile.ELEMENT_TYPE);
		IRodinFile[] machineFiles = getMachineFile(rodinProject);
		for (IRodinFile machineFile : machineFiles) {
			String bareName = machineFile.getBareName();
			if (!machineName.equals(bareName))
				results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	/**
	 * @return an IRodinFile array. Each file is a children of givenProject,
	 *         IRodinFile and the root element is IMachineRoot
	 */
	private IRodinFile[] getMachineFile(IRodinProject rodinProject)
			throws RodinDBException {
		final ArrayList<IRodinFile> result = new ArrayList<IRodinFile>();
		for (IRodinElement element : rodinProject.getChildren()) {
			if(!(element instanceof IRodinFile))
				continue;
			final IRodinFile rf = (IRodinFile) element;
			if(!(rf.getRoot() instanceof IMachineRoot))
				continue;
			result.add(rf);
		}
		return result.toArray(new IRodinFile[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#removeAttribute(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IRefinesMachine;
		return ((IRefinesMachine) element).hasAbstractMachineName();
	}
}
