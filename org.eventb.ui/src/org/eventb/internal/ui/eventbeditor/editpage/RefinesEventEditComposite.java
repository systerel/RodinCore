package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.RodinDBException;

public class RefinesEventEditComposite extends CComboEditComposite {

	public String getValue() throws RodinDBException {
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		return refinesEvent.getAbstractEventLabel();
	}

	@Override
	public void refresh() {
		initialise();
		internalPack();
	}

	public void setValue() {
		assert element instanceof IRefinesEvent;
		if (combo == null)
			return;
		
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		String str = combo.getText();

		String value;
		try {
			value = getValue();
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(str)) {
			try {
				refinesEvent.setAbstractEventLabel(str,
						new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			combo.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					setValue();
				}

			});
		}
		combo.removeAll();
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		IEvent event = (IEvent) refinesEvent.getParent();
		IMachineFile file = (IMachineFile) event.getParent();
		try {
			IRefinesMachine[] refinesClauses = file.getRefinesClauses();
			if (refinesClauses.length == 1) {
				IRefinesMachine refinesMachine = refinesClauses[0];
				IMachineFile abstractMachine = refinesMachine
						.getAbstractMachine();
				if (abstractMachine.exists()) {
					IEvent[] events = abstractMachine.getEvents();
					for (IEvent absEvent : events) {
						combo.add(absEvent.getLabel());
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		combo.setText(value);
	}

	@Override
	public void setDefaultValue() {
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		IEvent event = (IEvent) refinesEvent.getParent();
		String label = "abstract_event";
		try {
			label = event.getLabel();
		} catch (RodinDBException e1) {
			// Do nothing
		}
		
		try {
			refinesEvent.setAbstractEventLabel(label,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setDefaultValue();
	}

}
