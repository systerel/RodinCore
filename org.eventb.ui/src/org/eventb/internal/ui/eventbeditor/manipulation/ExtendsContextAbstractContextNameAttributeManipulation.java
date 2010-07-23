/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBProject;
import org.eventb.core.IExtendsContext;
import org.eventb.internal.ui.EventBUtils;
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
	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).getAbstractContextName();
	}

	@Override
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

	@Override
	public void removeAttribute(IRodinElement element,
			IProgressMonitor monitor) throws RodinDBException {
		asContextClause(element).removeAttribute(TARGET_ATTRIBUTE, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asContextClause(element).hasAbstractContextName();
	}

	@Override
	protected void removeCycle(IExtendsContext element, Set<String> contexts) {
		final IContextRoot root = (IContextRoot) element.getRoot();
		final IEventBProject prj = root.getEventBProject();
		final Iterator<String> iter = contexts.iterator();
		while (iter.hasNext()) {
			final String ctxName = iter.next();
			final IContextRoot ctx = prj.getContextRoot(ctxName);
			if (isExtendedBy(root, ctx)) {
				iter.remove();
			}
		}
	}

	/**
	 * Returns true if abstractContext is extended directly or indirectly by
	 * root .
	 */
	private boolean isExtendedBy(IContextRoot abstractContext, IContextRoot root) {
		try {
			for (IContextRoot ctx : EventBUtils.getAbstractContexts(root)) {
				if (ctx.equals(abstractContext)
						|| isExtendedBy(abstractContext, ctx))
					return true;
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return false;
	}
}
