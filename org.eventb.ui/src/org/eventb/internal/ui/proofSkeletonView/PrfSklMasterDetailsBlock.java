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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

/**
 * MasterDetailsBlock for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklMasterDetailsBlock extends MasterDetailsBlock {

	protected PrfSklMasterPart masterPart;

	public PrfSklMasterDetailsBlock() {
		// Do nothing
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		masterPart = new PrfSklMasterPart(parent);
		managedForm.addPart(masterPart);
		managedForm.setInput(DefaultInput.getDefault());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Do nothing
	}

	@Override
	protected void registerPages(DetailsPart part) {
		part.setPageProvider(PrfSklDetailsPageProvider.getDefault());
	}

	/**
	 * Get the master part TreeViewer.
	 * 
	 * @return the master part TreeViewer.
	 */
	public TreeViewer getViewer() {
		return masterPart.getViewer();
	}


	public void switchOrientation() {
		if (sashForm.getOrientation() == SWT.VERTICAL) {
			sashForm.setOrientation(SWT.HORIZONTAL);
		} else {
			sashForm.setOrientation(SWT.VERTICAL);
		}
	}

}
