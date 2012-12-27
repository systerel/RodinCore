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
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAssignmentElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class AssignmentAttributeManipulation extends
		AbstractAttributeManipulation {

	private IAssignmentElement asAssignment(IRodinElement element) {
		assert element instanceof IAssignmentElement;
		return (IAssignmentElement) element;
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asAssignment(element).setAssignmentString(newValue, monitor);
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAssignment(element).getAssignmentString();
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAssignment(element).setAssignmentString("", monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(ASSIGNMENT_ATTRIBUTE);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		logCantGetPossibleValues(ASSIGNMENT_ATTRIBUTE);
		return null;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAssignment(element).hasAssignmentString();
	}

}
