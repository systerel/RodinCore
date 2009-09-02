/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - introduced read only elements through new super class
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.RodinDBException;

public class NewVariant extends AbstractNewActionDelegate<IMachineRoot> {

	@Override
	public void runAction(IAction action) {
		IMachineRoot root = editor.getRodinInput();
		int length;
		try {
			length = root.getChildrenOfType(IVariant.ELEMENT_TYPE).length;
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}
		if (length != 0)
			MessageDialog.openError(editor.getEditorSite().getShell(),
					"Variant Exist", "Variant already exists in this machine");
		else
			EventBEditorUtils.newVariant(editor);
	}

}
