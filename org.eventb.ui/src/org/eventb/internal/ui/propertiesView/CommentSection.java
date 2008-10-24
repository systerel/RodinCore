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
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.CommentAttributeFactory;
import org.rodinp.core.RodinDBException;

public class CommentSection extends TextSection<ICommentedElement> {

	@Override
	String getLabel() {
		return "Comment";
	}

	@Override
	String getText() throws RodinDBException {
		if (element == null)
			return null;
		if (element.exists() && element.hasComment())
			return element.getComment();
		return "";
	}

	@Override
	void setStyle() {
		style = SWT.MULTI;
		math = false;
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, new CommentAttributeFactory(),
				text, monitor);
	}

}
