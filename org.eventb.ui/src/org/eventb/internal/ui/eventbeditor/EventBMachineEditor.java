/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.ui.PartInitException;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         A subclass of Event-B Editor for editting Event-B machines.
 */
public class EventBMachineEditor extends EventBEditor {

	// Set of mirror pages.
	private EventBMirrorPage invariantMirrorPage;

	private EventBMirrorPage theoremMirrorPage;

	private EventBMirrorPage eventMirrorPage;

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
	protected void addPages() {
		EventBEditorPage[] pages = EditorPagesRegistry.getDefault().getPages(
				EDITOR_ID);

		for (EventBEditorPage page : pages) {
			page.initialize(this);
			try {
				addPage(page);
			} catch (PartInitException e) {
				if (EventBEditorUtils.DEBUG)
					e.printStackTrace();
				UIUtils.log(e, "Failed to initialise page " + page.getId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class required) {
		if (IInvariantMirrorPage.class.equals(required)) {
			if (invariantMirrorPage == null) {
				invariantMirrorPage = new InvariantMirrorPage(this);
				return invariantMirrorPage;
			}
		}
		if (ITheoremMirrorPage.class.equals(required)) {
			if (theoremMirrorPage == null) {
				theoremMirrorPage = new TheoremMirrorPage(this);
				return theoremMirrorPage;
			}
		}
		if (IEventMirrorPage.class.equals(required)) {
			if (eventMirrorPage == null) {
				eventMirrorPage = new EventMirrorPage(this);
				return eventMirrorPage;
			}
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#dispose()
	 */
	public void dispose() {
		if (invariantMirrorPage != null)
			invariantMirrorPage.dispose();
		if (theoremMirrorPage != null)
			theoremMirrorPage.dispose();
		if (eventMirrorPage != null)
			eventMirrorPage.dispose();
		super.dispose();
	}

}
