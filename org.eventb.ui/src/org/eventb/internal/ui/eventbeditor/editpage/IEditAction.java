package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public interface IEditAction {

	void run(IEventBEditor editor, IInternalParent parent,
			IInternalElement element) throws CoreException;

	boolean isApplicable(IInternalParent parent, IInternalElement element)
			throws RodinDBException;
}
