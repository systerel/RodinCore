/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;

/**
 * An abstract Action Provider. 
 * Clients may overwrite <code>fillActionBars(IActionBars actionBars)</code>
 * and <code>fillContextMenu(IMenuManager menu)</code> from superclass.
 *
 */
public abstract class NavigatorActionProvider extends CommonActionProvider {

	
	public static String GROUP_MODELLING = "modelling";
	
    StructuredViewer viewer;
    
    ICommonActionExtensionSite site;

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
        site = aSite;
		viewer = aSite.getStructuredViewer();
	}

    /**
     * Builds an Open With menu.
     * 
     * @return the built menu
     */
	MenuManager buildOpenWithMenu() {
		MenuManager menu = new MenuManager("Open With",
				ICommonMenuConstants.GROUP_OPEN_WITH);
		ISelection selection = site.getStructuredViewer().getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		menu.add(new OpenWithMenu(EventBUIPlugin.getActivePage(),
				((IInternalElement) obj).getRodinFile().getResource()));
		return menu;
	}

}
