/*******************************************************************************
 * Copyright (c) 2008 Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Duesseldorf - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.ui.projectexplorer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class ExplorerUtilities {
	/**
	 * Link the current object to an editor for the specific kind of file.
	 * <p>
	 * 
	 * @param obj
	 *            the object (e.g. an internal element or a Rodin file)
	 */

	public static void linkToEditor(final Object obj) {

		IRodinFile component;

		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) UIUtils.getOpenable(obj);
			if (component == null)
				return;
			try {
				IEditorInput fileInput = new FileEditorInput(component
						.getResource());

				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(
								component.getCorrespondingResource().getName());

				IEditorPart editor = EventBUIPlugin.getActivePage().openEditor(
						new FileEditorInput(component.getResource()),
						desc.getId());
				editor.getSite().getSelectionProvider().setSelection(
						new StructuredSelection(obj));

			} catch (PartInitException e) {
				String errorMsg = "Error open Editor";
				MessageDialog.openError(null, null, errorMsg);
				EventBUIPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
								errorMsg, e));
			}
		}
		return;
	}
}
