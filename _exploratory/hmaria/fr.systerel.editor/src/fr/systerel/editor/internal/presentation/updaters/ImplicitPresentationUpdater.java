/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import fr.systerel.editor.internal.editors.RodinEditor;

/**
 * An adapter to update the presentation when the implicit children are
 * recalculated after the user "saves" a model. It iterates on all opened
 * RodinEditors and asks them to recalculate their presentation.
 * 
 * @author "Thomas Muller"
 */
public class ImplicitPresentationUpdater extends AdapterImpl {

	@Override
	public void notifyChanged(Notification notification) {
		final IWorkbenchWindow[] wws = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (IWorkbenchWindow ww : wws) {
			final IWorkbenchPage[] pages = ww.getPages();
			for (IWorkbenchPage p : pages) {
				final IEditorReference[] editorReferences = p
						.getEditorReferences();
				for (IEditorReference ref : editorReferences) {
					final IEditorPart ed = ref.getEditor(false);
					if (ed != null && ed instanceof RodinEditor) {
						final RodinEditor rodinEditor = (RodinEditor) ed;
						rodinEditor.resync(null);
					}
				}
			}
		}
	}
}
