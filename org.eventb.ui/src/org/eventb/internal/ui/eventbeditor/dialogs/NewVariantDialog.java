/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of attributes or names
 */
public class NewVariantDialog extends EventBDialog {

	private String expressionResult;

	private IEventBInputText expressionText;

	private String message;

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor that made the call to this dialog
	 * @param root
	 *            the root element to which variants will be added
	 * @param parentShell
	 *            The parent shell of the dialog
	 * @param title
	 *            The title of the dialog
	 * @param message
	 *            The text message of the dialog
	 */
	public NewVariantDialog(IEventBEditor<IMachineRoot> editor,
			IMachineRoot root, Shell parentShell, String title, String message) {
		super(parentShell, root, title);
		this.message = message;
		expressionText = null;
		expressionResult = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		setDebugBackgroundColor();
		setFormGridLayout(getBody(), 2);
		setFormGridData();

		createLabel(getBody(), message);
		expressionText = createContentInputText(getBody());
		getProposalAdapter(IVariant.ELEMENT_TYPE, EXPRESSION_ATTRIBUTE,
				expressionText);
		select(expressionText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			expressionResult = null;
		} else if (buttonId == IDialogConstants.OK_ID) {
			expressionResult = translate(expressionText);
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
		return expressionResult;
	}

	@Override
	public boolean close() {
		if (expressionText != null)
			expressionText.dispose();
		return super.close();
	}

}
