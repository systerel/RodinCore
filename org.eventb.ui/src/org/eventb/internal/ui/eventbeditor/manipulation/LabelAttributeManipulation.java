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
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class LabelAttributeManipulation extends AbstractAttributeManipulation {

	public void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		final ILabeledElement labeledElement = asLabeled(element);
		final String label = UIUtils.getFreeElementLabel(
				(IInternalElement) labeledElement.getParent(), labeledElement
						.getElementType());
		labeledElement.setLabel(label, monitor);
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asLabeled(element).setLabel(newValue, monitor);
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asLabeled(element).getLabel();
	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(LABEL_ATTRIBUTE);
	}

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable for Labeled Element
		logCantGetPossibleValues(LABEL_ATTRIBUTE);
		return null;
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asLabeled(element).hasLabel();
	}
	
	protected ILabeledElement asLabeled(IRodinElement element) {
		assert element instanceof ILabeledElement;
		return (ILabeledElement) element;
	}
}
