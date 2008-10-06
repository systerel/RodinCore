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

/**
 * An action provider for projects.
 *
 */
public class ProjectActionProvider extends NavigatorActionProvider {

	
    @Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(new Separator(GROUP_MODELLING));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getDeleteAction(site));
       	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getRenameProjectAction(site));
      	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getCopyProjectAction(site));
      	
   }	
	
   
    
}
