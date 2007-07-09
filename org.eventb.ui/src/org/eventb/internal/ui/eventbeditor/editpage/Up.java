package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public class Up implements IEditAction {

	public void run(IEventBEditor<?> editor, final IInternalParent parent,
			final IInternalElement element,
			IInternalElementType<IInternalElement> type) throws CoreException {

		// Trying to find the previous element
		IInternalElement[] children = parent.getChildrenOfType(element
				.getElementType());
		IInternalElement prev = null;
		for (IInternalElement child : children) {
			if (child.equals(element))
				break;
			else {
				prev = child;
			}
		}

		element.move(parent, prev, null, false, new NullProgressMonitor());
	}

	public boolean isApplicable(IInternalParent parent,
			IInternalElement element,
			IInternalElementType<IInternalElement> type)
			throws RodinDBException {
		if (element == null)
			return false;

		IInternalElement[] children = parent.getChildrenOfType(element
				.getElementType());

		if (element.equals(children[0]))
			return false;

		return true;
	}

}
