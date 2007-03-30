/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.eventbeditor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author htson
 *         <p>
 *         This is the base abstract class for implementing an Event-B Editor
 *         Page. This extends the standard
 *         {@link org.eclipse.ui.forms.editor.FormPage}. Plug-in writers
 *         suppose to extend this class when adding a new page to the Event-B
 *         Editor.
 */
public abstract class EventBEditorPage extends FormPage {

	// Private field for the page title.
	private String pageTitle;
	
	/**
	 * The constructor.
	 * <p>
	 * 
	 * @param id
	 *            an unique ID for the page
	 * @param tabTitle
	 *            the tab title
	 * @param pageTitle
	 *            the page title
	 */
	public EventBEditorPage(String id, String tabTitle,
			String pageTitle) {
		super(id, tabTitle);
		this.pageTitle = pageTitle;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();

		form.setText(pageTitle);
	}

	/**
	 * Returns the parent event-B editor.
	 * 
	 * @return parent editor instance
	 */
	protected IEventBEditor getEventBEditor() {
		return (IEventBEditor) this.getEditor();
	}
	
}
