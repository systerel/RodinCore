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

package org.eventb.internal.ui.obligationexplorer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for the Obligation Explorer.
 */
public class ObligationExplorerActionGroup extends ActionGroup {

	// The project explorer.
	private ObligationExplorer explorer;

	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;

	public static RefreshAction refreshAction;

	/**
	 * Constructor: Create the actions.
	 * 
	 * @param obligationExplorer
	 *            The project explorer
	 */
	public ObligationExplorerActionGroup(ObligationExplorer obligationExplorer) {
		this.explorer = obligationExplorer;
		drillDownAdapter = new DrillDownAdapter(explorer.getTreeViewer());

		refreshAction = new RefreshAction(obligationExplorer.getSite()
				.getShell());

	}

	/**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");

			IStructuredSelection ssel = (IStructuredSelection) sel;
			// newMenu.add(newProjectAction);
			newMenu.add(new Separator());
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IRodinProject) {
					// Do nothing
				}
			}
			menu.add(newMenu);
			// menu.add(deleteAction);
			menu.add(refreshAction);
			// if ((ssel.size() == 1) && (ssel.getFirstElement() instanceof
			// IRodinFile)) menu.add(proveAction);
			menu.add(new Separator());
			drillDownAdapter.addNavigationActions(menu);

			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
