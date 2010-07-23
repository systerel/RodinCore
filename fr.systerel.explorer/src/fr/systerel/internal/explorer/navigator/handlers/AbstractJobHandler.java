/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractJobHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection sel = getSelection(event);
		if (!(sel instanceof IStructuredSelection)) {
			return null;
		}
		final WorkspaceJob job = getWorkspaceJob((IStructuredSelection) sel);
		job.setUser(true);
		job.schedule();
		return null;
	}

	private ISelection getSelection(ExecutionEvent event) {
		final IWorkbenchWindow ww = HandlerUtil.getActiveWorkbenchWindow(event);
		if (ww == null)
			return null;
		final IWorkbenchPage page = ww.getActivePage();
		if (page == null)
			return null;
		return page.getSelection();
	}

	/**
	 * @return the specific WorkspaceJob associated with this handler.
	 */
	protected abstract WorkspaceJob getWorkspaceJob(IStructuredSelection sel);

}
