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
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class SeesContextNameAttributeFactory extends AbstractContextFactory<ISeesContext> {

	public void setDefaultValue(ISeesContext element, IProgressMonitor monitor)
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

	public void removeAttribute(ISeesContext element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(ISeesContext element, IProgressMonitor monitor)
			throws RodinDBException {
		return element.hasSeenContextName();
	}
}
