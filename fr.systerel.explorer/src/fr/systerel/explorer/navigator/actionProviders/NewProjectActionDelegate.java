/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.navigator.actionProviders;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.ui.EventBUIPlugin;

import fr.systerel.explorer.navigator.wizards.NewProjectWizard;

/**
 * 
 * An action provider for creating new projects
 *
 */
public class NewProjectActionDelegate implements IViewActionDelegate {

	IViewPart view;
	
	public void init(IViewPart viewPart) {
		this.view = viewPart;
	}

	public void run(IAction action) {
		BusyIndicator.showWhile(view.getSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				NewProjectWizard wizard = new NewProjectWizard();
				WizardDialog dialog = new WizardDialog(EventBUIPlugin
						.getActiveWorkbenchShell(), wizard);
				dialog.create();
				dialog.open();
			}
		});
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing

	}

}
