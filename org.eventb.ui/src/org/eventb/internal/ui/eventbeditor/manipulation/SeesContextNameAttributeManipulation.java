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
package org.eventb.internal.ui.eventbeditor.manipulation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SeesContextNameAttributeManipulation extends AbstractContextManipulation<ISeesContext> {

	@Override
	protected ISeesContext getContextElement(IRodinElement element) {
		assert element instanceof IExtendsContext;
		return (ISeesContext) element;
	}
	
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// do nothing
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getContextElement(element).getSeenContextName();
	}

	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		getContextElement(element).setSeenContextName(newValue, monitor);
	}
	
	@Override
	public ISeesContext[] getClauses(ISeesContext element) {
		final IMachineRoot root = (IMachineRoot) element.getParent();
		try {
			return root.getSeesClauses();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when reading the sees clauses");
			return new ISeesContext[0];
		}
	}

	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		getContextElement(element).removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return getContextElement(element).hasSeenContextName();
	}
}
