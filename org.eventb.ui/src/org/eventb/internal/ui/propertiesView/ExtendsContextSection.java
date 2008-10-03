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
import org.eventb.core.IContextFile;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.ExtendsContextAbstractContextNameAttributeFactory;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ExtendsContextSection extends CComboSection {

	@Override
	String getLabel() {
		return "Abs. Ctx.";
	}

	@Override
	String getText() throws RodinDBException {
		IExtendsContext eElement = (IExtendsContext) element;
		return eElement.getAbstractContextName();
	}

	@Override
	void setData() {
		final IRodinProject project = element.getRodinProject();
		final IContextFile[] contexts;
		try {
			contexts = project.getChildrenOfType(IContextFile.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the contexts of " + project);
			return;
		}
		for (IContextFile context : contexts) {
			final String bareName = context.getComponentName();
			comboWidget.add(bareName);
		}
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new ExtendsContextAbstractContextNameAttributeFactory(), text,
				monitor);
	}

}
