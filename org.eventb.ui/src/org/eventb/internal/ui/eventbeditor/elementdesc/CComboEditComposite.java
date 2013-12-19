/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - added history support
 *     Systerel - made IAttributeFactory generic
 *     Systerel - removed MouseWheel Listener of CCombo
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *     Systerel - update combo list on focus gain
 *     Systerel - introduced read only elements
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.elementdesc;

import static org.eventb.internal.ui.UIUtils.COMBO_VALUE_UNDEFINED;
import static org.eventb.internal.ui.UIUtils.disableMouseWheel;
import static org.eventb.internal.ui.UIUtils.resetCComboValues;
import static org.eventb.internal.ui.UIUtils.setStringAttribute;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.markers.MarkerUIRegistry;

public class CComboEditComposite extends AbstractEditComposite {
	
	protected CCombo combo;
	protected Button undefinedButton;
	protected final boolean required;
	
	public CComboEditComposite(ComboDesc attrDesc) {
		super(attrDesc);
		this.required = attrDesc.isRequired();
	}
	
	@Override
	public void initialise(boolean refreshMarkers) {
		String value = getValue();
		createCombo();
		combo.setText(value);
		if (refreshMarkers)
			displayMarkers();
	}

	private String getValue() {
		try {
			if (!manipulation.hasValue(element, null))
				return COMBO_VALUE_UNDEFINED;
			return manipulation.getValue(element, null);
		} catch (CoreException e) {
			e.printStackTrace();
			return COMBO_VALUE_UNDEFINED;
		}
	}

	private void displayMarkers() {
		try {
			int maxSeverity = MarkerUIRegistry.getDefault()
				.getMaxMarkerSeverity(element, attrDesc.getAttributeType());
			if (maxSeverity == IMarker.SEVERITY_ERROR) {
				combo.setBackground(RED);
				combo.setForeground(YELLOW);
				return;
			}
			else if (maxSeverity == IMarker.SEVERITY_WARNING) {
				combo.setBackground(YELLOW);
				combo.setForeground(RED);
				return;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		combo.setBackground(WHITE);
		combo.setForeground(BLACK);
	}
	@Override
	public void setSelected(boolean selection) {
		Control control = combo == null ? undefinedButton : combo;
		if (selection)
			control.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_GRAY));
		else {
			control.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	private void createCombo() {
		if (combo == null) {
			combo = new CCombo(composite, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);

			combo.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					resetCComboValues(combo, manipulation, element, required);
				}
			});
			
			// to fix bug 2417413
			disableMouseWheel(combo);
			
			combo.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					commit();
					resetCComboValues(combo, manipulation, element, required);
				}
			});
			this.getFormToolkit().paintBordersFor(composite);
		}
	}

	private String getText() {
		final String text = combo.getText();
		return (text.equals(COMBO_VALUE_UNDEFINED)) ? null : text;
	}

	protected void commit() {
		setStringAttribute(element, attrDesc.getManipulation(), getText(), null);
	}
	
	@Override
	public void edit(int charStart, int charEnd) {
		combo.setFocus();
		if (charStart != -1 && charEnd != -1)
			combo.setSelection(new Point(charStart, charEnd));
		else {
			String text = combo.getText();
			combo.setSelection(new Point(0, text.length()));
		}
			
		FormToolkit.ensureVisible(combo);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		if (combo != null) {
			combo.setEnabled(!readOnly);
		}
	}

}
