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
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
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
		refinesEvent.setAbstractEventLabel(newValue, new NullProgressMonitor());
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		IMachineRoot root = element.getAncestor(IMachineRoot.ELEMENT_TYPE);
		try {
			addAbstractEvents(results, root);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing abstract events for " + root);
		}
		return results.toArray(new String[results.size()]);
	}

	private void addAbstractEvents(List<String> results, IMachineRoot root)
			throws RodinDBException {
		if (root == null)
			return;
		IMachineRoot abstractMachine = EventBUtils.getAbstractMachine(root);
		if (abstractMachine.exists()) {
			for (IEvent absEvent : abstractMachine.getEvents()) {
				results.add(absEvent.getLabel());
			}
		}
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IRefinesEvent;
		return ((IRefinesEvent) element).hasAbstractEventLabel();
	}
}
