/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for the Project Explorer.
 */
public class ProjectExplorerActionGroup extends ActionGroup {

	// The project explorer.
	private ProjectExplorer explorer;

	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;

	public static Action newProjectAction;

	public static Action newComponentAction;

	public static RefreshAction refreshAction;

	/**
	 * Constructor: Create the actions.
	 * 
	 * @param projectExplorer
	 *            The project explorer
	 */
	public ProjectExplorerActionGroup(ProjectExplorer projectExplorer) {
		this.explorer = projectExplorer;
		drillDownAdapter = new DrillDownAdapter(explorer.getTreeViewer());

		refreshAction = new RefreshAction(projectExplorer.getSite().getShell());

		// Creating the public action
		newProjectAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
						.getDisplay(), new Runnable() {
					public void run() {
						NewProjectWizard wizard = new NewProjectWizard();
						WizardDialog dialog = new WizardDialog(EventBUIPlugin
								.getActiveWorkbenchShell(), wizard);
						dialog.create();
						dialog.open();
					}
				});
			}
		};
		newProjectAction.setText("&Project");
		newProjectAction.setToolTipText("Create new project");
		newProjectAction.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_PROJECT_PATH));

		newComponentAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
						.getDisplay(), new Runnable() {
					public void run() {
						IStructuredSelection sel = (IStructuredSelection) explorer
								.getTreeViewer().getSelection();
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
		};
		newComponentAction.setText("&Component");
		newComponentAction.setToolTipText("Create new component");
		newComponentAction.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_COMPONENT_PATH));

		
	}


	/**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		Object input = getContext().getInput();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");

			IStructuredSelection ssel = (IStructuredSelection) sel;

			// Can only create new Project if at the Workspace level
			if (input == null) {
				newMenu.add(newProjectAction);
				if (ssel.size() == 1) {
					newMenu.add(newComponentAction);
				}
			} else {
				newMenu.add(newComponentAction);
			}
			newMenu.add(new Separator("new"));
			menu.add(newMenu);
			menu.add(new Separator("modelling"));
			menu.add(new Separator("proving"));
			menu.add(new Separator());
//			menu.add(refreshAction);
			
			menu.add(new Separator());
			drillDownAdapter.addNavigationActions(menu);

			// Other plug-ins can contribute there actions here
			
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
