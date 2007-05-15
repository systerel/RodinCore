package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.RodinDBException;

public abstract class CComboEditComposite extends DefaultEditComposite implements
		IEditComposite {

	protected final String UNDEFINED = "----- UNDEFINED -----";
	
	protected CCombo combo;
	protected Button undefinedButton;

	public void refresh() {
		initialise();
		internalPack();
	}

	@Override
	public void initialise() {
		try {
			String value = getValue();
			displayValue(value);
		} catch (RodinDBException e) {
			setUndefinedValue();
		}
	}

	public void displayValue(String value) {
		if (combo == null) {
			if (undefinedButton != null) {
				undefinedButton.dispose();
				undefinedButton = null;
			}
			
			combo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					setValue();
				}

			});
		}
		combo.setText(value);
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

	public void setUndefinedValue() {
		FormToolkit toolkit = this.getFormToolkit();
		if (undefinedButton != null)
			return;
		
		if (combo != null)
			combo.dispose();
		
		undefinedButton = toolkit.createButton(composite, "UNDEFINED", SWT.PUSH);
		undefinedButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				setDefaultValue();
			}
			
		});
	}

	@Override
	public void setDefaultValue() {
		if (combo != null)
			combo.setFocus();
	}

}
