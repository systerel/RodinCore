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
import org.eventb.core.IIdentifierElement;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class IdentifierSection extends TextSection {

	@Override
	String getLabel() {
		return "Identifier";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element instanceof IIdentifierElement) {
			return ((IIdentifierElement) element).getIdentifierString();
		}
		return null;
	}


	@Override
	void setStyle() {
		style = SWT.SINGLE;
		math = true;
	}
	
	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, UIUtils
				.getIdentifierAttributeFactory(element), text, monitor);
	}

}
