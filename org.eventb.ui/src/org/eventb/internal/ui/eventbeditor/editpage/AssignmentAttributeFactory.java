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
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class AssignmentAttributeFactory implements
		IAttributeFactory<IAssignmentElement> {

	public void setValue(IAssignmentElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setAssignmentString(newValue, monitor);
	}

	public String getValue(IAssignmentElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getAssignmentString();
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IAssignmentElement element, IProgressMonitor monitor)
			throws RodinDBException {
		element.setAssignmentString("", monitor);
	}

	public void removeAttribute(IAssignmentElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IAssignmentElement element,
			IProgressMonitor monitor) {
		// Not applicable for Assignment Element.
		return null;
	}

	public boolean hasValue(IAssignmentElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasAssignmentString();
	}
}
