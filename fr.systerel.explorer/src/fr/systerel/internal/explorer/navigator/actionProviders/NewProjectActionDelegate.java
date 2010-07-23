/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.internal.explorer.navigator.actionProviders;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eventb.internal.ui.wizards.NewProjectWizard;

/**
 * 
 * An action provider for creating new projects
 *
 */
public class NewProjectActionDelegate implements IViewActionDelegate {

	private IViewPart view;
	IStructuredSelection ssel;
	
	@Override
	public void init(IViewPart viewPart) {
		this.view = viewPart;
	}

	@Override
	public void run(IAction action) {
		final IViewSite site = view.getViewSite();
		final Shell shell = site.getShell();
		final IWorkbench workbench = site.getWorkbenchWindow().getWorkbench();
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			@Override
			public void run() {
				NewProjectWizard wizard = new NewProjectWizard();
				wizard.init(workbench, ssel);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.open();
			}
		});
	}

	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			this.ssel = (IStructuredSelection) sel;
		} else {
			this.ssel = null;
		}
	}

}
