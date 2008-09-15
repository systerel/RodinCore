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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinFile;

public class POActionProvider extends CommonActionProvider {

	public POActionProvider() {
		// TODO Auto-generated constructor stub
	}
	

	// Action when double clicking.
	Action doubleClickAction;
	StructuredViewer viewer;
	
    @Override
    public void init(ICommonActionExtensionSite aSite) {
        super.init(aSite);
		viewer = aSite.getStructuredViewer();
		makeActions();
		hookDoubleClickAction();
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


	/**
	 * Create the actions.
	 */
	private void makeActions() {
		// Double click to link with editor
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (obj instanceof IRodinFile) {
					UIUtils.linkToEventBEditor(obj);
				}
				else if (obj instanceof IPSStatus) {
					selectPO((IPSStatus) obj);
				}
			}
		};

	}
	void selectPO(IPSStatus ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
	}
 
	
    
}
