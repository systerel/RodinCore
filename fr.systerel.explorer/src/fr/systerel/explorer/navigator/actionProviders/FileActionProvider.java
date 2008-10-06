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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.ICommonActionConstants;

/**
 * The Action Provider for <code>IMachineFile</code>s and <code>IContextFile</code>s 
 * @author Maria Husmann
 *
 */
public class FileActionProvider extends NavigatorActionProvider {

	/**
	 * Create the actions.
	 */

	/* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
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
		menu.add(new Separator(GROUP_MODELLING));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getDeleteAction(site));
    	
    }	
    
	
}
