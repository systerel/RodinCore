/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ICarrierSet;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 * <p>
 * An implementation of Master Details block for Carrier Sets (Rodin elements)
 */
public class CarrierSetMasterDetailsBlock
	extends EventBMasterDetailsBlock
{

	// Title and description of the section.
	private final static String TITLE = "Carrier Sets";
	private final static String DESCRIPTION = "The list contains carrier set elements from the context whose details are editable on the right";

	// The table part with buttons (master section).
	EventBTablePartWithButtons spart;
	
	
	/**
	 * Contructor.
	 * <p>
	 * @param page the form page which is the parent of this master-detail block
	 */
	public CarrierSetMasterDetailsBlock(FormPage page) {
		super(page, TITLE, DESCRIPTION);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.MasterDetailsBlock#createContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);
		int [] weights = {30, 70};
		this.sashForm.setWeights(weights);
	}

	/**
	 * Method to create the master section.
	 */
	protected SectionPart createMasterSection(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		spart = new CarrierSetMasterSection(managedForm, parent, toolkit, Section.TITLE_BAR | Section.DESCRIPTION, this);
		
		return spart;
	}

	
	/**
	 * Method for creating detail pages according to the input
	 * (selected in the master section) object.
	 */
	public IDetailsPage getPage(Object object) {
		if (object instanceof ICarrierSet)
			return new CarrierSetDetailsSection(this, (ICarrierSet) object);
		return null;
	}


	/**
	 * Pass the selection to the master section.
	 */
	public void setSelection(IRodinElement element) {
		spart.setSelection(element);
	}

}