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
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ExtendsContextAbstractContextNameAttributeFactory implements
		IAttributeFactory {

	public void setDefaultValue(IEventBEditor<?> editor,
			IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IRefinesMachine refinesEvent = (IRefinesMachine) element;
		String name = "abstract_context";
		refinesEvent.setAbstractMachineName(name, new NullProgressMonitor());
	}

	public String getValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		IExtendsContext extendsContext = (IExtendsContext) element;
		return extendsContext.getAbstractContextName();
	}

	public void setValue(IAttributedElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IExtendsContext;
		IExtendsContext extendsContext = (IExtendsContext) element;
		extendsContext.setAbstractContextName(str, new NullProgressMonitor());
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();
		IExtendsContext extendsContext = (IExtendsContext) element;
		IContextRoot context = (IContextRoot) extendsContext.getParent();
		String contextName = context.getElementName();

		IContextRoot[] contextRoots = getContextRoots(extendsContext);
		for (IContextRoot root : contextRoots) {
			String bareName = root.getElementName();
			if (!contextName.equals(bareName))
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

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}
	
	public boolean hasValue(IAttributedElement element, IProgressMonitor monitor)
			throws RodinDBException {
		assert element instanceof IExtendsContext;
		return ((IExtendsContext) element).hasAbstractContextName();
	}

}
