/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *     Systerel - remove empty comment attributes within setValue()
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class CommentAttributeManipulation extends AbstractAttributeManipulation {

	private ICommentedElement asCommented(IRodinElement element) {
		assert element instanceof ICommentedElement;
		return (ICommentedElement) element;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		final ICommentedElement commented = asCommented(element);
		if (commented.hasComment())
			return commented.getComment();
		else
			return "";
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		if (newValue.isEmpty()) {
			asCommented(element).removeAttribute(COMMENT_ATTRIBUTE, monitor);
			return;
		}
		asCommented(element).setComment(newValue, monitor);
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		//
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asCommented(element).removeAttribute(COMMENT_ATTRIBUTE, monitor);
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// Not applicable for Commented Element.
		return null;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (element == null)
			return false;
		return asCommented(element).hasComment();

	}

}
