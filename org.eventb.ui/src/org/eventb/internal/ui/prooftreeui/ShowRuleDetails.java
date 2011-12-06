/*******************************************************************************
 * Copyright (c) 2011 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShowRuleDetails extends AbstractProofTreeAction {

	public ShowRuleDetails() {
		super(false);
	}

	@Override
	public void run(IAction action) {
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (workbenchWindow == null) {
			return;
		}
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}
		try {
			activePage.showView(RuleDetailsView.VIEW_ID, null,
					IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			// Nothing to do here
		}
	}
}
