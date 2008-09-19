package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesEventAbstractEventLabelAttributeFactory;
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
		IMachineFile machine = (IMachineFile) element.getRodinFile();
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
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element,
				new RefinesEventAbstractEventLabelAttributeFactory(), text,
				monitor);
	}

}
