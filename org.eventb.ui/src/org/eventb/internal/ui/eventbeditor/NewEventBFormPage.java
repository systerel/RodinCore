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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author htson
 * <p>
 * An abstract class which extends </class>FormPage<class>
 * for editing Rodin elements.
 */
public abstract class NewEventBFormPage
	extends FormPage
{
	
	// Title of the page.
	private String pageTitle;
	
	/**
	 * Contructor.
	 * <p>
	 * @param editor The editor contains this page
	 * @param pageID The ID of the page
	 * @param pageTitle The title of the page
	 * @param pageTabTitle The tab title of the page
	 */
	public NewEventBFormPage(FormEditor editor, String pageID, String pageTitle, String pageTabTitle) {
		super(editor, pageID, pageTabTitle);  //$NON-NLS-1$
		this.pageTitle = pageTitle;
	}
	
	/**
	 * Creating the content for the page
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		
		form.setText(pageTitle); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		
		NewEventBTablePartWithButtons part = createMasterSection(managedForm, body, Section.TITLE_BAR | Section.DESCRIPTION, (EventBEditor) this.getEditor());
		managedForm.addPart(part);
		
		createMirrorSections(body, managedForm);
		
	}
	
	/**
	 * Creating mirror sections in the page.
	 * @param body The composite to create the mirror sections
	 * @param managedForm The form to create the mirror sections
	 */
	protected abstract void createMirrorSections(Composite body, IManagedForm managedForm);
		
	
	protected abstract NewEventBTablePartWithButtons createMasterSection(IManagedForm managedForm, Composite parent, int style, EventBEditor editor);
	
	
	/**
	 * Setting the selection within the page.
	 * @param element A Rodin Element
	 */
//	public void setSelection(IRodinElement element) {
//		block.setSelection(element);
//	}

}
