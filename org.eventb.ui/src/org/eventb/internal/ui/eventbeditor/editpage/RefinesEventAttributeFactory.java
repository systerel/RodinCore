package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class RefinesEventAttributeFactory implements IAttributeFactory {

	public void createDefaulAttribute(IEventBEditor editor,
			IInternalElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IRefinesEvent)) {
			return;
		}
		IRefinesEvent rElement = (IRefinesEvent) element;
		IEvent event = (IEvent) rElement.getParent();
		rElement.setAbstractEventLabel(event.getLabel(), monitor);
	}

}
