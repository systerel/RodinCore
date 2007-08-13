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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class RefinesEventAbstractEventLabelAttributeFactory implements
		IAttributeFactory {

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IRefinesEvent)) {
			return;
		}
		IRefinesEvent rElement = (IRefinesEvent) element;
		IEvent event = (IEvent) rElement.getParent();
		rElement.setAbstractEventLabel(event.getLabel(), monitor);
	}

	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IRefinesEvent;
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		return refinesEvent.getAbstractEventLabel();
	}

	public void setValue(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IRefinesEvent;
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		String value;
		try {
			value = getValue(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			refinesEvent.setAbstractEventLabel(newValue,
					new NullProgressMonitor());
		}
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		List<String> results = new ArrayList<String>();

		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		IEvent event = (IEvent) refinesEvent.getParent();
		IMachineFile file = (IMachineFile) event.getParent();
		IRefinesMachine[] refinesClauses = file.getRefinesClauses();
		if (refinesClauses.length == 1) {
			IRefinesMachine refinesMachine = refinesClauses[0];
			IMachineFile abstractMachine = refinesMachine.getAbstractMachine();
			if (abstractMachine.exists()) {
				IEvent[] events = abstractMachine.getEvents();
				for (IEvent absEvent : events) {
					results.add(absEvent.getLabel());
				}
			}
		}
		return results.toArray(new String[results.size()]);
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

}
