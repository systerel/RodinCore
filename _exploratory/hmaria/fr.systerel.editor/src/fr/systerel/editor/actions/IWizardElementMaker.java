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
package fr.systerel.editor.actions;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IEventBRoot;

/**
 * This interface allows one to define a way for wizard to create elements for a
 * given root and editor operation history. This is typically used in the case
 * of element creation with the wizard that stays open.
 */
public interface IWizardElementMaker {

	IEditorPart getEditor();

	Shell getShell();

	IRodinHistory getHistory();

	void addValues(IEventBDialog dialog);
	
	IEventBRoot getRoot();
	
	void setDialog(IEventBDialog dialog);

	IEventBDialog getDialog();

	IUndoContext getUndoContext();
	
}
