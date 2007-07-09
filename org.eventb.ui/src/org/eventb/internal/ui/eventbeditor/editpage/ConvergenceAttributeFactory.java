package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class ConvergenceAttributeFactory implements IAttributeFactory {

	public void createDefaulAttribute(IEventBEditor<?> editor,
			IInternalElement element, IProgressMonitor monitor)
			throws RodinDBException {
		if (!(element instanceof IConvergenceElement)) {
			return;
		}
		IConvergenceElement cElement = (IConvergenceElement) element;
		cElement.setConvergence(IConvergenceElement.Convergence.ORDINARY,
				monitor);
	}

}
