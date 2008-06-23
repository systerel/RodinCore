/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of attributes or names
 */
public class NewVariantInputDialog extends EventBInputDialog {

	private String expression;

	private IEventBInputText expressionText;

	private String message;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            The parent shell of the dialog
	 * @param title
	 *            The title of the dialog
	 * @param message
	 *            The text message of the dialog
	 */
	public NewVariantInputDialog(Shell parentShell, String title,
			String message) {
		super(parentShell, title);
		this.message = message;
		expressionText = null;
		expression = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		Composite body = scrolledForm.getBody();
		if (EventBEditorUtils.DEBUG) {
			body.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_CYAN));
		}

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());

		expressionText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 100;
		Text textWidget = expressionText.getTextWidget();
		textWidget.setLayoutData(gd);
		textWidget.addModifyListener(new DirtyStateListener());
		textWidget.selectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			expression = null;
		} else if (buttonId == IDialogConstants.OK_ID) {
			Text widget = expressionText.getTextWidget();
			expression = Text2EventBMathTranslator.translate(widget.getText());
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the list of attributes or names.
	 * <p>
	 * 
	 * @return The list of the text attributes that the user entered
	 */
	public String getExpression() {
		return expression;
	}

	@Override
	public boolean close() {
		if (expressionText != null)
			expressionText.dispose();
		return super.close();
	}

}
