package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eventb.core.IEvent;
import org.rodinp.core.RodinDBException;

public class InheritedEditComposite extends CComboEditComposite {

	private final String TRUE = "true";
	
	private final String FALSE = "false";
	
	public String getValue() throws RodinDBException {
		IEvent event = (IEvent) element;
		return event.isInherited() ? TRUE : FALSE;
	}

	@Override
	public void refresh() {
		initialise();
		internalPack();
		super.refresh();
	}

	public void setValue() {
		assert element instanceof IEvent;
		if (combo == null)
			return;
		
		IEvent event = (IEvent) element;
		String str = combo.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			try {
				event.setInherited(str.equalsIgnoreCase(TRUE),
						new NullProgressMonitor());
			} catch (RodinDBException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}

		}
	}

	@Override
	public void displayValue(String value) {
		if (combo == null) {
			if (undefinedButton != null) {
				undefinedButton.dispose();
				undefinedButton = null;
			}
			
			combo = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
			combo.add(TRUE);
			combo.add(FALSE);
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
	public void setDefaultValue() {
		IEvent event = (IEvent) element;
		try {
			event.setInherited(false,
					new NullProgressMonitor());
		} catch (RodinDBException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
		super.setDefaultValue();
	}

}
