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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public abstract class IdentifierAttributeFactory implements
		IAttributeFactory<IIdentifierElement> {

	public abstract String getPrefix();

	public void setDefaultValue(IEventBEditor<?> editor,
			IIdentifierElement element, IProgressMonitor monitor)
			throws RodinDBException {
		String prefix = getPrefix();
		String identifier = UIUtils.getFreeElementIdentifier(editor,
				(IInternalParent) element.getParent(),
				element.getElementType(), prefix);
		element.setIdentifierString(identifier, monitor);
	}

	public void setValue(IIdentifierElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setIdentifierString(newValue, new NullProgressMonitor());
	}

	public String getValue(IIdentifierElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getIdentifierString();
	}

	public void removeAttribute(IIdentifierElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.IDENTIFIER_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IIdentifierElement element,
			IProgressMonitor monitor) {
		// Not applicable to Identifier Elements
		return null;
	}

	public boolean hasValue(IIdentifierElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasIdentifierString();
	}

}
