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
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.IExpressionElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.ExpressionAttributeFactory;
import org.rodinp.core.RodinDBException;

public class ExpressionSection extends TextSection {

	@Override
	String getLabel() {
		return "Expression";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof IExpressionElement) {
			IExpressionElement eElement = (IExpressionElement) element;
			if (eElement == null)
				return null;
			return eElement.getExpressionString();
		}
		return null;
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = true;
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, new ExpressionAttributeFactory(),
				text, monitor);
	}

}
