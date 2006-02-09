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
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 * <p>
 * An implementation of Master Details block for Constants (Rodin elements).
 */
public class EventMasterDetailsBlock 
	extends EventBMasterDetailsBlock 
{
	
	// Title and description of the section.
	private final static String TITLE = "Events";
	private final static String DESCRIPTION = "The list contains event elements from the model whose details are editable on the right";
	

	/**
	 * Contructor.
	 * <p>
	 * @param page the form page which is the parent of this master-detail block
	 */
	public EventMasterDetailsBlock(FormPage page) {
		super(page, TITLE, DESCRIPTION);
	}
	

	/**
	 * Method to create the master section.
	 */
	protected SectionPart createMasterSection(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		return new EventMasterSection(managedForm, parent, toolkit, Section.TITLE_BAR | Section.DESCRIPTION, this);
	}


	/**
	 * Method for creating detail pages according to the input
	 * (selected in the master section) object.
	 */
	public IDetailsPage getPage(Object object) {
		if (object instanceof IEvent)
			return new EventDetailsSection(this, (IEvent) object);
		if (object instanceof IVariable)
			return new VariableDetailsSection(this, (IVariable) object);
		if (object instanceof IGuard)
			return new GuardDetailsSection(this, (IGuard) object);
		if (object instanceof IAction)
			return new ActionDetailsSection(this, (IAction) object);
		return null;
	}


	/**
	 * Pass the selection to the master section.
	 */
	public void setSelection(IRodinElement element) {
		((EventBTreePartWithButtons) this.getMasterPart()).setSelection(element);
		return;
	}

}