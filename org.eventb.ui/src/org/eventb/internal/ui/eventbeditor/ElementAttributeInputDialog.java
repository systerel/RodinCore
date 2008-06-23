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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of attributes or names
 */
public class ElementAttributeInputDialog extends EventBInputDialog {

	// The default prefix
	private String defaultPrefix;

	private Collection<String> attributes;

	private Collection<IEventBInputText> texts;

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
	 * @param defaultPrefix
	 *            The default prefix of for the attributes
	 */
	public ElementAttributeInputDialog(Shell parentShell, String title,
			String message, String defaultPrefix) {
		super(parentShell, title);
		this.message = message;
		this.defaultPrefix = defaultPrefix;
		texts = new ArrayList<IEventBInputText>();
		attributes = new ArrayList<String>();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "&More", false);

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
		if (EventBEditorUtils.DEBUG)
			body.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());

		EventBMath text = new EventBMath(toolkit.createText(body, defaultPrefix));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 100;
		text.getTextWidget().setLayoutData(gd);
		text.getTextWidget().addModifyListener(new DirtyStateListener());
		texts.add(text);
		text.getTextWidget().selectAll();
		text.getTextWidget().setFocus();
		
		label = toolkit.createLabel(body, message);
		// label.setLayoutData(new GridData());

		text = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 100;
		text.getTextWidget().setLayoutData(gd);
		text.getTextWidget().addModifyListener(new DirtyStateListener());
		texts.add(text);

		label = toolkit.createLabel(body, message);
		label.setText(message);
		// label.setLayoutData(new GridData());

		text = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 100;
		text.getTextWidget().setLayoutData(gd);
		text.getTextWidget().addModifyListener(new DirtyStateListener());
		texts.add(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			attributes = new HashSet<String>();
		} else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(scrolledForm.getBody(), message);
			label.setLayoutData(new GridData());

			EventBMath text = new EventBMath(toolkit.createText(scrolledForm
					.getBody(), ""));
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			texts.add(text);

			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			toolkit.paintBordersFor(scrolledForm.getBody());
			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			attributes = new ArrayList<String>();
			for (IEventBInputText text : texts) {
				String inputText = text.getTextWidget().getText();
				if (dirtyTexts.contains(text.getTextWidget()))
					attributes.add(inputText);
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the list of attributes or names.
	 * <p>
	 * 
	 * @return The list of the text attributes that the user entered
	 */
	public Collection<String> getAttributes() {
		return attributes;
	}

	@Override
	public boolean close() {
		for (IEventBInputText text : texts) text.dispose();
		return super.close();
	}

}
