/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eventb.internal.ui.obligationexplorer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;

/**
 * Implementation of the expand all action in the obligation explorer
 * 
 * @author Laurent Voisin
 */
public class ObligationsExpandAllAction implements IViewActionDelegate {

	private ObligationExplorer fView;

	public void init(IViewPart view) {
		fView = (ObligationExplorer) view;
	}

	public void run(IAction action) {
		fView.getTreeViewer().expandAll();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
