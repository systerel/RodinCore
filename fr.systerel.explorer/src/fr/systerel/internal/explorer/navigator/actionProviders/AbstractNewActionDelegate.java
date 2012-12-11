/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;

/**
 * Common implementation of action delegates for creating new projects and new
 * components.
 */
public abstract class AbstractNewActionDelegate implements IViewActionDelegate {

	public static class NewComponent extends AbstractNewActionDelegate {
		@Override
		protected INewWizard createWizard() {
			return new NewComponentWizard();
		}
	}

	public static class NewProject extends AbstractNewActionDelegate {
		@Override
		protected INewWizard createWizard() {
			return new NewProjectWizard();
		}
	}

	private IViewPart view;
	private IStructuredSelection ssel;

	@Override
	public void init(IViewPart viewPart) {
		this.view = viewPart;
	}

	@Override
	public void run(IAction action) {
		final IViewSite site = view.getViewSite();
		final Shell shell = site.getShell();
		final IWorkbench workbench = site.getWorkbenchWindow().getWorkbench();
		final INewWizard wizard = createWizard();
		wizard.init(workbench, ssel);
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			@Override
			public void run() {
				final WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.open();
			}
		});
	}

	protected abstract INewWizard createWizard();

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.ssel = (IStructuredSelection) selection;
		} else {
			this.ssel = null;
		}
	}

}
