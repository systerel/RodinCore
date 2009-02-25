/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
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
 * Switch Orientation Action. If master part and details part are currently laid
 * out side by side, they become laid out top to bottom, and conversely.
 * 
 * @author Nicolas Beauger
 * 
 */
public class SwitchOrientationAction implements IViewActionDelegate {

	private ProofSkeletonView prfSklView;

	public void init(IViewPart view) {
		assert view instanceof ProofSkeletonView;
		this.prfSklView = (ProofSkeletonView) view;
	}

	public void run(IAction action) {
		prfSklView.switchOrientation();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

}
