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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type invariant and
 *         initilisation.
 */
public class NewEnumeratedSetInputDialog extends EventBInputDialog {

	private String defaultName;

	private String name;

	private Collection<String> elements;

	private IEventBInputText nameText;

	private Collection<IEventBInputText> elementTexts;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param defaultName
	 *            the default variable name
	 * @param defaultInvariantName
	 *            the default invariant name
	 */
	public NewEnumeratedSetInputDialog(Shell parentShell,
			String title, String defaultName) {
		super(parentShell, title);
		this.defaultName = defaultName;
		elementTexts = new ArrayList<IEventBInputText>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "&More Element", true);

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
	protected void createContents() {
		Composite body = scrolledForm.getBody();

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		toolkit.createLabel(body, "Name");

		nameText = new EventBText(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		nameText.getTextWidget().setLayoutData(gd);
		nameText.getTextWidget().addModifyListener(new DirtyStateListener());

		elementTexts = new ArrayList<IEventBInputText>();

		for (int i = 0; i < 3; i++) {
			toolkit.createLabel(body, "Element");

			IEventBInputText elementText = new EventBMath(toolkit.createText(
					body, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.widthHint = 150;
			elementText.getTextWidget().setLayoutData(gd);
			elementText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			elementTexts.add(elementText);
		}
		nameText.getTextWidget().setText(defaultName);
		scrolledForm.reflow(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			elements = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Composite body = scrolledForm.getBody();
			Label label = toolkit.createLabel(body, "Element");
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			label.setLayoutData(gd);

			IEventBInputText elementText = new EventBMath(toolkit.createText(
					body, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			elementText.getTextWidget().setLayoutData(gd);
			elementText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			elementTexts.add(elementText);

			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			if (dirtyTexts.contains(nameText.getTextWidget()))
				name = nameText.getTextWidget().getText();
			else
				name = null;
			elements = new ArrayList<String>();
			for (IEventBInputText text : elementTexts) {
				if (dirtyTexts.contains(text.getTextWidget())) {
					String element = text.getTextWidget().getText();
					elements.add(element);
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the invariant name.
	 * <p>
	 * 
	 * @return the invariant name as input by the user
	 */
	public Collection<String> getElements() {
		return elements;
	}

	@Override
	public boolean close() {
		nameText.dispose();
		for (IEventBInputText text : elementTexts) {
			text.dispose();
		}
		return super.close();
	}

}
