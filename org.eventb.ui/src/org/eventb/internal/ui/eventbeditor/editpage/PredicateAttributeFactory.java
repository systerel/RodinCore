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
import org.eventb.core.IPredicateElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class PredicateAttributeFactory implements
		IAttributeFactory<IPredicateElement> {

	public void setValue(IPredicateElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setPredicateString(newValue, null);
	}

	public String getValue(IPredicateElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getPredicateString();
	}

	public void setDefaultValue(IEventBEditor<?> editor,
			IPredicateElement element, IProgressMonitor monitor)
			throws RodinDBException {
		element.setPredicateString("", monitor);
	}

	public void removeAttribute(IPredicateElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IPredicateElement element,
			IProgressMonitor monitor) {
		// Not applicable for Predicate Element.
		return null;
	}

	public boolean hasValue(IPredicateElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasPredicateString();
	}
}
