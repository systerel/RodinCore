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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class IdentifierAttributeFactory implements IAttributeFactory {

	public abstract String getPrefix();

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IIdentifierElement)) {
			return;
		}
		String prefix = getPrefix();
		IIdentifierElement iElement = (IIdentifierElement) element;
		String identifier = UIUtils.getFreeElementIdentifier(editor,
				(IInternalParent) element.getParent(), iElement
						.getElementType(), prefix);
		iElement.setIdentifierString(identifier, monitor);
	}

	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		iElement.setIdentifierString(newValue, new NullProgressMonitor());
	}

	public String getValue(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IIdentifierElement;
		final IIdentifierElement iElement = (IIdentifierElement) element;
		return iElement.getIdentifierString();
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Not applicable to Identifier Elements
		return null;
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IIdentifierElement;
		return ((IIdentifierElement) element).hasIdentifierString();
	}
	
}
