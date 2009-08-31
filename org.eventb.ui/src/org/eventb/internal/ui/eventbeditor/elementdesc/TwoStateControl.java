/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class TwoStateControl {

	protected Button button;

	final String selectedValue;
	final String notSelectedValue;

	protected IInternalElement element;

	protected final AbstractBooleanManipulation manipulation;

	public TwoStateControl(AbstractBooleanManipulation manipulation) {
		this.manipulation = manipulation;
		selectedValue = manipulation.getText(true);
		notSelectedValue = manipulation.getText(false);
	}

	public void refresh() {
		final String value = getValue();
		button.setSelection((value.equals(selectedValue)));
		button.setText(value);
	}

	private String getValue() {
		try {
			if (!element.exists() || !manipulation.hasValue(element, null))
				return notSelectedValue;
			return manipulation.getValue(element, null);
		} catch (RodinDBException e) {
			e.printStackTrace();
			return notSelectedValue;
		}
	}

	public void createCheckBox(Composite composite) {
		createButton(composite, SWT.CHECK);
	}

	public void createToggle(Composite composite) {
		createButton(composite, SWT.TOGGLE);
		button.setText(selectedValue);
	}

	private void createButton(Composite composite, int style) {
		if (button != null)
			return;
		button = new Button(composite, style);
		button.setToolTipText("Select to set " + selectedValue
				+ " or deselect to set " + notSelectedValue);

		button.addSelectionListener(new SelectionListener() {

			private String getText() {
				return (button.getSelection() ? selectedValue
						: notSelectedValue);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				UIUtils.setStringAttribute(element, manipulation, getText(),
						null);
			}
		});
	}

	public void setElement(IInternalElement element) {
		this.element = element;
	}

	public Button getButton() {
		return button;
	}
}
