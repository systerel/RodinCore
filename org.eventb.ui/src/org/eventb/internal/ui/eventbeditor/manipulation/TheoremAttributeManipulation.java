/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.THEOREM_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class TheoremAttributeManipulation extends AbstractBooleanManipulation {

	public TheoremAttributeManipulation() {
		super(Messages.attributeManipulation_theorem_true,
				Messages.attributeManipulation_theorem_false);
	}

	private IDerivedPredicateElement asDerivedPredicate(IRodinElement element) {
		assert element instanceof IDerivedPredicateElement;
		return (IDerivedPredicateElement) element;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final IDerivedPredicateElement derived = asDerivedPredicate(element);
		final boolean isSelected = derived.hasTheorem() && derived.isTheorem();
		return getText(isSelected);
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		if (newValue.equals(TRUE)) {
			asDerivedPredicate(element).setTheorem(true, monitor);
		} else if (newValue.equals(FALSE)) {
			asDerivedPredicate(element).setTheorem(false, monitor);
		} else {
			logNotPossibleValues(THEOREM_ATTRIBUTE, newValue);
		}
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asDerivedPredicate(element).setTheorem(false, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asDerivedPredicate(element).removeAttribute(THEOREM_ATTRIBUTE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (element == null)
			return false;
		return asDerivedPredicate(element).hasTheorem();
	}

}
