package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public interface IAttributeFactory {

	void createDefaulAttribute(IEventBEditor<?> editor, IInternalElement element,
			IProgressMonitor monitor) throws RodinDBException;

}
