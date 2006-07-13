/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of element name with content.
 */
public class ElementNameContentInputDialog extends Dialog {
	private String defaultNamePrefix;

	private Collection<String> names;

	private Collection<String> contents;

	private Collection<IEventBInputText> nameTexts;

	private Collection<IEventBInputText> contentTexts;

	private ScrolledForm scrolledForm;

	private String title;

	private String message;

	private FormToolkit toolkit;

	private int counter;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            The parent shell of the dialog
	 * @param title
	 *            The title of the dialog
	 * @param message
	 *            The message of the dialog
	 * @param defaultNamePrefix
	 *            The default name prefix
	 * @param counter
	 *            The start counter for the elements.
	 */
	public ElementNameContentInputDialog(Shell parentShell, String title,
			String message, String defaultNamePrefix, int counter) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.defaultNamePrefix = defaultNamePrefix;
		this.counter = counter;
		names = new ArrayList<String>();
		nameTexts = new ArrayList<IEventBInputText>();
		contents = new ArrayList<String>();
		contentTexts = new ArrayList<IEventBInputText>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "&Add", false);

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
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		toolkit.setBorderStyle(SWT.BORDER);

		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, message);
		label.setLayoutData(new GridData());

		IEventBInputText text = new EventBText(toolkit.createText(body,
				defaultNamePrefix + (counter++)));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.getTextWidget().setLayoutData(gd);
		nameTexts.add(text);

		EventBMath textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath);
		textMath.getTextWidget().setFocus();

		label = toolkit.createLabel(body, message);

		text = new EventBText(toolkit.createText(body, defaultNamePrefix
				+ (counter++)));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.getTextWidget().setLayoutData(gd);
		nameTexts.add(text);

		textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath);

		label = toolkit.createLabel(body, message);
		label.setText(message);

		text = new EventBText(toolkit.createText(body, defaultNamePrefix
				+ (counter++)));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		text.getTextWidget().setLayoutData(gd);
		nameTexts.add(text);

		textMath = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		textMath.getTextWidget().setLayoutData(gd);
		contentTexts.add(textMath);

		composite.pack();

		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			names = new HashSet<String>();
			contents = new HashSet<String>();
		} else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(scrolledForm.getBody(), message);
			label.setLayoutData(new GridData());

			IEventBInputText text = new EventBText(toolkit.createText(
					scrolledForm.getBody(), defaultNamePrefix + (counter++)));
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			text.getTextWidget().setLayoutData(gd);
			nameTexts.add(text);

			text = new EventBText(toolkit
					.createText(scrolledForm.getBody(), ""));
			text.getTextWidget().setLayoutData(gd);
			contentTexts.add(text);

			toolkit.paintBordersFor(scrolledForm.getBody());
			scrolledForm.reflow(true);
		} else if (buttonId == IDialogConstants.OK_ID) {
			names = new ArrayList<String>();
			contents = new ArrayList<String>();
			Object[] namesList = nameTexts.toArray();
			Object[] contentsList = contentTexts.toArray();
			for (int i = 0; i < namesList.length; i++) {
				IEventBInputText contentText = (IEventBInputText) contentsList[i];
				String text = contentText.getTextWidget().getText();
				if (!text.equals("")) {
					IEventBInputText nameText = (IEventBInputText) namesList[i];
					names.add(nameText.getTextWidget().getText());
					contents.add(text);
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the list of new names.
	 * <p>
	 * 
	 * @return The list of new names (strings)
	 */
	public String[] getNewNames() {
		return (String[]) names.toArray(new String[names.size()]);
	}

	/**
	 * Get the list of new contents.
	 * <p>
	 * 
	 * @return The list of new contents (strings)
	 */
	public String[] getNewContents() {
		return (String[]) contents.toArray(new String[contents.size()]);
	}

	@Override
	public boolean close() {
		for (IEventBInputText text : nameTexts) text.dispose();
		for (IEventBInputText text : contentTexts) text.dispose();
		return super.close();
	}

	
	
}
