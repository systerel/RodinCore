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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class ExtendsContextAbstractContextNameAttributeFactory extends
		AbstractContextFactory<IExtendsContext> {

	public void setDefaultValue(IEventBEditor<?> editor,
			IExtendsContext element, IProgressMonitor monitor)
			throws RodinDBException {
		String name = "abstract_context";
		element.setAbstractContextName(name, new NullProgressMonitor());
	}

	public String getValue(IExtendsContext element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.getAbstractContextName();
	}

	public void setValue(IExtendsContext element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		element.setAbstractContextName(str, new NullProgressMonitor());
	}

	@Override
	public IExtendsContext[] getClauses(IExtendsContext element) {
		final IContextRoot root = (IContextRoot) element.getParent();
		try {
			return root.getExtendsClauses();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when reading the extends clauses");
			return new IExtendsContext[0];
		}
	}

	public void removeAttribute(IExtendsContext element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IExtendsContext element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasAbstractContextName();
	}
}
