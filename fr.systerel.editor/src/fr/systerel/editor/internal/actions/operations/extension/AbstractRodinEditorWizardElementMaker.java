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
package fr.systerel.editor.internal.actions.operations.extension;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IEventBRoot;

import fr.systerel.editor.actions.IEventBDialog;
import fr.systerel.editor.actions.IRodinHistory;
import fr.systerel.editor.actions.IWizardElementMaker;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.editors.RodinEditorUtils;

public abstract class AbstractRodinEditorWizardElementMaker implements
		IWizardElementMaker {

	private final IEditorPart editor;
	private final Shell shell;
	private final IEventBRoot root;
	private IEventBDialog dialog;

	
	public AbstractRodinEditorWizardElementMaker(IEditorPart editor,
			IEventBRoot root) {
		this.editor = editor;
		this.shell = RodinEditorUtils.getShell();
		this.root = root;
	}

	@Override
	public IEditorPart getEditor() {
		return editor;
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	public IEventBRoot getRoot() {
		return root;
	}

	@Override
	public IRodinHistory getHistory() {
		return RodinEditorHistory.getInstance();
	}

	@Override
	public IUndoContext getUndoContext() {
		return OperationFactory.getRodinFileUndoContext(getRoot());
	}

	@Override
	public void setDialog(IEventBDialog dialog) {
		this.dialog = dialog;
	}

	public IEventBDialog getDialog() {
		return dialog;
	}

}
