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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.internal.ui.projectexplorer.ProjectExplorerActionGroup;

/**
 * @author Maria Husmann
 *
 */
public class ProjectActionProvider extends NavigatorActionProvider {

	
    @Override
	public void fillContextMenu(IMenuManager menu) {
		MenuManager newMenu = new MenuManager("&New");
		newMenu.add(ActionCollection.getNewProjectAction(site));
		newMenu.add(ActionCollection.getNewComponentAction(site));
    	// put in front
    	IContributionItem[] items = menu.getItems();
    	if (items.length > 0) {
    		menu.insertBefore(items[1].getId(), newMenu);
    	} else	menu.add(newMenu);
		menu.add(new Separator(GROUP_MODELLING));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getDeleteAction(site));
    }	
	
   
    
}
