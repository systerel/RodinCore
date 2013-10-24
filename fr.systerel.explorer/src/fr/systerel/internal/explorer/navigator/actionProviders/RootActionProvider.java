/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import static fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection.getDeleteAction;
import static fr.systerel.internal.explorer.navigator.actionProviders.ActionCollection.getOpenAction;
import static org.eclipse.ui.IWorkbenchCommandConstants.EDIT_DELETE;
import static org.eclipse.ui.actions.ActionFactory.NEW_WIZARD_DROP_DOWN;
import static org.eclipse.ui.navigator.ICommonActionConstants.OPEN;
import static org.eclipse.ui.navigator.ICommonMenuConstants.GROUP_NEW;
import static org.eclipse.ui.navigator.ICommonMenuConstants.GROUP_OPEN;
import static org.eclipse.ui.navigator.ICommonMenuConstants.GROUP_OPEN_WITH;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * The action provider for <code>IEventBRoot</code>s.
 */
public class RootActionProvider extends NavigatorActionProvider {

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		// forward doubleClick to doubleClickAction
		actionBars.setGlobalActionHandler(OPEN, getOpenAction(site));
		// forwards pressing the delete key to deleteAction
		actionBars.setGlobalActionHandler(EDIT_DELETE, getDeleteAction(site));
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(new Separator(GROUP_NEW));
		menu.appendToGroup(GROUP_NEW, getNewAction());
		menu.appendToGroup(GROUP_OPEN, getOpenAction(site));
		menu.appendToGroup(GROUP_OPEN_WITH, buildOpenWithMenu());
		menu.add(new Separator(GROUP_MODELLING));
		menu.appendToGroup(GROUP_MODELLING, getDeleteAction(site));
	}

	/**
	 * Builds a New menu action with drop down specific to the current IDE.
	 *
	 * @return the built menu
	 */
	private static IWorkbenchAction getNewAction() {
		final IWorkbenchAction newAction = NEW_WIZARD_DROP_DOWN
				.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		newAction.setText(newAction.getToolTipText());
		return newAction;
	}

}
