/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - added checkAndSetFieldValues()
 *     Systerel - refactored after wizard refactoring
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new enumerated set.
 */
public class NewEnumeratedSetDialog extends EventBDialog {

	private String defaultName;

	private String name;

	private final List<String> elements = new ArrayList<String>();

	private IEventBInputText nameText;

	private Collection<IEventBInputText> elementTexts;

	/**
	 * Constructor.
	 * 
	 * @param root
	 *            the root element to which enumerated sets will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewEnumeratedSetDialog(IContextRoot root, Shell parentShell, String title) {
		super(parentShell, root, title);
		this.defaultName = UIUtils.getFreeElementIdentifier(root,
				ICarrierSet.ELEMENT_TYPE);
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

		nameText = createInput("Identifier", ICarrierSet.ELEMENT_TYPE,
				LABEL_ATTRIBUTE);
		for (int i = 0; i < 3; i++) {
			createElement();
		}

		setText(nameText, defaultName);
		select(nameText);
		scrolledForm.reflow(true);
	}
	
	private IEventBInputText createInput(String label,
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		createLabel(getBody(), label);
		final IEventBInputText input = createBText(getBody(), EMPTY, 150, true);
		addProposalAdapter(elementType, attributeType, input);
		return input;
	}
	
	private IEventBInputText createElement() {
		final IEventBInputText text = createInput("Element",
				IAxiom.ELEMENT_TYPE, PREDICATE_ATTRIBUTE);
		elementTexts.add(text);
		return text;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			name = null;
			elements.clear();
		} else if (buttonId == MORE_ELEMENT_ID) {
			final IEventBInputText text = createElement();
			updateSize();
			select(text);
		} else if (buttonId == OK_ID) {
			if (!checkAndSetFieldValues()) {
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	
	private boolean checkAndSetFieldValues() {
		name = nameText.getTextWidget().getText();
		elements.clear();
		fillResult(elementTexts, elements);
		
		final List<String> allNames = new ArrayList<String>(elements);
		allNames.add(name);
		
		if (!checkNewIdentifiers(allNames, true, root.getFormulaFactory())) {
			name = null;
			elements.clear();
			return false;
		}
		return true;
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
