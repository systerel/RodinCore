/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class LabelAttributeFactory implements IAttributeFactory {

	protected abstract String getPrefix();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setDefaultValue(org.eventb.ui.eventbeditor.IEventBEditor,
	 *      org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof ILabeledElement)) {
			return;
		}
		String prefix = getPrefix();
		ILabeledElement lElement = (ILabeledElement) element;
		String label = UIUtils.getFreeElementLabel(editor,
				(IInternalParent) element.getParent(), lElement
						.getElementType(), prefix);
		lElement.setLabel(label, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#setValue(org.rodinp.core.IAttributedElement,
	 *      java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		lElement.setLabel(newValue, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getValue(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof ILabeledElement;
		final ILabeledElement lElement = (ILabeledElement) element;
		return lElement.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#removeAttribute(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory#getPossibleValues(org.rodinp.core.IAttributedElement,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Not applicable for Labeled Element
		return null;
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof ILabeledElement;
		return ((ILabeledElement) element).hasLabel();
	}
}
