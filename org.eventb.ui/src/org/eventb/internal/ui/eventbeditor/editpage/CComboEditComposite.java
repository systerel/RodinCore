package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.RodinDBException;

public abstract class CComboEditComposite extends DefaultEditComposite implements
		IEditComposite {

	protected final String UNDEFINED = "----- UNDEFINED -----";
	
	@Override
	public void createMainComposite(FormToolkit toolkit, Composite parent) {
		final CCombo combo = new CCombo(parent, SWT.FLAT | SWT.READ_ONLY);
		setControl(combo);
		initialise();
		combo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				setValue();
			}

		});
	}

	public void refresh() {
		CCombo combo = (CCombo) control;
		String value;
		try {
			value = getValue();
			if (!(combo.getText().equals(value))) {
				combo.setText(value);
				internalPack();
			}
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	public void initialise() {
		CCombo combo = (CCombo) control;
		try {
			combo.setText(getValue());
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	@Override
	public void setSelected(boolean selection) {
		CCombo combo = (CCombo) control;
		if (selection)
			combo.setBackground(combo.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			combo.setBackground(combo.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
		super.setSelected(selection);
	}

	public void setUndefinedValue() {
		final CCombo combo = (CCombo) control;
		combo.removeAll();
		combo.add(UNDEFINED);
		combo.setText(UNDEFINED);
		combo.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				combo.removeFocusListener(this);
				setDefaultValue();
			}

			public void focusLost(FocusEvent e) {
				// Do nothing
			}
			
		});
	}

	@Override
	public void setDefaultValue() {
		final CCombo combo = (CCombo) control;
		combo.removeAll();
		initialise();
	}

}
