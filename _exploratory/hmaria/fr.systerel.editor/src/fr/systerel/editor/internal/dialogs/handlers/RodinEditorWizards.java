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
package fr.systerel.editor.internal.dialogs.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.actions.IWizardElementMaker;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.handlers.AbstractEditorHandler;

/**
 * Used to launch wizards.
 */
public abstract class RodinEditorWizards<T extends IEventBDialog> extends
		AbstractEditorHandler {

	protected T dialog;

	public void run() {
		if (dialog == null) {
			return;
		}
		final Dialog jdialog = (Dialog) dialog;
		jdialog.open();
		if (jdialog.getReturnCode() == InputDialog.CANCEL)
			return; // Cancel
		handleResult();
	}

	public void handleResult() {
		final IWizardElementMaker elementMaker = dialog.getElementMaker();
		elementMaker.addValues(dialog);
	}

	public abstract void createDialog(ExecutionEvent event,
			final RodinEditor editor);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final RodinEditor rodinEditor = getActiveRodinEditor(event);
		if (rodinEditor != null) {
			createDialog(event, rodinEditor);
			run();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
