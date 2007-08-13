/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

public class CComboEditComposite extends AbstractEditComposite {

	protected final String UNDEFINED = "--undef--";
	
	protected CCombo combo;
	protected Button undefinedButton;

	public CComboEditComposite(IAttributeUISpec uiSpec) {
		super(uiSpec);
	}
	
	@Override
	public void initialise() {
		try {
			String value = uiSpec.getAttributeFactory().getValue(element,
					new NullProgressMonitor());
			createCombo();
			combo.setText(value);
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	@Override
	public void setSelected(boolean selection) {
		Control control = combo == null ? undefinedButton : combo;
		if (selection)
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(control.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	/**
	 * Set undefined value for the element.
	 */
	private void setUndefinedValue() {
		if (combo == null)
			createCombo();
		
		combo.setText(UNDEFINED);
	}

	private void createCombo() {
		if (combo != null)
			combo.removeAll();
		else {
			combo = new CCombo(composite, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					if (combo.getText().equals(UNDEFINED)) {
						try {
							uiSpec.getAttributeFactory().removeAttribute(element,
									new NullProgressMonitor());
						} catch (RodinDBException e1) {
							EventBUIExceptionHandler.handleRemoveAttribteException(e1);
						}
					} else {
						try {
							uiSpec.getAttributeFactory().setValue(element, combo
									.getText(), new NullProgressMonitor());
						} catch (RodinDBException exception) {
							EventBUIExceptionHandler
									.handleSetAttributeException(exception);
						}
					}
				}
			});
			this.getFormToolkit().paintBordersFor(composite);
		}
		
		combo.add(UNDEFINED);
		try {
			String[] values;
			values = uiSpec.getAttributeFactory().getPossibleValues(element,
					new NullProgressMonitor());
			for (String value : values) {
				combo.add(value);
			}
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(e,
					UserAwareness.IGNORE);
		}
	}

	public void setDefaultValue(IEventBEditor<?> editor) {
		try {
			uiSpec.getAttributeFactory().setDefaultValue(editor, element,
					new NullProgressMonitor());
			if (combo != null)
				combo.setFocus();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

}
