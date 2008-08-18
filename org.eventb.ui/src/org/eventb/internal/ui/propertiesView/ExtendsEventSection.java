/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

public class ExtendsEventSection extends CComboSection {

	private static final String TRUE = "true";

	private static final String FALSE = "false";

	@Override
	String getLabel() {
		return "Extended";
	}

	@Override
	String getText() throws RodinDBException {
		if (((IEvent) element).isExtended())
			return TRUE;
		else {
			return FALSE;
		}
	}

	@Override
	void setData() {
		comboWidget.add(TRUE);
		comboWidget.add(FALSE);
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		IEvent eElement = (IEvent) element;
		boolean extended;
		try {
			extended = eElement.isExtended();
		} catch (RodinDBException e) {
			// Set the attribute anyway if there was a problem accessing the
			// database
			eElement.setExtended(text.equalsIgnoreCase(FALSE),
					monitor);
			return;
		}
		if (extended && text.equalsIgnoreCase(FALSE)) {
			eElement.setExtended(false, new NullProgressMonitor());
		} else if (!extended && text.equalsIgnoreCase(TRUE)) {
			eElement.setExtended(true, new NullProgressMonitor());
		}
	}

}
