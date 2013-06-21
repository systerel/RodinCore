/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring to use commands and handlers
 *******************************************************************************/
package org.eventb.internal.ui.handlers;

import static org.eclipse.ui.handlers.HandlerUtil.getActiveWorkbenchWindow;
import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelection;
import static org.eventb.internal.ui.UIUtils.linkToProverUI;
import static org.eventb.ui.EventBUIPlugin.PROVING_PERSPECTIVE_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

/**
 * Handler for the command to open interactive proof.
 */
public class ProveInteractivelyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() != 1) {
				return null;
			}
			final Object obj = ssel.getFirstElement();
			linkToProverUI(obj);
			final IWorkbenchWindow ww = getActiveWorkbenchWindow(event);
			if (ww == null) {
				return null;
			}
			try {
				ww.getWorkbench().showPerspective(PROVING_PERSPECTIVE_ID, ww);
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
