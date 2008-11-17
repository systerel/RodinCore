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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ICommentedElement;
import org.rodinp.core.RodinDBException;

public class CommentAttributeFactory implements
		IAttributeFactory<ICommentedElement> {

	public String getValue(ICommentedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (element.hasComment())
			return element.getComment();
		else
			return "";
	}

	public void setValue(ICommentedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setComment(newValue, monitor);
	}

	public void setDefaultValue(ICommentedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.setComment("", new NullProgressMonitor());
	}

	public void removeAttribute(ICommentedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.COMMENT_ATTRIBUTE, monitor);
	}

	public String[] getPossibleValues(ICommentedElement element,
			IProgressMonitor monitor) {
		// Not applicable for Commented Element.
		return null;
	}

	public boolean hasValue(ICommentedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasComment();

	}
}
