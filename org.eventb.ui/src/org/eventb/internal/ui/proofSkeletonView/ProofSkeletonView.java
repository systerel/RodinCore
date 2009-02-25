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

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

/**
 * ViewPart for displaying proof skeletons.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonView extends ViewPart {

			
	protected PrfSklMasterDetailsBlock masterDetailsBlock;
	private SelectionManager selManager;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final IManagedForm managedForm = new ManagedForm(parent);
		masterDetailsBlock = new PrfSklMasterDetailsBlock();
		masterDetailsBlock.createContent(managedForm);

		selManager = new SelectionManager(this, managedForm);

		getSite().getPage().addPartListener(selManager);

	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(selManager);
		super.dispose();
	}
	
	/**
	 * Expand or collapse the master part tree viewer.
	 * 
	 * @param expand
	 *            expands all when <code>true</code>; collapses all when
	 *            <code>false</code>.
	 */
	public void changeExpansionState(boolean expand) {
		if (expand) {
			masterDetailsBlock.getViewer().expandAll();
		} else {
			masterDetailsBlock.getViewer().collapseAll();
		}
	}

	public void switchOrientation() {
		masterDetailsBlock.switchOrientation();
	}

}
