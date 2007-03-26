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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         A subclass of Event-B Editor for editting Event-B machines.
 */
public class EventBMachineEditor extends EventBEditor<IMachineFile> {

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
		EventBEditorPage[] editorPages = EditorPagesRegistry.getDefault().getPages(
				EDITOR_ID);

		for (EventBEditorPage page : editorPages) {
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

	@Override
	protected IMachineFile getRodinFile(IEditorInput input) {
		FileEditorInput editorInput = (FileEditorInput) input;
		IFile inputFile = editorInput.getFile();
		return (IMachineFile) RodinCore.valueOf(inputFile);
	}

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

}
