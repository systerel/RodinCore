/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.eventbeditor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author htson
 *         <p>
 *         This is the base abstract class for implementing an Event-B Editor
 *         Page. This extends the standard
 *         {@link org.eclipse.ui.forms.editor.FormPage}. Plug-in writers
 *         suppose to extend this class when adding a new page to the Event-B
 *         Editor.
 * @since 1.0
 */
public abstract class EventBEditorPage extends FormPage {

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
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
	}

	/**
	 * Returns the parent event-B editor.
	 * 
	 * @return parent editor instance
	 */
	public IEventBEditor<?> getEventBEditor() {
		return (IEventBEditor<?>) this.getEditor();
	}
	
}
