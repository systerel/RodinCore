package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class RefinesEventSection extends CComboSection {

	@Override
	String getLabel() {
		return "Ref. Evt.";
	}

	@Override
	String getText() throws RodinDBException {
		IRefinesEvent rElement = (IRefinesEvent) element;
		return rElement.getAbstractEventLabel();
	}

	@Override
	void setData() {
		IMachineFile machine = (IMachineFile) editor.getRodinInput();
		final IEvent[] events;
		try {
			events = machine.getEvents();
		} catch (RodinDBException e) {
			UIUtils.log(e, "when listing the events of " + machine.getBareName());
			return;
		}
		for (IEvent event : events) {
			try {
				comboWidget.add(event.getLabel());
			} catch (RodinDBException e) {
				UIUtils.log(e, "when getting the label of " + event.getElementName());
				return;
			}
		}
	}

	@Override
	void setText(String text) throws RodinDBException {
		IRefinesEvent rElement = (IRefinesEvent) element;
		if (!rElement.getAbstractEventLabel().equals(text)) {
			rElement.setAbstractEventLabel(text, new NullProgressMonitor());
		}
	}

}
