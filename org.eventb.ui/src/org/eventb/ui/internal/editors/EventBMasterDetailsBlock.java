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

package org.eventb.ui.internal.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 * <p>
 * An abstract class for the Master-Detail block for Rodin elements.
 */
public abstract class EventBMasterDetailsBlock
	extends MasterDetailsBlock
	implements IDetailsPageProvider
{

	// Title and description.
	private String title;
	private String description;

	// The Form page contains this block.
	private FormPage page;
	
	// The master part.
	private SectionPart masterPart;
	
	
	/**
	 * Constructor.
	 * <p>
	 * @param page The Form Page contains this block
	 * @param title The title of this block
	 * @param description The description of this block
	 */
	public EventBMasterDetailsBlock(FormPage page, String title, String description) {
		this.page = page;
		this.title = title;
		this.description = description;
	}
	
	
	/**
	 * Getting the detail page for an object.
	 */
	public abstract IDetailsPage getPage(Object object);
	
	
	/**
	 * Setting the selection within the block.
	 * <p>
	 * @param element A Rodin element
	 */
	public abstract void setSelection(IRodinElement element);

	
	/**
	 * Create the master section.
	 * <p>
	 * @param managedForm The form used to create the master section
	 * @param parent The composite parent
	 * @return A new Master section
	 */
	protected abstract SectionPart createMasterSection(IManagedForm managedForm, Composite parent);
	
	
	/**
	 * Create the Toolbar actions
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
				form.reflow(true);
			}
		};
		haction.setChecked(true);
		haction.setToolTipText("Horizontal orientation");
		Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
				form.reflow(true);
			}
		};
		vaction.setChecked(false);
		vaction.setToolTipText("Vertical orientation");
		form.getToolBarManager().add(haction);
		form.getToolBarManager().add(vaction);
	}
	

	/**
	 * Create the Master part (contain the Master section).
	 */
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		masterPart = createMasterSection(managedForm, parent);
		Section section = masterPart.getSection();
		section.setText(title);
		section.setDescription(description);
		section.marginWidth = 10;
		section.marginHeight = 5;
		managedForm.addPart(masterPart);
	}
	
	
	/**
	 * Return the master part of this block
	 * <p>
	 * @return A master part
	 */
	protected SectionPart getMasterPart() {return masterPart;}
	
	
	/**
	 * Return the Form page container of this block.
	 * <p>
	 * @return A Form page
	 */
	protected FormPage getPage() {return page;} 

	
	/**
	 * Dynamically register the detail pages by setting the page provider.
	 */
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageLimit(1); // Set this to 1 in order to cope with changing name
		detailsPart.setPageProvider(this);
	}

	/**
	 * Return the page key for each object as the object itself
	 * (which will be used to create the detail page).
	 */
	public Object getPageKey(Object object) {return object;}
	
	
	/**
	 * Save the information contains in the block.
	 * <p>
	 * @param onSave <code>true</code> if the call is made from a save action 
	 */
	public void doSave(boolean onSave) {
		if (detailsPart != null) {
			EventBDetailsSection page = ((EventBDetailsSection) this.detailsPart.getCurrentPage());
			if (page != null) page.doSave(onSave);
		}
	}

	
}
