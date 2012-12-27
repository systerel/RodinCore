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
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class PredicateAttributeManipulation extends
		AbstractAttributeManipulation {

	private IPredicateElement asPredicate(IRodinElement element) {
		assert element instanceof IPredicateElement;
		return (IPredicateElement) element;
	}
	
	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asPredicate(element).setPredicateString(newValue, null);
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asPredicate(element).getPredicateString();
	}

	@Override
	public void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asPredicate(element).setPredicateString("\u22a4", monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		logCantRemove(PREDICATE_ATTRIBUTE);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable for Predicate Element.
		logCantGetPossibleValues(PREDICATE_ATTRIBUTE);
		return null;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asPredicate(element).hasPredicateString();
	}
	
}
