package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.CCombo;
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
		CCombo combo = (CCombo) control;
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
		super.refresh();
	}

	public void setValue() {
		assert element instanceof IRefinesEvent;
		CCombo combo = (CCombo) control;
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
	public void initialise() {
		CCombo combo = (CCombo) control;
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
		super.initialise();
	}

	@Override
	public void setDefaultValue() {
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		IEvent event = (IEvent) refinesEvent.getParent();
		try {
			refinesEvent.setAbstractEventLabel(event.getLabel(),
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setDefaultValue();
	}

}
