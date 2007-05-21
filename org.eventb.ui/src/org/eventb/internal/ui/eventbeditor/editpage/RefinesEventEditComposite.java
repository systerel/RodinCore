package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class RefinesEventEditComposite extends DefaultAttributeEditor implements
		IAttributeEditor {

	@Override
	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IRefinesEvent;
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		return refinesEvent.getAbstractEventLabel();
	}

	@Override
	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		assert element instanceof IRefinesEvent;
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		String value;
		try {
			value = getAttribute(element, monitor);
		} catch (RodinDBException e) {
			value = null;
		}
		if (value == null || !value.equals(newValue)) {
			refinesEvent.setAbstractEventLabel(newValue,
					new NullProgressMonitor());
		}
	}

	@Override
	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		IRefinesEvent refinesEvent = (IRefinesEvent) element;
		IEvent event = (IEvent) refinesEvent.getParent();
		String label = "abstract_event";
		try {
			label = event.getLabel();
		} catch (RodinDBException e1) {
			// Do nothing
		}

		refinesEvent.setAbstractEventLabel(label, monitor);
	}

	@Override
	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		List<String> results = new ArrayList<String>();

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
						results.add(absEvent.getLabel());
					}
				}
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results.toArray(new String[results.size()]);
	}

	@Override
	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		element.removeAttribute(EventBAttributes.TARGET_ATTRIBUTE, monitor);
	}

}
