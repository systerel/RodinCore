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
import org.eventb.core.IExpressionElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class ExpressionAttributeFactory implements IAttributeFactory {

	public String getValue(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExpressionElement;
		final IExpressionElement cElement = (IExpressionElement) element;
		return cElement.getExpressionString();
	}

	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExpressionElement;
		final IExpressionElement eElement = (IExpressionElement) element;
		eElement.setExpressionString(newValue, monitor);
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IExpressionElement eElement = (IExpressionElement) element;
		eElement.setExpressionString("", monitor);
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.EXPRESSION_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Not applicable for Expression Element.
		return null;
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IExpressionElement;
		return ((IExpressionElement) element).hasExpressionString();

	}
}
