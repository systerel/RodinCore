/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

public class PrefixElementName implements IEditorActionDelegate {

	//TODO in the longer term all subclasses should disappear and be replaced by instances

	private IEventBEditor<?> editor;

	private final IInternalElementType<?> elementType;

	private final String dialogTitle;

	private final String dialogMessage;
	
	public PrefixElementName(IInternalElementType<?> elementType,
			String dialogTitle, String dialogMessage) {
		this.elementType = elementType;
		this.dialogTitle = dialogTitle;
		this.dialogMessage = dialogMessage;
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editor = (IEventBEditor<?>) targetEditor;
	}

	public void setPrefix(IInternalElementType<?> type, String dialogTitle,
			String message) {
		final IInternalElement root = editor.getRodinInput();
		final String oldPrefix = UIUtils.getAutoNamePrefix(root, type);
		final Shell shell = editor.getSite().getShell();
		final InputDialog dialog = new InputDialog(shell, dialogTitle, message,
				oldPrefix, null);
		dialog.open();
		final String newPrefix = dialog.getValue();
		if (newPrefix != null && !newPrefix.equals(oldPrefix)) {
			UIUtils.setAutoNamePrefix(root, type, newPrefix);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// Do nothing
	}

	public void run(IAction action) {
		setPrefix(elementType, dialogTitle, dialogMessage);
	}
	
	public IInternalElementType<?> getElementType(){
		return elementType;
	}
}
