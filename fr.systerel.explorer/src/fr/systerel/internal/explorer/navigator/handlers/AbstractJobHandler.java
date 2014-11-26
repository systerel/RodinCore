/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelectionChecked;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public abstract class AbstractJobHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection sel = getCurrentSelectionChecked(event);
		if (!(sel instanceof IStructuredSelection)) {
			return null;
		}
		final WorkspaceJob job = getWorkspaceJob((IStructuredSelection) sel);
		job.setUser(true);
		job.schedule();
		return null;
	}

	/**
	 * @return the specific WorkspaceJob associated with this handler.
	 */
	protected abstract WorkspaceJob getWorkspaceJob(IStructuredSelection sel);

}
