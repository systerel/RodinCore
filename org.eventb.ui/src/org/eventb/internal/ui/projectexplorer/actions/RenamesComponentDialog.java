/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.projectexplorer.actions;

import static org.eventb.internal.ui.utils.Messages.dialogs_new_component_title;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class RenamesComponentDialog extends InputDialog {

	private Button checkbox;
	private boolean selected;

	/**
	 * Creates an input dialog to rename component. The dialog has a text field
	 * for the new name and a checkbox to update references.
	 * 
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell
	 * @param initialValue
	 *            the initial input value, or <code>null</code> if none
	 *            (equivalent to the empty string)
	 * @param initialSelected
	 *            the initial selected value
	 * @param validator
	 *            an input validator, or <code>null</code> if none
	 */
	public RenamesComponentDialog(Shell parentShell, String initialValue,
			boolean initialSelected, IInputValidator validator) {
		super(parentShell, dialogs_new_component_title,
				dialogs_new_component_title, initialValue, validator);
		selected = initialSelected;
	}

	public boolean updateReferences() {
		return selected;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText("Update references");
		checkbox.setSelection(selected);
		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			selected = checkbox.getSelection();
		} else {
			selected = false;
		}
		super.buttonPressed(buttonId);
	}
}
