/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
 *     Systerel - filter getPossibleValues() for cycles
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.manipulation;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class SeesContextNameAttributeManipulation extends AbstractContextManipulation<ISeesContext> {

	@Override
	protected ISeesContext asContextClause(IRodinElement element) {
		assert element instanceof ISeesContext;
		return (ISeesContext) element;
	}
	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).getSeenContextName();
	}

	@Override
	public void setValue(IRodinElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		asContextClause(element).setSeenContextName(newValue, monitor);
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

	@Override
	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asContextClause(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).hasSeenContextName();
	}

	@Override
	protected void removeCycle(ISeesContext element, Set<String> contexts) {
		// do nothing
	}
}
