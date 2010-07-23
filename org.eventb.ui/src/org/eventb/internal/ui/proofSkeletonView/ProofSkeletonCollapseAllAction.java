/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * CollapseAll action. Collapses the master part tree viewer of the
 * ProofSkeletonView.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonCollapseAllAction implements IViewActionDelegate {

	private ProofSkeletonView prfSklView;
	
	@Override
	public void init(IViewPart view) {
		assert view instanceof ProofSkeletonView;
		this.prfSklView = (ProofSkeletonView) view;
	}

	@Override
	public void run(IAction action) {
		prfSklView.changeExpansionState(false);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}
