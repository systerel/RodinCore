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
import org.eventb.core.IExpressionElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class ExpressionAttributeFactory implements
		IAttributeFactory<IExpressionElement> {

	public String getValue(IExpressionElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getExpressionString();
	}

	public void setValue(IExpressionElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setExpressionString(newValue, monitor);
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IExpressionElement element, IProgressMonitor monitor)
			throws RodinDBException {
		element.setExpressionString("", monitor);
	}

	public void removeAttribute(IExpressionElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IExpressionElement element,
			IProgressMonitor monitor) {
		// Not applicable for Expression Element.
		return null;
	}

	public boolean hasValue(IExpressionElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasExpressionString();

	}
}
