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


package fr.systerel.explorer.masterDetails;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;

import fr.systerel.explorer.RodinNavigator;

/**
 * This is the MasterDetailsBlock for the Navigator.
 *
 */
public class NavigatorMasterDetailsBlock extends MasterDetailsBlock {

	protected NavigatorMasterPart masterPart;
	private static final IDetailsPageProvider navigatorDetailsPageProvider = 
		new NavigatorDetailsPageProvider();
	private RodinNavigator navigator;

	public NavigatorMasterDetailsBlock(RodinNavigator navigator) {
		this.navigator = navigator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		masterPart = new NavigatorMasterPart(navigator, parent);
		managedForm.addPart(masterPart);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		//do nothing

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
	 */
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(navigatorDetailsPageProvider);

	}
	

}
