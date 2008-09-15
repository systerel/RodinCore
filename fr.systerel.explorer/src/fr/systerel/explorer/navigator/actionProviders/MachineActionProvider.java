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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorerActionGroup;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.rodinp.core.IRodinProject;

/**
 * @author Maria Husmann
 *
 */
public class MachineActionProvider extends CommonActionProvider {

	
    Action doubleClickAction;

    StructuredViewer viewer;

    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
		viewer = aSite.getStructuredViewer();
		makeActions();
//		hookDoubleClickAction();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        // forward doubleClick to doubleClickAction
        actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
              doubleClickAction);
    }
   
    @Override
	public void fillContextMenu(IMenuManager menu) {
    	menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, doubleClickAction);
    	menu.appendToGroup(ICommonMenuConstants.GROUP_NEW, ProjectExplorerActionGroup.newComponentAction);
    	menu.appendToGroup(ICommonMenuConstants.GROUP_NEW, ProjectExplorerActionGroup.newProjectAction);
    }	
    
	/**
	 * Create the actions.
	 */
	private void makeActions() {

		// Double click to link with editor
		doubleClickAction = new Action("Open") {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (!(obj instanceof IRodinProject)) {
					if (obj instanceof IPSStatus) {
						selectPO((IPSStatus) obj);
					} else {
						UIUtils.linkToEventBEditor(obj);				
					}
				}
			}
		};
	}

	/**
	 * Associate the double click action.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	void selectPO(IPSStatus ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
	}
	

}
