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
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class LabelAttributeFactory implements
		IAttributeFactory<ILabeledElement> {

	protected abstract String getPrefix();

	public void setDefaultValue(IEventBEditor<?> editor,
			ILabeledElement element, IProgressMonitor monitor)
			throws RodinDBException {
		String prefix = getPrefix();
		String label = UIUtils.getFreeElementLabel(editor,
				(IInternalParent) element.getParent(),
				element.getElementType(), prefix);
		element.setLabel(label, monitor);
	}

	public void setValue(ILabeledElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setLabel(newValue, monitor);
	}

	public String getValue(ILabeledElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getLabel();
	}

	public void removeAttribute(ILabeledElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(ILabeledElement element,
			IProgressMonitor monitor) {
		// Not applicable for Labeled Element
		return null;
	}

	public boolean hasValue(ILabeledElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasLabel();
	}
}
