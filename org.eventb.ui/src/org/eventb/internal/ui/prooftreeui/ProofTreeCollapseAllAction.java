/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPage;

/**
 * Implementation of the collapse all action in the obligation explorer
 * 
 * @author Laurent Voisin
 */
public class ProofTreeCollapseAllAction implements IViewActionDelegate {

	private ProofTreeUI fView;

	public void init(IViewPart view) {
		fView = (ProofTreeUI) view;
	}

	public void run(IAction action) {
		IPage currentPage = fView.getCurrentPage();
		if (currentPage != null && currentPage instanceof ProofTreeUIPage) {
			((ProofTreeUIPage) currentPage).getViewer().collapseAll();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
