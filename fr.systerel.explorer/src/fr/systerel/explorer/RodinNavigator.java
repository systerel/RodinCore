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

package fr.systerel.explorer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.navigator.CommonNavigator;
import org.rodinp.core.RodinCore;

import fr.systerel.explorer.masterDetails.NavigatorMasterDetailsBlock;
import fr.systerel.explorer.model.ModelController;

/**
 * The Navigator for the CommonNavigator framework.
 * There are some customizations for the menus 
 * and the working sets.
 *
 */
public class RodinNavigator extends CommonNavigator {
	
	protected IManagedForm managedForm;

	public RodinNavigator(){
		controller = new ModelController(this);
	}
	
	/**
	 * The Controller of the internal model.
	 */
	@SuppressWarnings("unused")
	private ModelController controller;
	private NavigatorMasterDetailsBlock masterDetailsBlock;
	
	
//	/**
//	 * Take the <code>RodinDB</code> as InitialInput and not the <code>Workspace</code>.
//	 *
//	 */
//	@Override
//	protected IAdaptable getInitialInput() {
//		this.getCommonViewer().refresh();
//		return RodinCore.getRodinDB();
//	}

	/**
	 * Create the master Details Block
	 * From in there super.createPartControl() will be called.
	 */
	@Override
	public void createPartControl(Composite parent) {		

		//create MasterDetailsBlock
		managedForm = new ManagedForm(parent);
		masterDetailsBlock = new NavigatorMasterDetailsBlock(this);
		masterDetailsBlock.createContent(managedForm);
			
	}
	
	/**
	 * This method is used in the NavigatorMasterPage to
	 * create the CommonViewer there. It's not intended to be used anywhere else.
	 * @param parent
	 */
	public void superCreatePartControl(Composite parent) {
		super.createPartControl(parent);
	}

	@Override
	public void dispose() {
		super.dispose();
		managedForm.getForm().dispose();
		for (IFormPart part : managedForm.getParts()) {
			part.dispose();
		}
	}
	
	
}
