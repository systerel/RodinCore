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

import static org.eclipse.ui.actions.ActionFactory.NEW_WIZARD_DROP_DOWN;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * The action provider for <code>IEventBRoot</code>s. 
 */
public class RootActionProvider extends NavigatorActionProvider {

    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        // forward doubleClick to doubleClickAction
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
              ActionCollection.getOpenAction(site));
        // forwards pressing the delete key to deleteAction
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), ActionCollection.getDeleteAction(site));
    }
	

    
    @Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(new Separator(ICommonMenuConstants.GROUP_NEW));
		menu.appendToGroup(ICommonMenuConstants.GROUP_NEW, getNewAction());
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, ActionCollection
				.getOpenAction(site));
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH,
				buildOpenWithMenu());
		menu.appendToGroup(GROUP_MODELLING, ActionCollection
				.getDeleteAction(site));
	}

	/**
	 * Builds a New menu action with drop down specific to the current IDE.
	 *
	 * @return the built menu
	 */
	private IWorkbenchAction getNewAction() {
		final IWorkbenchAction newAction = NEW_WIZARD_DROP_DOWN
				.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		newAction.setText(newAction.getToolTipText());
		return newAction;
	}
	
}
