/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
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
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.preferences.MachineEditorPagesPreference;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         A subclass of Event-B Editor for editting Event-B machines.
 */
public class EventBMachineEditor extends EventBEditor<IMachineRoot> {

	/**
	 * The plug-in identifier of the Event-B Machine Editor (value
	 * <code>"org.eventb.internal.ui.editors.EventBMachineEditor"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editors.machine";

	/**
	 * Default constructor.
	 */
	public EventBMachineEditor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		// Get the pages from the preference.
		EventBEditorPage[] editorPages = MachineEditorPagesPreference
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
