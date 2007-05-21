package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.RodinDBException;

public class DefaultAttributeEditor implements IAttributeEditor {

	public String getAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Default return null
		return null;
	}

	public String[] getPossibleValues(IAttributedElement element,
			IProgressMonitor monitor) {
		// Default return null
		return null;
	}

	public void removeAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Default do nothing
	}

	public void setAttribute(IAttributedElement element, String newValue,
			IProgressMonitor monitor) throws RodinDBException {
		// Default do nothing
	}

	public void setDefaultAttribute(IAttributedElement element,
			IProgressMonitor monitor) throws RodinDBException {
		// Default do nothing
	}

}
