/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - add createEventBFormText()
 *******************************************************************************/
package org.eventb.ui.eventbeditor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.ui.IEventBFormText;

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
	 * Returns an Event-B decoration around the given form text.
	 * <p>
	 * This method is provided for backward compatibility with Rodin 2.x and
	 * replaces calls to <code>new EventBFormText(text)</code>, as the class is
	 * no longer published in the API.
	 * </p>
	 * 
	 * @param text
	 *            the form text to decorate
	 * @return an Event-B form text
	 * @since 3.0
	 */
	public static IEventBFormText createEventBFormText(FormText text) {
		return new EventBFormText(text);
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
