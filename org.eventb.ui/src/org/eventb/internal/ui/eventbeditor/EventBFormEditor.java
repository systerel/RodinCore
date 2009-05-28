/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.rodinp.core.IRodinFile;

/**
 * Abstract Event-B form editor for machines, contexts, proofs.
 */
public abstract class EventBFormEditor extends
		FormEditor implements IResourceChangeListener {

	/**
	 * Constructor.
	 */
	public EventBFormEditor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this,
				IResourceChangeEvent.PRE_DELETE
						| IResourceChangeEvent.PRE_CLOSE);
		
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResource resource = event.getResource();
		if (resource.getType() != IResource.PROJECT)
			return;
		final IProject myProject = getRodinInputFile().getRodinProject()
				.getProject();
		if (!resource.equals(myProject))
			return;
		final EventBFormEditor editor = this;
		final Display display = PlatformUI.getWorkbench().getDisplay();
		final IWorkbenchPage page = editor.getSite().getWorkbenchWindow()
				.getActivePage();
		if (page != null) {
			display.syncExec(new Runnable() {
				public void run() {
					page.closeEditor(editor, true);
				}
			});
		}
		return;
	}
	
	/**
	 * Get the Rodin file associated with this editor.
	 * <p>
	 * 
	 * @return a handle to a Rodin file
	 */
	public abstract IRodinFile getRodinInputFile();

}
