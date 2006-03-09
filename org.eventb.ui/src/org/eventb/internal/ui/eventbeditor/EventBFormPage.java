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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 * <p>
 * An abstract class which extends </class>FormPage<class>
 * for editing Rodin elements.
 */
public abstract class EventBFormPage
	extends FormPage
{
	
	// Title of the page.
	private String pageTitle;
	
	// The master detail block within the page
	EventBMasterDetailsBlock block;
	
	/**
	 * Contructor.
	 * <p>
	 * @param editor The editor contains this page
	 * @param pageID The ID of the page
	 * @param pageTitle The title of the page
	 * @param pageTabTitle The tab title of the page
	 */
	public EventBFormPage(FormEditor editor, String pageID, String pageTitle, String pageTabTitle) {
		super(editor, pageID, pageTabTitle);  //$NON-NLS-1$
		this.pageTitle = pageTitle;
		block = createMasterDetailsBlock();
	}
	
	/**
	 * Creating the content for the page
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		
		form.setText(pageTitle); //$NON-NLS-1$
		block.createContent(managedForm);
		Composite body = form.getBody();
		
		createMirrorSections(body, managedForm);
	}
	
	
	/**
	 * Create and return the master detail block
	 * @return
	 */
	protected abstract EventBMasterDetailsBlock createMasterDetailsBlock();
	
	
	/**
	 * Creating mirror sections in the page.
	 * @param body The composite to create the mirror sections
	 * @param managedForm The form to create the mirror sections
	 */
	protected abstract void createMirrorSections(Composite body, IManagedForm managedForm);
		
	
	/**
	 * Setting the selection within the page.
	 * @param element A Rodin Element
	 */
	public void setSelection(IRodinElement element) {
		block.setSelection(element);
	}

	
	/**
	 * Return the master-detail block.
	 * @return An Event-B Master-Detail block
	 */
	protected EventBMasterDetailsBlock getMasterDetailsBlock() {return block;}
	
	
	/**
	 * Save the page.
	 */
	public void doSave(IProgressMonitor monitor) {
		this.getMasterDetailsBlock().doSave(true);
		super.doSave(monitor);
	}

}
