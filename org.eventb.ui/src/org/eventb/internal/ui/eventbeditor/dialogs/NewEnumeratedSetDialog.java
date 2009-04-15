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
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new enumerated set.
 */
public class NewEnumeratedSetDialog extends EventBDialog {

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
	 *            the default set name
	 */
	public NewEnumeratedSetDialog(Shell parentShell,
			String title, String defaultName) {
		super(parentShell, title);
		this.defaultName = defaultName;
		elementTexts = new ArrayList<IEventBInputText>();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, MORE_ELEMENT_ID, MORE_ELEMENT_LABEL);
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	@Override
	protected void createContents() {
		elementTexts = new ArrayList<IEventBInputText>();

		setDebugBackgroundColor();
		setFormGridLayout(getBody(), 2);
		setFormGridData();

		nameText = createInput("Identifier");
		for (int i = 0; i < 3; i++) {
			createElement();
		}

		setText(nameText, defaultName);
		select(nameText);
		scrolledForm.reflow(true);
	}
	
	private IEventBInputText createInput(String label) {
		createLabel(getBody(), label);
		return createBText(getBody(), EMPTY, 150, true);
	}
	
	private void createElement() {
		final IEventBInputText text = createInput("Element");
		elementTexts.add(text);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			name = null;
			elements = new HashSet<String>();
		} else if (buttonId == MORE_ELEMENT_ID) {
			createElement();
			updateSize();
		} else if (buttonId == OK_ID) {
			name = nameText.getTextWidget().getText();
			elements = new ArrayList<String>();
			fillResult(elementTexts, elements);
		}
		super.buttonPressed(buttonId);
	}
	
	/**
	 * Get the set name.
	 * <p>
	 * 
	 * @return the set name entered by the user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the elements of the enumerated set.
	 * 
	 * @return the elements entered by the user
	 */
	public String[] getElements() {
		return elements.toArray(new String[elements.size()]);
	}

	@Override
	public boolean close() {
		nameText.dispose();
		dispose(elementTexts);
		return super.close();
	}

}
