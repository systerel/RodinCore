/*******************************************************************************
 * Copyright (c) 2011 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.pm.IUserSupport;

/**
 * @author Nicolas Beauger
 * 
 */
public class MultiLineInputDialog extends EventBInputDialog {

	public MultiLineInputDialog(Shell parentShell, String title,
			String message, String initialValue, IInputValidator validator,
			IUserSupport userSupport) {
		super(parentShell, title, message, initialValue, validator, userSupport);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected int getInputTextStyle() {
		return SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Control control = super.createDialogArea(parent);
		final Text text = getText();
		final GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		data.heightHint = 5 * text.getLineHeight();
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
		text.setLayoutData(data);
		return control;
	}

}
