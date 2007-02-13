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
import org.eventb.core.IContextFile;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         Event-B specific form editor for machines.
 */
public class EventBContextEditor extends EventBEditor<IContextFile> {

	/**
	 * The plug-in identifier of the Event-B Context Editor (value
	 * <code>"org.eventb.internal.ui.editors.EventBContextEditor"</code>).
	 */
	public static final String EDITOR_ID = EventBUIPlugin.PLUGIN_ID
			+ ".editors.context";

	// Set of different Mirror Pages
	private EventBMirrorPage theoremMirrorPage;

	private EventBMirrorPage axiomMirrorPage;

	private EventBMirrorPage carrierSetMirrorPage;

	private EventBMirrorPage constantMirrorPage;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class required) {
		if (ITheoremMirrorPage.class.equals(required)) {
			if (theoremMirrorPage == null) {
				theoremMirrorPage = new TheoremMirrorPage(this);
				return theoremMirrorPage;
			}
		}
		if (IAxiomMirrorPage.class.equals(required)) {
			if (axiomMirrorPage == null) {
				axiomMirrorPage = new AxiomMirrorPage(this);
				return axiomMirrorPage;
			}
		}
		if (ICarrierSetMirrorPage.class.equals(required)) {
			if (carrierSetMirrorPage == null) {
				carrierSetMirrorPage = new CarrierSetMirrorPage(this);
				return carrierSetMirrorPage;
			}
		}
		if (ICarrierSetMirrorPage.class.equals(required)) {
			if (constantMirrorPage == null) {
				constantMirrorPage = new CarrierSetMirrorPage(this);
				return constantMirrorPage;
			}
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditor#dispose()
	 */
	@Override
	public void dispose() {
		if (theoremMirrorPage != null)
			theoremMirrorPage.dispose();
		if (axiomMirrorPage != null)
			axiomMirrorPage.dispose();
		if (carrierSetMirrorPage != null)
			carrierSetMirrorPage.dispose();
		if (constantMirrorPage != null)
			constantMirrorPage.dispose();
		super.dispose();
	}

	@Override
	protected IContextFile getRodinFile(IEditorInput input) {
		FileEditorInput editorInput = (FileEditorInput) input;
		IFile inputFile = editorInput.getFile();
		return (IContextFile) RodinCore.valueOf(inputFile);
	}

	@Override
	public String getEditorId() {
		return EDITOR_ID;
	}

}
