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

package org.eventb.internal.ui.eventbeditor.actions;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IEventBRoot;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author "Nicolas Beauger"
 * 
 */
public abstract class AbstractNewActionDelegate<T extends IEventBRoot>
		implements IEditorActionDelegate {

	protected IEventBEditor<T> editor;

	protected abstract void runAction(IAction action);

	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<T>) targetEditor;
	}

	public void run(IAction action) {
		if (checkAndShowReadOnly(editor.getRodinInput())) {
			return;
		}
		runAction(action);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

}
