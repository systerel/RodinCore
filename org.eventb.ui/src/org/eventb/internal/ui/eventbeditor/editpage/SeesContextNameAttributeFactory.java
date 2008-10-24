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
 *     Systerel - made IAttributeFactory generic
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class SeesContextNameAttributeFactory implements IAttributeFactory<ISeesContext> {

	public void setDefaultValue(IEventBEditor<?> editor,
			ISeesContext element, IProgressMonitor monitor)
			throws RodinDBException {
		element.setSeenContextName("context", monitor);
	}

	public String getValue(ISeesContext element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getSeenContextName();
	}

	public void setValue(ISeesContext element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		element.setSeenContextName(newValue, monitor);
	}

	public String[] getPossibleValues(ISeesContext element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		IContextRoot[] contextRoot = getContextRoots(element);
		for (IContextRoot context : contextRoot) {
			String bareName = context.getComponentName();
			results.add(bareName);
		}
		return results.toArray(new String[results.size()]);
	}

	private IContextRoot[] getContextRoots(IInternalElement element) {
		final IRodinProject rodinProject = element.getRodinProject();
		try {
			return UIUtils.getContextRootChildren(rodinProject);
		} catch (RodinDBException e) {
			UIUtils.log(e, "When computing the list of contexts of project "
					+ rodinProject);
			return new IContextRoot[0];
		}
	}

	public void removeAttribute(ISeesContext element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(ISeesContext element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasSeenContextName();
	}
}
