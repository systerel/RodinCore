/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
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

import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IExpressionElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ExpressionAttributeManipulation extends
		AbstractAttributeManipulation {

	private IExpressionElement asExpression(IRodinElement element) {
		assert element instanceof IExpressionElement;
		return (IExpressionElement) element;
	}
	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asExpression(element).getExpressionString();
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asExpression(element).setExpressionString(newValue, monitor);
	}

	@Override
	public void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asExpression(element).setExpressionString("", monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		logCantRemove(EXPRESSION_ATTRIBUTE);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable for Expression Element.
		return null;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asExpression(element).hasExpressionString();

	}
	
}
