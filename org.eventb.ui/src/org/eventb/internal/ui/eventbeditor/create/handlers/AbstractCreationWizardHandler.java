/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.create.handlers;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author "Nicolas Beauger"
 * 
 */
public abstract class AbstractCreationWizardHandler extends AbstractHandler {

	protected IEventBEditor<?> editor;

	protected abstract void openCreationWizard();
	
	@Override
	public void setEnabled(Object evaluationContext) {
		super.setEnabled(evaluationContext);
		final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (ww == null) {
			return;
		}
		final IWorkbenchPage page = ww.getActivePage();
		if (page == null) {
			return;
		}
		final IEditorPart activeEditor = page.getActiveEditor();
		if (activeEditor instanceof IEventBEditor<?>) {
			editor = (IEventBEditor<?>) activeEditor;
		}
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (checkAndShowReadOnly(editor.getRodinInput())) {
			return null;
		}
		openCreationWizard();
		return null;
	}

}
