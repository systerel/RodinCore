package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public class Remove implements IEditAction {

	public void run(IEventBEditor editor, IInternalParent parent,
			IInternalElement element) throws CoreException {
		element.delete(true, new NullProgressMonitor());
	}

	public boolean isApplicable(IInternalParent parent, IInternalElement element)
			throws RodinDBException {
		if (element == null)
			return false;

		return true;
	}

}
