package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.custom.CCombo;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.RodinDBException;

public class RefinesEventEditComposite extends CComboEditComposite {

	@Override
	public String getValue() {
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		try {
			return refinesEvent.getAbstractEventLabel();
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
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

	@Override
	public void setValue() {
		assert element instanceof IRefinesEvent;
		CCombo combo = (CCombo) control;
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		String str = combo.getText();

		if (!getValue().equals(str)) {
			try {
				refinesEvent.setAbstractEventLabel(str,
						new NullProgressMonitor());
			} catch (RodinDBException exc) {
				// TODO Auto-generated catch block
				exc.printStackTrace();
			}

		}
	}

}
