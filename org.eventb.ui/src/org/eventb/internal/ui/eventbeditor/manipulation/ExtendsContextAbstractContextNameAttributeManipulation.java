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

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class ExtendsContextAbstractContextNameAttributeManipulation extends
		AbstractContextManipulation<IExtendsContext> {

	@Override
	protected IExtendsContext asContextClause(IRodinElement element) {
		assert element instanceof IExtendsContext;
		return (IExtendsContext) element;
	}
	
	public void setDefaultValue(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// do nothing
	}

	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).getAbstractContextName();
	}

	public void setValue(IRodinElement element, String str,
			IProgressMonitor monitor) throws RodinDBException {
		asContextClause(element).setAbstractContextName(str, null);
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

	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asContextClause(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).hasAbstractContextName();
	}
}
