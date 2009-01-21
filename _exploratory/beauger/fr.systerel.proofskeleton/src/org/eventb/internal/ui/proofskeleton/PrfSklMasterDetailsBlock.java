/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofskeleton;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

/**
 * MasterDetailsBlock for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklMasterDetailsBlock extends MasterDetailsBlock {

	private static final IDetailsPageProvider prfSklDetailsPageProvider = 
		new PrfSklDetailsPageProvider();

	protected PrfSklMasterPart masterPart;

	public PrfSklMasterDetailsBlock() {
		// Do nothing
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		masterPart = new PrfSklMasterPart(parent);
		managedForm.addPart(masterPart);
		managedForm.setInput(DefaultMasterInput.getDefault());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Do nothing
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(prfSklDetailsPageProvider);
	}

	/**
	 * Default master input set to the IManagedForm when creating the master
	 * part. Can be set later from outside the class, when the current input is
	 * no more accurate, and no other relevant input can be found.
	 * <p>
	 * It is a singleton implementation, thus static method
	 * <code>getDefault()</code> must be called.
	 * 
	 * @author Nicolas Beauger
	 * 
	 */
	public static final class DefaultMasterInput {
		private static DefaultMasterInput instance;

		private DefaultMasterInput() {
			// Singleton
		}

		public static DefaultMasterInput getDefault() {
			if (instance == null) {
				instance = new DefaultMasterInput();
			}
			return instance;
		}
	}

	/**
	 * Get the master part TreeViewer.
	 * 
	 * @return the master part TreeViewer.
	 */
	public TreeViewer getViewer() {
		return masterPart.getViewer();
	}
}
