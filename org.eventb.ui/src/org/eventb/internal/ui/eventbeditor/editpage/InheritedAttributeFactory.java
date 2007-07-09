package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class InheritedAttributeFactory implements IAttributeFactory {

	public void createDefaulAttribute(IEventBEditor<?> editor,
			IInternalElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IEvent)) {
			return;
		}
		IEvent event = (IEvent) element;
		event.setInherited(false, monitor);
	}

}
