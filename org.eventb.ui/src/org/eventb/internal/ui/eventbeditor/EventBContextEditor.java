/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.PartInitException;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.preferences.ContextEditorPagesPreference;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         Event-B specific form editor for machines.
 */
public class EventBContextEditor extends EventBEditor<IContextRoot> {

	/**
	 * The plug-in identifier of the Event-B Context Editor (value
	 * <code>"org.eventb.internal.ui.editors.EventBContextEditor"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editors.context";

	/**
	 * Default constructor.
	 */
	public EventBContextEditor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		// Get the page from the preference.
		EventBEditorPage[] editorPages = ContextEditorPagesPreference
				.getDefault().createPages();

		for (EventBEditorPage page : editorPages) {
			page.initialize(this);
			try {
				addPage(page);
			} catch (PartInitException e) {
				EventBEditorUtils.debugAndLogError(e,
						"Failed to initialise page " + page.getId());
			}
		}
	}

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

}
