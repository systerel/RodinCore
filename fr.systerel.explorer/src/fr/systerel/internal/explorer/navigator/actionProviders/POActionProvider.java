/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.internal.explorer.navigator.actionProviders;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.ICommonActionConstants;

/**
 * An action provider for ProofObligations.
 *
 */
public class POActionProvider extends NavigatorActionProvider {


    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        // forward doubleClick to doubleClickAction
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
              ActionCollection.getOpenAction(site));
    }
	
    @Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(new Separator(GROUP_MODELLING));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getRetryAutoProversAction(site));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getRecalculateAutoStatusAction(site));
    	menu.appendToGroup(GROUP_MODELLING, ActionCollection.getReplayUndischargedAction(site));
    }

}
