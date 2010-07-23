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


package fr.systerel.internal.explorer.navigator.actionProviders;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinCore;

/**
 * An action delegate for creating new components.
 *
 */
public class NewComponentActionDelegate implements IViewActionDelegate {

	IViewPart view;
	
	@Override
	public void init(IViewPart viewPart) {
		this.view = viewPart;
	}

	@Override
	public void run(IAction action) {
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) view.getViewSite().getSelectionProvider().getSelection();
				//The wizard uses IRodinProjects not IProjects
				//get the corresponding IRodinProject
				if (sel.getFirstElement() instanceof IProject) {
					IProject project = (IProject)sel.getFirstElement();
					sel = new StructuredSelection(RodinCore.getRodinDB().getRodinProject(project.getName()));
					
				}
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

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing

	}

}
