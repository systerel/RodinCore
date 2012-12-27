/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RefinesEventAbstractEventLabelAttributeManipulation extends
		AbstractAttributeManipulation {

	private IRefinesEvent asRefinesEvent(IRodinElement element) {
		assert element instanceof IRefinesEvent;
		return (IRefinesEvent) element;
	}
	
	@Override
	public void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IEvent event = (IEvent) element.getParent();
		asRefinesEvent(element).setAbstractEventLabel(event.getLabel(),
				monitor);
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesEvent(element).getAbstractEventLabel();
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asRefinesEvent(element).setAbstractEventLabel(newValue,
				new NullProgressMonitor());
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
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
		if (abstractMachine != null && abstractMachine.exists()) {
			for (IEvent absEvent : abstractMachine.getEvents()) {
				results.add(absEvent.getLabel());
			}
		}
	}

	@Override
	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asRefinesEvent(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asRefinesEvent(element).hasAbstractEventLabel();
	}
}
