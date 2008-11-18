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
 *     Systerel - increased index of label when add new input
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
import org.eclipse.swt.widgets.Text;
import org.eventb.core.ILabeledElement;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of element name with content.
 */
public class ElementNameContentInputDialog<T extends ILabeledElement> extends
		EventBInputDialog {

	private String prefix;

	private Collection<String> names;

	private Collection<String> contents;

	private Collection<IEventBInputText> nameTexts;

	private Collection<IEventBInputText> contentTexts;

	private String message;

	private String firstIndex;

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
	 * @param prefix
	 *            The default name prefix
	 * @param index
	 *            The start counter for the elements.
	 */
	public ElementNameContentInputDialog(Shell parentShell, String title,
			String message, 
			String prefix, String index) {
		super(parentShell, title);
		this.message = message;
		this.prefix = prefix;
		this.firstIndex = index;
		names = new ArrayList<String>();
		nameTexts = new ArrayList<IEventBInputText>();
		contents = new ArrayList<String>();
		contentTexts = new ArrayList<IEventBInputText>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
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
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, message);
		gd = new GridData();
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		Text focusText = null;

		for (int i = 0; i < 3; i++) {
			final int index = Integer.parseInt(firstIndex) + i;
			final IEventBInputText inputText = getNameInputText(toolkit,
					scrolledForm.getBody(), prefix + index);
			final EventBMath textMath = getContentInputText(toolkit,
					scrolledForm.getBody());

			nameTexts.add(inputText);
			contentTexts.add(textMath);
			
			if (focusText == null)
				focusText = textMath.getTextWidget();
		}

		focusText.selectAll();
		focusText.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			names = new HashSet<String>();
			contents = new HashSet<String>();
		} else if (buttonId == IDialogConstants.YES_ID) {
			// button "more"
			final int index = Integer.parseInt(firstIndex) + nameTexts.size();
			final IEventBInputText inputText = getNameInputText(toolkit,
					scrolledForm.getBody(), prefix + index);
			final EventBMath textMath = getContentInputText(toolkit,
					scrolledForm.getBody());

			nameTexts.add(inputText);
			contentTexts.add(textMath);
			
			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			names = new ArrayList<String>();
			contents = new ArrayList<String>();
			Object[] namesList = nameTexts.toArray();
			Object[] contentsList = contentTexts.toArray();
			for (int i = 0; i < namesList.length; i++) {
				IEventBInputText contentText = (IEventBInputText) contentsList[i];
				Text textWidget = contentText.getTextWidget();
				String text = textWidget.getText();
				if (dirtyTexts.contains(textWidget)) {
					IEventBInputText nameText = (IEventBInputText) namesList[i];
					names.add(nameText.getTextWidget().getText());
					contents.add(Text2EventBMathTranslator.translate(text));
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
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Get the list of new contents.
	 * <p>
	 * 
	 * @return The list of new contents (strings)
	 */
	public String[] getNewContents() {
		return contents.toArray(new String[contents.size()]);
	}

	@Override
	public boolean close() {
		for (IEventBInputText text : nameTexts)
			text.dispose();
		for (IEventBInputText text : contentTexts)
			text.dispose();
		return super.close();
	}

}
