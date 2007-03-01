package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public interface IEditAction {

	void run(IEventBEditor editor, IInternalParent parent,
			IInternalElement element, IInternalElementType<IInternalElement> type) throws CoreException;

	boolean isApplicable(IInternalParent parent, IInternalElement element,
			IInternalElementType<IInternalElement> type) throws RodinDBException;
}
