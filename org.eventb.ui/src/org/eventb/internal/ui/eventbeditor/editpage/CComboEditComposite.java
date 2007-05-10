package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class CComboEditComposite extends DefaultEditComposite implements
		IEditComposite {

	@Override
	public void createComposite(FormToolkit toolkit, Composite parent) {
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

	@Override
	public void refresh() {
		CCombo combo = (CCombo) control;
		if (!(combo.getText().equals(getValue()))) {
			combo.setText(getValue());
			internalPack();
		}
	}

	@Override
	public void initialise() {
		CCombo combo = (CCombo) control;
		combo.setText(getValue());
	}

	public void setSelected(boolean selection) {
		CCombo combo = (CCombo) control;
		if (selection)
			combo.setBackground(combo.getDisplay().getSystemColor(
					SWT.COLOR_GRAY));
		else {
			combo.setBackground(combo.getDisplay().getSystemColor(
					SWT.COLOR_WHITE));
		}
	}

}
