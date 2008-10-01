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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.ui.EventBUIPlugin;

/**
 * An action delegate for creating new components.
 *
 */
public class NewComponentActionDelegate implements IViewActionDelegate {

	IViewPart view;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart viewPart) {
		this.view = viewPart;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) view.getViewSite().getSelectionProvider().getSelection();
				NewComponentWizard wizard = new NewComponentWizard();
				wizard.init(EventBUIPlugin.getDefault().getWorkbench(),
						sel);
				WizardDialog dialog = new WizardDialog(EventBUIPlugin
						.getActiveWorkbenchShell(), wizard);
				dialog.create();
				// SWTUtil.setDialogSize(dialog, 500, 500);
				dialog.open();
			}
		});

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing

	}

}
