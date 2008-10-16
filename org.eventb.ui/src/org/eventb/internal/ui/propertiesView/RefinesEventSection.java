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
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesEventAbstractEventLabelAttributeFactory;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class RefinesEventSection extends CComboSection {

	@Override
	String getLabel() {
		return "Ref. Evt.";
	}

	@Override
	String getText() throws RodinDBException {
		IRefinesEvent rElement = (IRefinesEvent) element;
		return rElement.getAbstractEventLabel();
	}

	@Override
	void setData() {
		IRodinFile machine = element.getRodinFile();
		IMachineRoot root = (IMachineRoot) machine.getRoot();
		final IEvent[] events;
		try {
			events = root.getEvents();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the events of " + machine.getBareName());
			return;
		}
		for (IEvent event : events) {
			try {
				comboWidget.add(event.getLabel());
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting the label of " + event.getElementName());
				return;
			}
		}
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new RefinesEventAbstractEventLabelAttributeFactory(), text,
				monitor);
	}

}
