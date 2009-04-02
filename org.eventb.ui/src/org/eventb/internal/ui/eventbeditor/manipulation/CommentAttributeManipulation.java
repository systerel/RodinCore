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
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class CommentAttributeManipulation extends AbstractAttributeManipulation
		implements IAttributeManipulation {

	private ICommentedElement asCommented(IRodinElement element) {
		assert element instanceof ICommentedElement;
		return (ICommentedElement) element;
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final ICommentedElement commented = asCommented(element);
		if (commented.hasComment())
			return commented.getComment();
		else
			return "";
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asCommented(element).setComment(newValue, monitor);
	}

	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		//
	}

	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCommented(element).removeAttribute(COMMENT_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable for Commented Element.
		logCantGetPossibleValues(COMMENT_ATTRIBUTE);
		return null;
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (element == null)
			return false;
		return asCommented(element).hasComment();

	}

}
