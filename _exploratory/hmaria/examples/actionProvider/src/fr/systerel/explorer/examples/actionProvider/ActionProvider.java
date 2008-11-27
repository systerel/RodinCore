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

package fr.systerel.explorer.examples.actionProvider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import fr.systerel.explorer.IElementNode;

/**
 * This action provider adds a simple action that opens a dialog when clicking
 * on the nodes labeled "Invariants", "Variables", etc.
 * 
 */
public class ActionProvider extends CommonActionProvider {

	private StructuredViewer viewer;

	public ActionProvider() {
		// do nothing
	}

    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
		viewer = aSite.getStructuredViewer();
		doubleClickAction = getAction(viewer);
  }
	
	
	Action doubleClickAction;

	
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        // forward doubleClick to doubleClickAction
       
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
        		doubleClickAction);
    }
	
	public void fillContextMenu(IMenuManager menu) {
    	//add the action the context menu.
    	menu.add(new Separator("example"));
    	menu.appendToGroup("example", doubleClickAction);
    	
    }	
	
    
	/**
	 * A simple action that opens a dialog when clicking on the nodes labeled
	 * "Invariants", "Variables", etc.
	 */
	public static Action getAction(final StructuredViewer aViewer) {
		Action doubleClickAction = new Action("Example Action") {
			@Override
			public void run() {
				ISelection selection = aViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IElementNode) {
					MessageDialog.openInformation(null, "Message", 
					"You clicked on the " +((IElementNode) obj).getLabel() +" node of " 
					+ ((IElementNode) obj).getParent().getComponentName());
				}
			}
		};
		return doubleClickAction;
		
	}
    
	
}
