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
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class AssignmentAttributeFactory implements IAttributeFactory {

	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement aElement = (IAssignmentElement) element;
		aElement.setAssignmentString(newValue, monitor);
	}

	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IAssignmentElement;
		final IAssignmentElement pElement = (IAssignmentElement) element;
		return pElement.getAssignmentString();
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IAssignmentElement aElement = (IAssignmentElement) element;
		aElement.setAssignmentString("", monitor);
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.ASSIGNMENT_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Not applicable for Assignment Element.
		return null;
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IAssignmentElement;
		return ((IAssignmentElement) element).hasAssignmentString();
	}
}
